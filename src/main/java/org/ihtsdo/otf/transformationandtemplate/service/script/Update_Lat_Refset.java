package org.ihtsdo.otf.transformationandtemplate.service.script;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.StringUtils;
import org.ihtsdo.otf.exception.TermServerScriptException;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.RefsetMemberPojo;
import org.ihtsdo.otf.transformationandtemplate.domain.Concept;
import org.ihtsdo.otf.transformationandtemplate.service.client.SnowstormClient;
import org.ihtsdo.otf.utils.SnomedUtils;
import org.snomed.otf.scheduler.domain.*;
import org.snomed.otf.script.dao.ReportManager;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * This script will add and remove Concepts to/from 723264001 |Lateralizable body structure reference set|.
 */
public class Update_Lat_Refset extends AuthoringPlatformScript implements JobClass {

	/**
	 * Reference set identifier.
	 */
	private final String LAT_REFSETID = "723264001";

	/**
	 * Key for accessing request parameter to control whether the script should
	 * process all Concepts or just recently created/modified.
	 */
	private final String paramLegacy = "legacy";

	/**
	 * Key for accessing request parameter to control which branch to process.
	 */
	private final String paramBranchPath = "branch";

	/**
	 * Key for accessing request parameter to control whether the script should
	 * run in dry mode, i.e. only produce the report and not to modify content.
	 */
	private final String paramDryRun = "dry";

	/**
	 * Counter for how many rows have been written to the Google Sheet.
	 */
	private int writes = 0;

	/**
	 * Counter for how many Concepts have been added to the reference set.
	 */
	private int added = 0;

	/**
	 * Counter for how many Concepts have been removed from the reference set.
	 */
	private int removed = 0;

	/**
	 * Counter for how many Concepts have been duplicated within the reference set.
	 */
	private int duplicated = 0;

	/**
	 * A copy of the data in the spreadsheet which is analysed for duplicates.
	 */
	private final Multimap<String, List<String>> multiMapOfAllConceptsAddedToSpreadsheet = HashMultimap.create();

	public Update_Lat_Refset(JobRun jobRun, ScriptManager mgr) {
		super(jobRun, mgr);
	}

	@Override
	public Job getJob() {
		JobParameters jobParameters = new JobParameters()
				.add(paramLegacy)
				.withType(JobParameter.Type.BOOLEAN)
				.withDefaultValue(false)
				.withDescription("Change script to process all active Concepts, instead of only new/modified Concepts.")
				.add(paramDryRun)
				.withType(JobParameter.Type.BOOLEAN)
				.withDefaultValue(false)
				.withDescription("Change script to run in dry mode, i.e. only produce report and not modify content.")
				.add(paramBranchPath)
				.withType(JobParameter.Type.STRING)
				.withDescription("Change which branch to process. Note, a new task will be created which references the report produced.")
				.build();
		return new Job()
				.withCategory(new JobCategory(JobType.BATCH_JOB, JobCategory.REFSET_UPDATE))
				.withName("Update Lateralizable Refset")
				.withDescription("This job adds and removes relevant Concepts to/from the Lateralizable RefSet. Please note this RefSet is only for Lateralizable " +
						"Concepts, and not Lateralized Concepts. Lateralizable is when a Concept can have a side, i.e. an arm " +
						"can be a left arm or a right arm. Lateralized, however, is when a Concept has a side, i.e. a left arm is on the left side.")
				.withProductionStatus(Job.ProductionStatus.PROD_READY)
				.withParameters(jobParameters)
				.build();
	}

	@Override
	protected String createGoogleSheet() throws TermServerScriptException {
		reportManager = ReportManager.create(this, reportConfiguration);
		reportManager.setTabNames(new String[] {"Process", "Duplicates", "Summary"});
		String[] columnHeadings = new String[] {"Task,SCTID,FSN,Semtag,Severity,Action,Info,Detail1, Detail2, ",
												"Task,SCTID,FSN,Semtag,Severity,Action,Info,Detail1, Detail2, ",
												"Issue, Count"};
		reportManager.initialiseReportFiles(columnHeadings);
		return reportManager.getUrl();
	}

	@Override
	public void runJob() throws TermServerScriptException {
		String branchPath = jobRun.getParamValue(paramBranchPath, task.getBranchPath());
		boolean legacy = jobRun.getParamBoolean(paramLegacy);
		boolean dryRun = jobRun.getParamBoolean(paramDryRun);

		info(String.format("Running with branchPath: %s legacy: %b dryRun: %b", branchPath, legacy, dryRun));
		percentageComplete(30);

		/*
		 * Remove Concept if:-
		 * 	- Concept has been lateralised
		 * 	- Concept no longer has prerequisite ancestor
		 * 	- Concept has been inactivated
		 * */
		removeConceptsFromRefSet(branchPath, legacy, dryRun);
		percentageComplete(60);

		/*
		 * Add Concept if:-
		 * 	- Concept has the laterality attribute with an appropriate value
		 * 	- Concept is within a certain hierarchy and also has an ancestor within the reference set
		 * */
		addConceptsToRefSet(branchPath, legacy, dryRun);
		percentageComplete(90);

		addDuplicateConceptsToRefSetDuplicatesTab();
		percentageComplete(95);

		/*
		 * Finish
		 * */
		if (writes == 0) {
			String message = String.format("Script has not found any data to action for Branch %s.", branchPath);
			info(message);
			report(TAB_0, message);
		}

		report(TAB_2, "Concepts added", this.added);
		report(TAB_2, "Concepts removed", this.removed);
		report(TAB_2, "Concepts duplicated", this.duplicated);

		flushFiles(true, false);
		percentageComplete(100);
	}

	private void removeConceptsFromRefSet(String branchPath, boolean legacy, boolean dryRun) throws TermServerScriptException {
		List<RefsetMemberPojo> rmToInactivate = new ArrayList<>();
		Map<String, Concept> cache = new HashMap<>();

		Set<Concept> relevantConceptsToRemove = getRelevantConceptsToRemove(branchPath, legacy);
		chunk(relevantConceptsToRemove, 20)
				.forEach(chunk -> {
					cache.putAll(mapByConceptId(chunk));
					List<RefsetMemberPojo> tscResponse = tsClient.findRefsetMemberByReferencedComponentId(branchPath, LAT_REFSETID, joinByConceptId(chunk), true);
					rmToInactivate.addAll(tscResponse);
				});

		if (rmToInactivate.isEmpty()) {
			return;
		}

		int x = 0;
		int size = rmToInactivate.size();
		for (RefsetMemberPojo rm : rmToInactivate) {
			x = x + 1;
			info(String.format("Processing %d / %d Concepts to remove from Reference Set.", x, size));

			String conceptId = rm.getConceptId();
			Concept concept = cache.get(conceptId);
			if (concept == null) {
				warn(String.format("Can't find Concept in cache with id %s. Associated RefSet Member(s) will not be actioned.", conceptId));
				continue;
			}

			ReportActionType reportActionType = removeRefsetMemberSilently(branchPath, rm, dryRun);
			doReportOrLog(concept, Severity.LOW, reportActionType, "Removed by removing ReferenceSetMember " + rm.getId());
		}
	}

	private Set<Concept> getRelevantConceptsToRemove(String branchPath, boolean legacy) {
		// Concepts which have been lateralised
		String byLaterality = "(^ 723264001 AND << 91723000 |Anatomical structure (body structure)|) : (272741003 | Laterality (attribute) | = (7771000 |Left (qualifier value)| OR 24028007 |Right(qualifier value)| OR 51440002 |Right and left (qualifier value)|))";

		// Concepts which no longer have an appropriate prerequisite ancestor
		String byNoPrerequisiteAncestor = "^ 723264001 MINUS (^ 723264001 AND << 423857001)";

		Set<Concept> conceptsToRemove = new HashSet<>();
		if (legacy) {
			conceptsToRemove.addAll(getAllConceptsByECL(branchPath, byLaterality));
			conceptsToRemove.addAll(getAllConceptsByECL(branchPath, byNoPrerequisiteAncestor));
		} else {
			conceptsToRemove.addAll(tsClient.findNewConcepts(branchPath, byLaterality, null));
			conceptsToRemove.addAll(tsClient.findUpdatedConcepts(branchPath, true, null, null, byLaterality));

			conceptsToRemove.addAll(tsClient.findNewConcepts(branchPath, byNoPrerequisiteAncestor, null));
			conceptsToRemove.addAll(tsClient.findUpdatedConcepts(branchPath, true, null, null, byNoPrerequisiteAncestor));
		}

		conceptsToRemove.addAll(tsClient.findUpdatedConcepts(branchPath, false, null, null, null));

		return conceptsToRemove;
	}

	private List<Concept> getAllConceptsByECL(String branchPath, String ecl) {
		List<Concept> concepts = new ArrayList<>();
		SnowstormClient.ConceptPage page = tsClient.fetchConceptPageBlocking(branchPath, null, ecl, null, null);
		long totalExpected = page.getTotal();
		long totalReceived = page.getItems().size();

		if (totalExpected == 0) {
			return concepts;
		}

		while (totalReceived <= totalExpected) {
			concepts.addAll(new ArrayList<>(page.getItems()));

			// Get next page
			page = tsClient.fetchConceptPageBlocking(branchPath, null, ecl, null, page.getSearchAfter());
			totalReceived += page.getItems().size();
		}

		return concepts;
	}

	// Split collection into X smaller collections (for batch processing)
	private <T> Stream<List<T>> chunk(Set<T> set, int chunkSize) {
		if (chunkSize <= 0) {
			throw new IllegalArgumentException("length = " + chunkSize);
		}

		List<T> list = new ArrayList<>(set);
		int size = list.size();
		if (size <= 0) {
			return Stream.empty();
		}

		int fullChunks = (size - 1) / chunkSize;
		return IntStream.range(0, fullChunks + 1).mapToObj(n -> list.subList(n * chunkSize, n == fullChunks ? size : (n + 1) * chunkSize));
	}

	private Map<String, Concept> mapByConceptId(List<Concept> chunk) {
		Map<String, Concept> map = new HashMap<>();
		for (Concept concept : chunk) {
			map.put(concept.getId(), concept);
		}

		return map;
	}

	private String joinByConceptId(List<Concept> concepts) {
		return concepts.stream().map(Concept::getId).collect(Collectors.joining(","));
	}

	// Remove ReferenceSetMember without reporting.
	protected ReportActionType removeRefsetMemberSilently(String branchPath, RefsetMemberPojo refsetMemberPojo, boolean dryRun) throws TermServerScriptException {
		this.removed = this.removed + 1;
		if (isPublished(refsetMemberPojo)) {
			info("Inactivating " + refsetMemberPojo);
			refsetMemberPojo.setActive(false);

			if (!dryRun) {
				tsClient.updateRefsetMember(branchPath, refsetMemberPojo);
			}

			return ReportActionType.REFSET_MEMBER_INACTIVATED;
		} else {
			info("Deleting " + refsetMemberPojo);

			if (!dryRun) {
				tsClient.deleteRefsetMember(branchPath, refsetMemberPojo);
			}

			return ReportActionType.REFSET_MEMBER_DELETED;
		}
	}

	protected boolean isPublished(RefsetMemberPojo refsetMemberPojo) {
		if (refsetMemberPojo == null) {
			return false;
		}

		return !StringUtils.isEmpty(refsetMemberPojo.getReleasedEffectiveTime());
	}

	private void doReportOrLog(Concept concept, Severity severity, ReportActionType reportActionType, String details) throws TermServerScriptException {
		String semTag = null;

		try {
			semTag = SnomedUtils.deconstructFSN(concept.getFsnTerm(), true)[1];
		} catch (Exception e) {
			debug("FSN related exception while trying to report for " + concept.getConceptId() + ": " + e);
		}

		multiMapOfAllConceptsAddedToSpreadsheet.put(concept.getConceptId(), Lists.newArrayList(task.getKey(), concept.getConceptId(), concept.getFsnTerm(), semTag, severity.name(), reportActionType.name(), details));

		boolean success = report(concept, severity, reportActionType, details);
		if (!success) {
			warn(String.format("Failed to write row: %s, %s, %s, %s", concept, severity, reportActionType, details));
			return;
		}

		this.writes = this.writes + 1;
	}

	private void addConceptsToRefSet(String branchPath, boolean legacy, boolean dryRun) throws TermServerScriptException {
		Set<Concept> conceptsToAdd = getRelevantConceptsToAdd(branchPath, legacy);
		if (!conceptsToAdd.isEmpty()) {
			int x = 0;
			int size = conceptsToAdd.size();
			for (Concept definite : conceptsToAdd) {
				x = x + 1;
				info(String.format("Processing %d / %d Concepts to add to Reference Set.", x, size));

				RefsetMemberPojo newRefSetMember = createRefSetMember(branchPath, LAT_REFSETID, definite, dryRun);
				doReportOrLog(definite, Severity.LOW, ReportActionType.REFSET_MEMBER_ADDED, "Added by creating ReferenceSetMember " + newRefSetMember.getId());
			}
		}
	}

	private void addDuplicateConceptsToRefSetDuplicatesTab() {
		for (String sctid : multiMapOfAllConceptsAddedToSpreadsheet.keySet()) {
			info("Writing information about duplicate records");
			Collection<List<String>> items = multiMapOfAllConceptsAddedToSpreadsheet.get(sctid);

			if (items.size() > 1) {
				duplicated++;

				for (List<String> item : items) {
					try {
						boolean success = report(TAB_1, item.toArray());

						if (!success) {
							warn(String.format("Failed to write duplicate row: %s", sctid));
							return;
						}
					} catch (TermServerScriptException e) {
						error(String.format("Failed to write duplicate row: %s", sctid), e);
						return;
					}
				}
			}
		}

		if (duplicated == 0) {
			try {
				report(TAB_1, "No duplicates found");
			} catch (TermServerScriptException e) {
				warn("Failed to write to spreadsheet");
			}
		}
	}

	private Set<Concept> getRelevantConceptsToAdd(String branchPath, boolean legacy) {
		// Concepts which have the laterality attribute with an appropriate value
		String byLaterality = "( (<< 91723000 |Anatomical structure (body structure)| : 272741003 | Laterality (attribute) | = 182353008 |Side (qualifier value)|) MINUS ( * : 272741003 | Laterality (attribute) | = (7771000 |Left (qualifier value)| OR 24028007 |Right (qualifier value)| OR 51440002 |Right and left (qualifier value)|) ) )  MINUS (^ 723264001)";

		Set<Concept> conceptsToAdd = new HashSet<>();
		if (legacy) {
			conceptsToAdd.addAll(getAllConceptsByECL(branchPath, byLaterality));
		} else {
			conceptsToAdd.addAll(tsClient.findNewConcepts(branchPath, byLaterality, null));
			conceptsToAdd.addAll(tsClient.findUpdatedConcepts(branchPath, true, null, null, byLaterality));
		}

		// Concepts which are within a certain hierarchy and have an ancestor within the reference set
		String byHierarchy = "(( << 91723000 |Anatomical structure (body structure)| MINUS (* : 272741003 | Laterality (attribute) | = (7771000 |Left (qualifier value)| OR 24028007 |Right (qualifier value)| OR 51440002 |Right and left (qualifier value)|)))  AND (<  (^ 723264001)))   MINUS (^ 723264001)";
		if (legacy) {
			conceptsToAdd.addAll(getAllConceptsByECL(branchPath, byHierarchy));
		} else {
			conceptsToAdd.addAll(tsClient.findNewConcepts(branchPath, byHierarchy, null));
			conceptsToAdd.addAll(tsClient.findUpdatedConcepts(branchPath, true, null, null, byHierarchy));
		}

		return conceptsToAdd;
	}

	private RefsetMemberPojo createRefSetMember(String branchPath, String refsetId, Concept concept, boolean dryRun) {
		this.added = this.added + 1;
		RefsetMemberPojo rm = new RefsetMemberPojo()
				.withRefsetId(refsetId)
				.withActive(true)
				.withModuleId(SCTID_CORE_MODULE)
				.withReferencedComponentId(concept.getId());

		if (dryRun) {
			rm.setId(UUID.randomUUID().toString());
			return rm;
		}

		return tsClient.createRefsetMember(branchPath, rm);
	}
}
