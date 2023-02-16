package org.ihtsdo.otf.transformationandtemplate.service.script;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.gdata.util.common.base.Pair;
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
public class Update_Lat_Refset extends AuthoringPlatformScript {
	private static final String LAT_REFSETID = "723264001";
	private static final String ECL_ADD_BY_LATERALITY = "(<< 91723000 |Anatomical structure (body structure)| : 272741003 | Laterality (attribute) | = 182353008 |Side (qualifier value)|) MINUS ((^ 723264001) OR ( * : 272741003 | Laterality (attribute) | = (7771000 |Left (qualifier value)| OR 24028007 |Right (qualifier value)| OR 51440002 |Right and left (qualifier value)|) ))";
	private static final String ECL_ADD_BY_HIERARCHY = "((<< 91723000 |Anatomical structure (body structure)|) AND (< (^ 723264001))) MINUS ((^ 723264001) OR (* : 272741003 | Laterality (attribute) | = (7771000 |Left (qualifier value)| OR 24028007 |Right (qualifier value)| OR 51440002 |Right and left (qualifier value)|)))";
	private static final String ECL_REMOVE_BY_LATERALITY = "(^ 723264001 AND << 91723000 |Anatomical structure (body structure)|) : (272741003 | Laterality (attribute) | = (7771000 |Left (qualifier value)| OR 24028007 |Right(qualifier value)| OR 51440002 |Right and left (qualifier value)|))";
	private static final String ECL_REMOVE_BY_NO_PREREQUISITE_ANCESTOR = "^ 723264001 MINUS << 423857001";

	private static final int CHUNK_SIZE = 20;

	/**
	 * Key for accessing request parameter to control whether the script should
	 * process all Concepts or just recently created/modified.
	 */
	private static final String PARAM_LEGACY = "legacy";

	/**
	 * Key for accessing request parameter to control which branch to process.
	 */
	private static final String PARAM_BRANCH_PATH = "branch";

	/**
	 * Key for accessing request parameter to control whether the script should
	 * run in dry mode, i.e. only produce the report and not to modify content.
	 */
	private static final String PARAM_DRY_RUN = "dry";
	public static final boolean REMOVE_CONCEPT = false;
	public static final boolean ADD_CONCEPT = true;

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
	private final HashMap<String, Concept> conceptsToAdd = new HashMap<>();
	private final HashMap<String, Pair<Concept, RefsetMemberPojo>> conceptsToRemove = new HashMap<>();

	public Update_Lat_Refset(JobRun jobRun, ScriptManager mgr) {
		super(jobRun, mgr);
	}

	@Override
	public Job getJob() {
		JobParameters jobParameters = new JobParameters()
				.add(PARAM_LEGACY)
				.withType(JobParameter.Type.BOOLEAN)
				.withDefaultValue(false)
				.withDescription("Change script to process all active Concepts, instead of only new/modified Concepts.")
				.add(PARAM_DRY_RUN)
				.withType(JobParameter.Type.BOOLEAN)
				.withDefaultValue(false)
				.withDescription("Change script to run in dry mode, i.e. only produce report and not modify content.")
				.add(PARAM_BRANCH_PATH)
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
		String branchPath = jobRun.getParamValue(PARAM_BRANCH_PATH, task.getBranchPath());
		boolean legacy = jobRun.getParamBoolean(PARAM_LEGACY);
		boolean dryRun = jobRun.getParamBoolean(PARAM_DRY_RUN);

		info(String.format("Running with branchPath: %s legacy: %b dryRun: %b", branchPath, legacy, dryRun));
		percentageComplete(20);

		removeConceptsFromRefSet(branchPath, legacy);
		percentageComplete(50);

		addConceptsToRefSet(branchPath, legacy);
		percentageComplete(60);

		performUniqueChangeToRefSetBasedOnHashMaps(branchPath, dryRun);
		percentageComplete(70);

		addDuplicateConceptsToRefSetDuplicatesTab();
		percentageComplete(90);

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

	private void removeConceptsFromRefSet(String branchPath, boolean legacy) {
		List<RefsetMemberPojo> rmToInactivate = new ArrayList<>();
		Map<String, Concept> cache = new HashMap<>();

		Set<Concept> relevantConceptsToRemove = getConceptSetFromECLs(true, branchPath, legacy, ECL_REMOVE_BY_LATERALITY, ECL_REMOVE_BY_NO_PREREQUISITE_ANCESTOR);
		chunk(relevantConceptsToRemove)
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

			recordAdditionOrRemovalInHashMaps(ADD_CONCEPT, concept, rm);
		}
	}

	private Set<Concept> getConceptSetFromECLs(boolean findUpdatedConcepts, String branchPath, boolean legacy, String eclFirst, String eclSecond) {
		Set<Concept> conceptSet = new HashSet<>();

		if (legacy) {
			conceptSet.addAll(getAllConceptsByECL(branchPath, eclFirst));
			conceptSet.addAll(getAllConceptsByECL(branchPath, eclSecond));
		} else {
			conceptSet.addAll(tsClient.findNewConcepts(branchPath, eclFirst, null));
			conceptSet.addAll(tsClient.findUpdatedConcepts(branchPath, true, null, eclFirst));
			conceptSet.addAll(tsClient.findNewConcepts(branchPath, eclSecond, null));
			conceptSet.addAll(tsClient.findUpdatedConcepts(branchPath, true, null, eclSecond));
		}

		if (findUpdatedConcepts) {
			conceptSet.addAll(tsClient.findUpdatedConcepts(branchPath, false, null, null));
		}

		return conceptSet;
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
	private <T> Stream<List<T>> chunk(Set<T> set) {
		List<T> list = new ArrayList<>(set);
		int size = list.size();

		if (size > 0) {
			int fullChunks = (size - 1) / CHUNK_SIZE;
			return IntStream.range(0, fullChunks + 1).mapToObj(n -> list.subList(n * CHUNK_SIZE, n == fullChunks ? size : (n + 1) * CHUNK_SIZE));
		}

		return Stream.empty();
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

	private void doReportOrLog(Concept concept, ReportActionType reportActionType, String details) throws TermServerScriptException {
		String semTag = null;

		try {
			semTag = SnomedUtils.deconstructFSN(concept.getFsnTerm(), true)[1];
		} catch (Exception e) {
			debug("FSN related exception while trying to report for " + concept.getConceptId() + ": " + e);
		}

		multiMapOfAllConceptsAddedToSpreadsheet.put(concept.getConceptId(), Lists.newArrayList(task.getKey(), concept.getConceptId(), concept.getFsnTerm(), semTag, Severity.LOW.name(), reportActionType.name(), details));

		boolean success = report(concept, Severity.LOW, reportActionType, details);
		if (!success) {
			warn(String.format("Failed to write row: %s, %s, %s, %s", concept, Severity.LOW, reportActionType, details));
			return;
		}

		this.writes = this.writes + 1;
	}

	private void addConceptsToRefSet(String branchPath, boolean legacy) {
		Set<Concept> conceptsToAdd = getConceptSetFromECLs(false, branchPath, legacy, ECL_ADD_BY_LATERALITY, ECL_ADD_BY_HIERARCHY);

		if (!conceptsToAdd.isEmpty()) {
			int x = 0;
			int size = conceptsToAdd.size();

			for (Concept concept : conceptsToAdd) {
				x++;
				info(String.format("Processing %d / %d Concepts to add to Reference Set.", x, size));
				recordAdditionOrRemovalInHashMaps(REMOVE_CONCEPT, concept, null);
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

	private RefsetMemberPojo createRefSetMember(String branchPath, Concept concept, boolean dryRun) {
		this.added = this.added + 1;
		RefsetMemberPojo rm = new RefsetMemberPojo()
				.withRefsetId(LAT_REFSETID)
				.withActive(true)
				.withModuleId(SCTID_CORE_MODULE)
				.withReferencedComponentId(concept.getId());

		if (dryRun) {
			rm.setId(UUID.randomUUID().toString());
			return rm;
		}

		return tsClient.createRefsetMember(branchPath, rm);
	}

	private void recordAdditionOrRemovalInHashMaps(boolean removeConcept, Concept concept, RefsetMemberPojo member) {
		if (removeConcept) {
			conceptsToRemove.put(concept.getConceptId(), new Pair<>(concept, member));
		} else {
			if (conceptsToRemove.containsKey(concept.getConceptId())) {
				conceptsToRemove.remove(concept.getConceptId());
				multiMapOfAllConceptsAddedToSpreadsheet.removeAll(concept.getConceptId());
			} else {
				conceptsToAdd.put(concept.getConceptId(), concept);
			}
		}
	}

	private void performUniqueChangeToRefSetBasedOnHashMaps(String branchPath, boolean dryRun) throws TermServerScriptException {
		for (Concept concept : conceptsToAdd.values()) {
			RefsetMemberPojo newRefSetMember = createRefSetMember(branchPath, concept, dryRun);
			doReportOrLog(concept, ReportActionType.REFSET_MEMBER_ADDED, "Added by creating ReferenceSetMember " + newRefSetMember.getId());
		}

		for (Pair<Concept, RefsetMemberPojo> pair : conceptsToRemove.values()) {
			Concept concept = pair.getFirst();
			RefsetMemberPojo rm = pair.getSecond();
			ReportActionType reportActionType = removeRefsetMemberSilently(branchPath, rm, dryRun);
			doReportOrLog(concept, reportActionType, "Removed by removing ReferenceSetMember " + rm.getId());
		}
	}
}
