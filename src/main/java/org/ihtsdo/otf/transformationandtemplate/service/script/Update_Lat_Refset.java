package org.ihtsdo.otf.transformationandtemplate.service.script;

import org.ihtsdo.otf.exception.TermServerScriptException;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.CodeSystemVersion;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.RefsetMemberPojo;
import org.ihtsdo.otf.transformationandtemplate.domain.Concept;
import org.ihtsdo.otf.transformationandtemplate.service.client.SnowstormClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private static final Logger LOGGER = LoggerFactory.getLogger(Update_Lat_Refset.class);
	private static final String LATERALISABLE_REFERENCE_SET_ID = "723264001";
	private static final String ECL_RULE_FOR_MEMBERSHIP = "(<< 423857001 |Structure of half of body lateral to midsagittal plane (body structure)| MINUS ( * : 272741003 | Laterality (attribute) | = (7771000 |Left (qualifier value)| OR 24028007 |Right (qualifier value)| OR 51440002 |Right and left (qualifier value)|) ))";
	private static final String ECL_TO_ADD_MEMBERSHIP = ECL_RULE_FOR_MEMBERSHIP + " MINUS (^ 723264001)";
	private static final String ECL_TO_REMOVE_MEMBERSHIP = "(^ 723264001) MINUS " + ECL_RULE_FOR_MEMBERSHIP;

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

	/**
	 * Key for accessing request parameter to exclude Concepts from being processed.
	 */
	private static final String PARAM_EXCLUDE = "exclude";

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
	 * Counter for how many Concepts have contradicting modelling.
	 */
	private int contradicting = 0;

	/**
	 * Counter for how many updates failed to go through.
	 */
	private int failed = 0;

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
				.add(PARAM_EXCLUDE)
				.withType(JobParameter.Type.STRING)
				.withDescription("List of Concept Ids to be ignored during processing.")
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
		reportManager.setTabNames(new String[] {"Process", "Issues", "Summary"});
		String[] columnHeadings = new String[] {"Task,SCTID,FSN,Semtag,Severity,Action,Info",
												"SCTID,FSN,Issue,Info",
												"Issue, Count"};
		reportManager.initialiseReportFiles(columnHeadings);
		return reportManager.getUrl();
	}

	@Override
	public void runJob() throws TermServerScriptException {
		percentageComplete(1);
		String branchPath = jobRun.getParamValue(PARAM_BRANCH_PATH, task.getBranchPath());
		boolean legacy = jobRun.getParamBoolean(PARAM_LEGACY);
		boolean dryRun = jobRun.getParamBoolean(PARAM_DRY_RUN);
		String excludedConceptIds = jobRun.getParamValue(PARAM_EXCLUDE);
		String versionedBranchPath = getVersionedBranchPath(branchPath);

		LOGGER.info("Running with branchPath: '{}', legacy: '{}', dryRun: '{}', excludedConceptIds: '{}'", branchPath, legacy, dryRun, excludedConceptIds);
		percentageComplete(20);

		// Collect concepts
		List<Concept> conceptsToAdd = getConceptsToAdd(branchPath, versionedBranchPath, legacy);
		List<Concept> conceptsToRemove = getConceptsToRemove(branchPath, versionedBranchPath, legacy);
		LOGGER.info("Found {} Concepts to add to reference set.", conceptsToAdd.size());
		LOGGER.info("Found {} Concepts to remove from reference set.", conceptsToRemove.size());
		percentageComplete(40);

		// Add to reference set
		addToReferenceSet(branchPath, dryRun, excludedConceptIds, conceptsToAdd);
		percentageComplete(60);

		// Remove from reference set
		removeFromReferenceSet(branchPath, dryRun, excludedConceptIds, conceptsToRemove);
		percentageComplete(80);

		// Finalise
		reportRemainingChanges(branchPath, versionedBranchPath, legacy, conceptsToAdd, conceptsToRemove);
		flushReport(branchPath);
		percentageComplete(100);
	}

	private String getVersionedBranchPath(String branchPath) {
		String codeSystemShortName = extractShortName(branchPath);
		CodeSystemVersion codeSystemVersion = tsClient.getLatestVersion(codeSystemShortName, false, false);
		if (codeSystemVersion == null) {
			return null;
		}

		return codeSystemVersion.getBranchPath();
	}

	private String extractShortName(String branchPath) {
		if (branchPath == null || branchPath.isBlank()) {
			throw new IllegalArgumentException("Branch path must contain at least MAIN");
		}

		if ("MAIN".equals(branchPath)) {
			return "SNOMEDCT";
		}

		String[] parts = branchPath.split("/");

		if (parts.length < 2) {
			throw new IllegalArgumentException("Branch path must contain a short name");
		}

		if (branchPath.startsWith("MAIN/SNOMEDCT-")) {
			return parts[1];
		}

		if (branchPath.startsWith("MAIN/")) {
			return "SNOMEDCT";
		}

		throw new IllegalArgumentException("Branch path must contain a short name and start with MAIN");
	}

	private List<Concept> getConceptsToAdd(String branchPath, String versionedBranchPath, boolean legacy) {
		List<Concept> conceptsToAdd = getConceptsFromTS(branchPath, ECL_TO_ADD_MEMBERSHIP);
		conceptsToAdd.removeIf(concept -> concept.getFsnTerm().endsWith("(cell)") || concept.getFsnTerm().endsWith("(cell structure)") || concept.getFsnTerm().endsWith("(morphologic abnormality)"));

		if (!legacy && versionedBranchPath != null) {
			// If Concept found in previous version with same query, then it is considered a legacy issue. Essentially,
			// non-legacy mode only updates current authoring cycle content.
			getConceptsFromTS(versionedBranchPath, ECL_TO_ADD_MEMBERSHIP).forEach(conceptsToAdd::remove);
		}

		return conceptsToAdd;
	}

	private List<Concept> getConceptsToRemove(String branchPath, String versionedBranchPath, boolean legacy) {
		List<Concept> conceptsToRemove = getConceptsFromTS(branchPath, ECL_TO_REMOVE_MEMBERSHIP);

		if (!legacy && versionedBranchPath != null) {
			// If Concept found in last previous with same query, then it is considered a legacy issue. Essentially,
			// non-legacy mode only updates current authoring cycle content.
			getConceptsFromTS(versionedBranchPath, ECL_TO_REMOVE_MEMBERSHIP).forEach(conceptsToRemove::remove);
		}

		return conceptsToRemove;
	}

	private void addToReferenceSet(String branchPath, boolean dryRun, String excludedConceptIds, List<Concept> conceptsToAdd) throws TermServerScriptException {
		if (conceptsToAdd.isEmpty()) {
			LOGGER.info("No Concepts identified to add to reference set.");
			return;
		}

		int x = 1;
		int size = conceptsToAdd.size();
		for (Concept concept : conceptsToAdd) {
			LOGGER.info("Processing {}/{} concepts to add to reference set.", x, size);
			x = x + 1;
			if (excludedConceptIds != null && excludedConceptIds.contains(concept.getConceptId())) {
				doReportOrLog(concept, ReportActionType.SKIPPING, "Concept has been identified for addition but has been ignored.");
				continue;
			}

			List<RefsetMemberPojo> tscResponse = tsClient.findRefsetMemberByReferencedComponentId(branchPath, LATERALISABLE_REFERENCE_SET_ID, concept.getId(), null);
			if (tscResponse.isEmpty()) {
				RefsetMemberPojo refsetMemberPojo = createMember(branchPath, dryRun, concept);
				doReportOrLog(concept, ReportActionType.REFSET_MEMBER_ADDED, "Added by creating ReferenceSetMember " + refsetMemberPojo.getId());
			} else {
				RefsetMemberPojo refsetMemberPojo = reactivateMember(branchPath, dryRun, tscResponse.get(0));
				doReportOrLog(concept, ReportActionType.REFSET_MEMBER_REACTIVATED, "Added by re-activating ReferenceSetMember " + refsetMemberPojo.getId());
			}
		}
	}

	private void removeFromReferenceSet(String branchPath, boolean dryRun, String excludedConceptIds, List<Concept> conceptsToRemove) throws TermServerScriptException {
		if (conceptsToRemove.isEmpty()) {
			LOGGER.info("No Concepts identified to remove from reference set.");
			return;
		}

		List<RefsetMemberPojo> referenceSetMembersToInactivate = new ArrayList<>();
		Map<String, Concept> cache = new HashMap<>();
		chunk(new HashSet<>(conceptsToRemove))
				.forEach(chunk -> {
					cache.putAll(mapByConceptId(chunk));
					List<RefsetMemberPojo> tscResponse = tsClient.findRefsetMemberByReferencedComponentId(branchPath, LATERALISABLE_REFERENCE_SET_ID, joinByConceptId(chunk), true);
					referenceSetMembersToInactivate.addAll(tscResponse);
				});

		if (referenceSetMembersToInactivate.isEmpty()) {
			return;
		}

		int x = 1;
		int size = referenceSetMembersToInactivate.size();
		for (RefsetMemberPojo refsetMemberPojo : referenceSetMembersToInactivate) {
			LOGGER.info("Processing {}/{} concepts to remove from reference set.", x, size);
			x = x + 1;

			Concept concept = cache.get(refsetMemberPojo.getConceptId());
			if (excludedConceptIds != null && excludedConceptIds.contains(concept.getId())) {
				doReportOrLog(concept, ReportActionType.SKIPPING, "Concept has been identified for removal but has been ignored.");
				continue;
			}

			boolean published = refsetMemberPojo.getReleasedEffectiveTime() != null && !refsetMemberPojo.getReleasedEffectiveTime().isBlank();
			if (published) {
				inactivateMember(branchPath, refsetMemberPojo, dryRun);
				doReportOrLog(concept, ReportActionType.REFSET_MEMBER_INACTIVATED, "Removed by inactivating ReferenceSetMember " + refsetMemberPojo.getId());
			} else {
				deleteMember(branchPath, refsetMemberPojo, dryRun);
				doReportOrLog(concept, ReportActionType.REFSET_MEMBER_DELETED, "Removed by deleting ReferenceSetMember " + refsetMemberPojo.getId());
			}
		}
	}

	private List<Concept> getConceptsFromTS(String branchPath, String ecl) {
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
		int chunkSize = 20;

		if (size > 0) {
			int fullChunks = (size - 1) / chunkSize;
			return IntStream.range(0, fullChunks + 1).mapToObj(n -> list.subList(n * chunkSize, n == fullChunks ? size : (n + 1) * chunkSize));
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

	private RefsetMemberPojo createMember(String branchPath, boolean dryRun, Concept concept) {
		this.added = this.added + 1;
		RefsetMemberPojo rm = new RefsetMemberPojo()
				.withRefsetId(LATERALISABLE_REFERENCE_SET_ID)
				.withActive(true)
				.withModuleId(SCTID_CORE_MODULE)
				.withReferencedComponentId(concept.getId());

		if (dryRun) {
			rm.setId(UUID.randomUUID().toString());
			return rm;
		}

		return tsClient.createRefsetMember(branchPath, rm);
	}

	private RefsetMemberPojo reactivateMember(String branchPath, boolean dryRun, RefsetMemberPojo refsetMemberPojo) throws TermServerScriptException {
		this.added = this.added + 1;
		refsetMemberPojo.setActive(true);
		refsetMemberPojo.setEffectiveTime(null);

		if (!dryRun) {
			return tsClient.updateRefsetMember(branchPath, refsetMemberPojo);
		}

		return refsetMemberPojo;
	}

	protected void inactivateMember(String branchPath, RefsetMemberPojo refsetMemberPojo, boolean dryRun) throws TermServerScriptException {
		this.removed = this.removed + 1;
		boolean published = refsetMemberPojo.getReleasedEffectiveTime() != null && !refsetMemberPojo.getReleasedEffectiveTime().isBlank();
		if (published) {
			LOGGER.info("Inactivating {}", refsetMemberPojo.getMemberId());
			refsetMemberPojo.setActive(false);

			if (!dryRun) {
				tsClient.updateRefsetMember(branchPath, refsetMemberPojo);
			}
		}
	}

	protected void deleteMember(String branchPath, RefsetMemberPojo refsetMemberPojo, boolean dryRun) throws TermServerScriptException {
		this.removed = this.removed + 1;
		boolean published = refsetMemberPojo.getReleasedEffectiveTime() != null && !refsetMemberPojo.getReleasedEffectiveTime().isBlank();
		if (!published) {
			LOGGER.info("Deleting {}", refsetMemberPojo.getMemberId());

			if (!dryRun) {
				tsClient.deleteRefsetMember(branchPath, refsetMemberPojo);
			}
		}
	}

	private void doReportOrLog(Concept concept, ReportActionType reportActionType, String details) throws TermServerScriptException {
		boolean success = report(concept, Severity.LOW, reportActionType, details);
		if (!success) {
			LOGGER.warn(String.format("Failed to write row: %s, %s, %s, %s", concept, Severity.LOW, reportActionType, details));
			return;
		}

		this.writes = this.writes + 1;
	}

	private void reportRemainingChanges(String branchPath, String versionedBranchPath, boolean legacy, List<Concept> conceptsToAdd, List<Concept> conceptsToRemove) throws TermServerScriptException {
		LOGGER.info("Reporting remaining changes.");
		if (legacy) {
			return;
		}

		List<Concept> secondConceptsToAdd = getConceptsToAdd(branchPath, versionedBranchPath, legacy);
		if (!secondConceptsToAdd.isEmpty()) {
			LOGGER.info("Despite initial run, {} Concepts need to be added to reference set.", secondConceptsToAdd.size());
			List<String> removedConceptIds = conceptsToRemove.stream().map(Concept::getId).toList();
			for (Concept concept : secondConceptsToAdd) {
				if (removedConceptIds.contains(concept.getConceptId())) {
					this.contradicting = this.contradicting + 1;
					report(TAB_1, concept.getConceptId(), concept.getFsnTerm(), "CONTRADICTING", "Concept was removed by this script, but it is now suggested it should be added again. This suggests the modelling is contradicting/ambiguous.");
				} else {
					this.failed = this.failed + 1;
					report(TAB_1, concept.getConceptId(), concept.getFsnTerm(), "FAILED", "First run identified Concept for addition to reference set; second run also identifies Concept for addition. This suggests the initial update failed.");
				}
			}
		}

		List<Concept> secondConceptsToRemove = getConceptsToRemove(branchPath, versionedBranchPath, legacy);
		if (!secondConceptsToRemove.isEmpty()) {
			LOGGER.info("Despite initial run, {} Concepts need to be removed from reference set.", secondConceptsToRemove.size());
			List<String> addedConceptIds = conceptsToAdd.stream().map(Concept::getId).toList();
			for (Concept concept : secondConceptsToRemove) {
				if (addedConceptIds.contains(concept.getConceptId())) {
					this.contradicting = this.contradicting + 1;
					report(TAB_1, concept.getConceptId(), concept.getFsnTerm(), "CONTRADICTING", "Concept was added by this script, but it is now suggested it should be removed again. This suggests the modelling is contradicting/ambiguous.");
				} else {
					this.failed = this.failed + 1;
					report(TAB_1, concept.getConceptId(), concept.getFsnTerm(), "FAILED", "First run identified Concept for removal from reference set; second run also identifies Concept for removal. This suggests the initial update failed.");
				}
			}
		}

		LOGGER.info("Remaining changes reported.");
	}

	private void flushReport(String branchPath) throws TermServerScriptException {
		if (writes == 0) {
			String message = String.format("Script has not found any data to action for Branch %s.", branchPath);
			LOGGER.info(message);
			report(TAB_0, message);
		}

		if (this.contradicting == 0 && this.failed == 0) {
			report(TAB_1, "No content issues have been identified.");
		}

		report(TAB_2, "Added to reference set", this.added);
		report(TAB_2, "Removed from reference set", this.removed);
		report(TAB_2, "Contradicting modelling", this.contradicting);
		report(TAB_2, "Updates failed", this.failed);

		flushFiles(true);
	}
}
