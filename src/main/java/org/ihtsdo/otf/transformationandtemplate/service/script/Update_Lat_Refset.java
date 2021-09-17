package org.ihtsdo.otf.transformationandtemplate.service.script;

import org.ihtsdo.otf.exception.TermServerScriptException;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.RefsetMemberPojo;
import org.ihtsdo.otf.transformationandtemplate.domain.Concept;
import org.snomed.otf.scheduler.domain.Job;
import org.snomed.otf.scheduler.domain.JobCategory;
import org.snomed.otf.scheduler.domain.JobRun;
import org.snomed.otf.scheduler.domain.JobType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * See FRI-71.
 */
public class Update_Lat_Refset extends AuthoringPlatformScript implements JobClass {

	public static final String LAT_REFSETID = "723264001";
	public static final String LAT_REFSETID_AND_PT = "723264001 |Lateralizable body structure reference set|";

	/**
	 * Debug property. Toggle to skip the function of removing inactive Concepts from RefSet.
	 */
	private final boolean removeInactivateConcepts = true;

	/**
	 * Debug property. Toggle to skip the function of adding new Concepts to RefSet.
	 */
	private final boolean addNewConcepts = true;


	public Update_Lat_Refset(JobRun jobRun, ScriptManager mgr) {
		super(jobRun, mgr);
	}

	@Override
	public Job getJob() {
		return new Job()
				.withCategory(new JobCategory(JobType.BATCH_JOB, JobCategory.REFSET_UPDATE))
				.withName("Update Lateralizable Refset")
				.withDescription("This job adds and removes relevant Concepts to/from the Lateralizable RefSet.")
				.withProductionStatus(Job.ProductionStatus.TESTING)
				.build();
	}

	@Override
	public void runJob() throws TermServerScriptException {
		String branchPath = task.getBranchPath();

		if (removeInactivateConcepts) {
			info(String.format("Finding Concepts recently inactivated on Branch %s.", branchPath));
			List<Concept> inactivatedConcepts = tsClient.findUpdatedConcepts(branchPath, false, null);
			info(String.format("Found %d recently inactivated Concepts on Branch %s.", inactivatedConcepts.size(), branchPath));
			info(String.format("Removing recently inactivated Concepts on Branch %s from %s.", branchPath, LAT_REFSETID_AND_PT));
			removeInactiveConceptsFromRefset(branchPath, LAT_REFSETID, inactivatedConcepts);
		} else {
			info(String.format("Function to remove inactive Concepts from %s on Branch %s has been disabled.", LAT_REFSETID_AND_PT, branchPath));
		}

		if (addNewConcepts) {
			String ecl = "< 123037004 |Body structure (body structure)|";
			info(String.format("Finding Concepts recently created on Branch %s with ECL %s.", branchPath, ecl));
			List<Concept> newConcepts = tsClient.findNewConcepts(branchPath, ecl, null);
			info(String.format("Found %d recently created Concepts on Branch %s with ECL %s.", newConcepts.size(), branchPath, ecl));
			info(String.format("Adding recently created Concepts on Branch %s to %s.", branchPath, LAT_REFSETID_AND_PT));
			addRecentlyCreatedConceptsToRefset(branchPath, LAT_REFSETID, newConcepts);
		} else {
			info(String.format("Function to add new Concepts to %s on Branch %s has been disabled.", LAT_REFSETID_AND_PT, branchPath));
		}
	}

	private void removeInactiveConceptsFromRefset(String branchPath, String refsetId, List<Concept> inactivatedConcepts) throws TermServerScriptException {
		List<RefsetMemberPojo> rmToInactivate = new ArrayList<>();
		Map<String, Concept> cache = new HashMap<>();
		chunk(inactivatedConcepts, 20)
				.forEach(chunk -> {
					cache.putAll(mapByConceptId(chunk));
					List<RefsetMemberPojo> tscResponse = tsClient.findRefsetMemberByReferencedComponentId(branchPath, refsetId, joinByConceptId(chunk), true);
					rmToInactivate.addAll(tscResponse);
				});

		info(String.format("Out of %s inactivated Concepts on Branch %s, %s are present in %s", inactivatedConcepts.size(), branchPath, rmToInactivate.size(), LAT_REFSETID_AND_PT));
		for (RefsetMemberPojo rm : rmToInactivate) {
			String conceptId = rm.getConceptId();
			Concept concept = cache.get(conceptId);
			if (concept == null) {
				warn(String.format("Can't find Concept in cache with id %s. Associated RefSet Member(s) will not be actioned.", conceptId));
				continue;
			}

			if (rm.isActive()) {
				removeRefsetMember(concept, rm);
				doReportOrLog(concept, Severity.LOW, ReportActionType.REFSET_MEMBER_INACTIVATED, "RefsetMember inactivated", rm);
			} else {
				doReportOrLog(concept, Severity.LOW, ReportActionType.NO_CHANGE, "RefsetMember previously inactivated", rm);
			}
		}
	}

	private void addRecentlyCreatedConceptsToRefset(String branchPath, String refsetId, List<Concept> newConcepts) throws TermServerScriptException {
		for (Concept newConcept : newConcepts) {
			String conceptIdentifier = newConcept.toString();
			info(String.format("Checking whether %s is already in %s on Branch %s.", conceptIdentifier, LAT_REFSETID_AND_PT, branchPath));
			boolean alreadyInRefset = conceptAlreadyInRefSet(branchPath, refsetId, newConcept);
			if (alreadyInRefset) {
				info(String.format("%s is already in %s on Branch %s. Moving onto next new Concept.", conceptIdentifier, LAT_REFSETID_AND_PT, branchPath));
				doReportOrLog(newConcept, Severity.LOW, ReportActionType.NO_CHANGE, "Concept already in RefSet");
				continue;
			} else {
				info(String.format("%s is not currently in %s on Branch %s.", conceptIdentifier, LAT_REFSETID_AND_PT, branchPath));
			}

			info(String.format("Finding %s's ancestors on Branch %s.", conceptIdentifier, branchPath));
			List<Concept> ancestors = tsClient.getAncestors(branchPath, newConcept.getConceptId());
			if (ancestors.isEmpty()) {
				info(String.format("%s has no ancestors. Cannot determine whether Concept should be added to %s.", conceptIdentifier, LAT_REFSETID_AND_PT));
				doReportOrLog(newConcept, Severity.LOW, ReportActionType.NO_CHANGE, "Concept has no ancestors. Cannot determine whether Concept should be added to RefSet.");
				continue;
			}

			List<RefsetMemberPojo> tscResponse = tsClient.findRefsetMemberByReferencedComponentId(branchPath, refsetId, joinByConceptId(ancestors), true);
			boolean conceptHasAncestorInRefSet = !tscResponse.isEmpty();
			if (conceptHasAncestorInRefSet) {
				info(String.format("%s has an ancestor in %s. Concept will be added to the RefSet.", conceptIdentifier, LAT_REFSETID_AND_PT));
				RefsetMemberPojo newRefSetMember = createRefSetMember(branchPath, refsetId, newConcept);
				info(String.format("Created RefSet %s for Branch %s.", newRefSetMember.toString(), branchPath));
				doReportOrLog(newConcept, Severity.LOW,  ReportActionType.REFSET_MEMBER_ADDED, newRefSetMember);
			}
		}
	}

	// Split collection into X smaller collections (for batch processing)
	private <T> Stream<List<T>> chunk(List<T> list, int chunkSize) {
		if (chunkSize <= 0) {
			throw new IllegalArgumentException("length = " + chunkSize);
		}

		int size = list.size();
		if (size <= 0) {
			return Stream.empty();
		}

		int fullChunks = (size - 1) / chunkSize;
		return IntStream.range(0, fullChunks + 1).mapToObj(n -> list.subList(n * chunkSize, n == fullChunks ? size : (n + 1) * chunkSize));
	}

	private boolean conceptAlreadyInRefSet(String branchPath, String refsetId, Concept concept) {
		return !tsClient.findRefsetMemberByReferencedComponentId(branchPath, refsetId, concept.getId(), true).isEmpty();
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

	private RefsetMemberPojo createRefSetMember(String branchPath, String refsetId, Concept concept) {
		RefsetMemberPojo rm = new RefsetMemberPojo()
				.withRefsetId(refsetId)
				.withActive(true)
				.withModuleId(SCTID_MODEL_MODULE)
				.withReferencedComponentId(concept.getId());
		return tsClient.createRefsetMember(branchPath, rm);
	}

	private void doReportOrLog(Concept concept, Severity severity, ReportActionType reportActionType, Object... details) throws TermServerScriptException {
		boolean success = report(concept, severity, reportActionType, details);
		if (!success) {
			warn(String.format("Failed to write row: %s, %s, %s, %s", concept, severity, reportActionType, details));
		}
	}
}
