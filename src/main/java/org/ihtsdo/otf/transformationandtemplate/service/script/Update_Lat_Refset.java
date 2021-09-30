package org.ihtsdo.otf.transformationandtemplate.service.script;

import org.ihtsdo.otf.exception.TermServerScriptException;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.RefsetMemberPojo;
import org.ihtsdo.otf.transformationandtemplate.domain.Concept;
import org.snomed.otf.scheduler.domain.Job;
import org.snomed.otf.scheduler.domain.JobCategory;
import org.snomed.otf.scheduler.domain.JobRun;
import org.snomed.otf.scheduler.domain.JobType;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * See FRI-71.
 */
public class Update_Lat_Refset extends AuthoringPlatformScript implements JobClass {

	public static final String LAT_REFSETID = "723264001";
	public static final String LAT_REFSETID_AND_PT = "723264001 |Lateralizable body structure reference set|";

	private final Set<Concept> conceptsActioned = new HashSet<>();

	private int writes = 0;

	public Update_Lat_Refset(JobRun jobRun, ScriptManager mgr) {
		super(jobRun, mgr);
	}

	@Override
	public Job getJob() {
		return new Job()
				.withCategory(new JobCategory(JobType.BATCH_JOB, JobCategory.REFSET_UPDATE))
				.withName("Update Lateralizable Refset")
				.withDescription("This job adds and removes relevant Concepts to/from the Lateralizable RefSet. Please note this RefSet is only for Lateralizable " +
						"Concepts, and not Lateralized Concepts. Lateralizable is when a Concept can have a side, i.e. an arm " +
						"can be a left arm or a right arm. Lateralized, however, is when a Concept has a side, i.e. a left arm is on the left side.")
				.withProductionStatus(Job.ProductionStatus.TESTING)
				.build();
	}

	@Override
	public void runJob() throws TermServerScriptException {
		String branchPath = task.getBranchPath();

		/*
		 * Remove Concepts from RefSet if:-
		 *  - Concept has been inactivated
		 *  - Concept has been lateralised
		 *  - Concept is no longer subsumed by 423857001 |Structure of half of body lateral to midsagittal plane (body structure)|
		 * */
		removeConceptsFromRefSet(branchPath);

		/*
		 * Add Concepts to RefSet if:-
		 *  - Concept has ancestor in RefSet
		 *  - Concept is lateralizable but not lateralised (i.e. 272741003 | Laterality (attribute) | = 182353008 |Side (qualifier value)|)
		 * */
		addConceptsToRefSet(branchPath);

		if (this.writes == 0) {
			String message = String.format("Script has not found any data to action for Branch %s.", branchPath);
			info(message);
			report(0, message);
		}
	}

	private void removeConceptsFromRefSet(String branchPath) throws TermServerScriptException {
		List<RefsetMemberPojo> rmToInactivate = new ArrayList<>();
		Map<String, Concept> cache = new HashMap<>();

		// Collect relevant Concepts to remove
		List<Concept> relevantConceptsToRemove = getRelevantConceptsToRemove(branchPath);
		chunk(relevantConceptsToRemove, 20)
				.forEach(chunk -> {
					cache.putAll(mapByConceptId(chunk));
					List<RefsetMemberPojo> tscResponse = tsClient.findRefsetMemberByReferencedComponentId(branchPath, LAT_REFSETID, joinByConceptId(chunk), true);
					rmToInactivate.addAll(tscResponse);
				});

		if (rmToInactivate.isEmpty()) {
			info(String.format("No Concepts to be removed from %s for Branch %s.", LAT_REFSETID_AND_PT, branchPath));
			return;
		}

		// Remove RefSet members
		for (RefsetMemberPojo rm : rmToInactivate) {
			String conceptId = rm.getConceptId();
			Concept concept = cache.get(conceptId);
			if (concept == null) {
				warn(String.format("Can't find Concept in cache with id %s. Associated RefSet Member(s) will not be actioned.", conceptId));
				continue;
			}

			if (conceptsActioned.contains(concept)) {
				continue;
			}

			if (rm.isActive()) {
				this.conceptsActioned.add(concept);
				removeRefsetMember(concept, rm);
			} else {
				doReportOrLog(concept, Severity.LOW, ReportActionType.NO_CHANGE, "RefSet Member previously inactivated", rm);
			}
		}
	}

	private List<Concept> getRelevantConceptsToRemove(String branchPath) {
		// Concepts that have been lateralised, i.e. marked left/right/both
		String lateralised = "<< 91723000 |Anatomical structure (body structure)| : 272741003 | Laterality (attribute) | = (7771000 |Left (qualifier value)| OR 24028007 |Right (qualifier value)| OR 51440002 |Right and left (qualifier value)|))";

		// Concepts that are in RefSet but no longer have correct prerequisite ancestor
		String ancestor = "^ 723264001 MINUS ^ 723264001 << 423857001 |Structure of half of body lateral to midsagittal plane (body structure)|";

		List<Concept> inactivated = tsClient.findUpdatedConcepts(branchPath, false, null, null);
		List<Concept> lateralisedNew = tsClient.findNewConcepts(branchPath, lateralised, null);
		List<Concept> lateralisedUpdated = tsClient.findUpdatedConcepts(branchPath, true, null, lateralised);
		List<Concept> noPrerequisiteAncestor = tsClient.findUpdatedConcepts(branchPath, true, null, ancestor);

		List<Concept> conceptsToRemove = new ArrayList<>();
		conceptsToRemove.addAll(inactivated);
		conceptsToRemove.addAll(lateralisedNew);
		conceptsToRemove.addAll(lateralisedUpdated);
		conceptsToRemove.addAll(noPrerequisiteAncestor);

		return conceptsToRemove;
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

	private void doReportOrLog(Concept concept, Severity severity, ReportActionType reportActionType, Object... details) throws TermServerScriptException {
		boolean success = report(concept, severity, reportActionType, details);
		if (!success) {
			warn(String.format("Failed to write row: %s, %s, %s, %s", concept, severity, reportActionType, details));
			return;
		}

		this.writes = this.writes + 1;
	}

	private void addConceptsToRefSet(String branchPath) throws TermServerScriptException {
		// Find new & updated Concepts which may be added to RefSet (depending on various conditions)
		Set<Concept> potentials = getRelevantConceptsToPotentiallyAdd(branchPath);

		if (potentials.isEmpty()) {
			info(String.format("No potential Concepts to be added to %s for Branch %s.", LAT_REFSETID_AND_PT, branchPath));
		} else {
			for (Concept potential : potentials) {
				if (conceptsActioned.contains(potential)) {
					continue;
				}

				String conceptIdentifier = potential.toString();

				// Check whether Concept is already in RefSet
				info(String.format("Checking whether %s is already in %s on Branch %s.", conceptIdentifier, LAT_REFSETID_AND_PT, branchPath));
				boolean conceptInRefSet = conceptInRefSet(branchPath, LAT_REFSETID, potential);
				if (conceptInRefSet) {
					info(String.format("%s is already in %s on Branch %s. Moving onto next Concept.", conceptIdentifier, LAT_REFSETID_AND_PT, branchPath));
					doReportOrLog(potential, Severity.LOW, ReportActionType.NO_CHANGE, "Concept already in RefSet");
					continue;
				} else {
					info(String.format("%s is not currently in %s on Branch %s.", conceptIdentifier, LAT_REFSETID_AND_PT, branchPath));
				}

				// Check whether Concept has any ancestors
				info(String.format("Finding %s's ancestors on Branch %s.", conceptIdentifier, branchPath));
				String lateralizableAncestors = "> " + potential.getConceptId() + " :  272741003 | Laterality (attribute) | = 182353008 |Side (qualifier value)| MINUS (* : | = (7771000 |Left (qualifier value)| OR | = (24028007 |Right (qualifier value)| OR 51440002 |Right and left (qualifier value)|))";
				List<Concept> ancestors = tsClient.conceptsByECL(branchPath, lateralizableAncestors);
				if (ancestors.isEmpty()) {
					info(String.format("%s has no lateralizable ancestors. Cannot determine whether Concept should be added to %s.", conceptIdentifier, LAT_REFSETID_AND_PT));
					doReportOrLog(potential, Severity.LOW, ReportActionType.NO_CHANGE, "Concept has no lateralizable ancestors. Cannot determine whether Concept should be added " +
							"to RefSet.");
					continue;
				}

				// Check whether Concept's ancestor is in RefSet
				List<RefsetMemberPojo> tscResponse = tsClient.findRefsetMemberByReferencedComponentId(branchPath, LAT_REFSETID, joinByConceptId(ancestors), true);
				boolean conceptAncestorInRefSet = !tscResponse.isEmpty();
				if (conceptAncestorInRefSet) {
					conceptsActioned.add(potential);
					info(String.format("%s has an ancestor in %s. Concept will be added to the RefSet.", conceptIdentifier, LAT_REFSETID_AND_PT));
					RefsetMemberPojo newRefSetMember = createRefSetMember(branchPath, LAT_REFSETID, potential);
					info(String.format("Created RefSet %s for Branch %s.", newRefSetMember.toString(), branchPath));
					doReportOrLog(potential, Severity.LOW, ReportActionType.REFSET_MEMBER_ADDED, newRefSetMember);
				}
			}
		}

		// Find new & updated Concepts that should be in RefSet
		Set<Concept> definites = getRelevantConceptsToDefinitelyAdd(branchPath);
		if (definites.isEmpty()) {
			info(String.format("No definite Concepts to be added to %s for Branch %s.", LAT_REFSETID_AND_PT, branchPath));
		} else {
			for (Concept definite : definites) {
				if (conceptsActioned.contains(definite)) {
					continue;
				}

				String conceptIdentifier = definite.toString();

				// Check whether Concept is already in RefSet
				info(String.format("Checking whether %s is already in %s on Branch %s.", conceptIdentifier, LAT_REFSETID_AND_PT, branchPath));
				boolean conceptInRefSet = conceptInRefSet(branchPath, LAT_REFSETID, definite);
				if (conceptInRefSet) {
					info(String.format("%s is already in %s on Branch %s. Moving onto next Concept.", conceptIdentifier, LAT_REFSETID_AND_PT, branchPath));
					doReportOrLog(definite, Severity.LOW, ReportActionType.NO_CHANGE, "Concept already in RefSet");
					continue;
				} else {
					info(String.format("%s is not currently in %s on Branch %s.", conceptIdentifier, LAT_REFSETID_AND_PT, branchPath));
				}

				// Add to RefSet
				conceptsActioned.add(definite);
				RefsetMemberPojo newRefSetMember = createRefSetMember(branchPath, LAT_REFSETID, definite);
				doReportOrLog(definite, Severity.LOW, ReportActionType.REFSET_MEMBER_ADDED, newRefSetMember);
			}
		}
	}

	private Set<Concept> getRelevantConceptsToDefinitelyAdd(String branchPath) {
		// Concepts that have a Laterality of side, but also do not have a Laterality of left/right/both
		String lateralizable = "<< 91723000 |Anatomical structure (body structure)| : 272741003 | Laterality (attribute) | = 182353008 |Side (qualifier value)| MINUS (* : " +
				"272741003 | Laterality (attribute) | = (7771000 |Left (qualifier value)| OR 24028007 |Right (qualifier value)| OR 51440002 |Right and left (qualifier value)|))";

		List<Concept> newConcepts = tsClient.findNewConcepts(branchPath, lateralizable, null);
		List<Concept> updatedConcepts = tsClient.findUpdatedConcepts(branchPath, true, null, lateralizable);
		Set<Concept> conceptsToAdd = new HashSet<>();
		conceptsToAdd.addAll(newConcepts);
		conceptsToAdd.addAll(updatedConcepts);

		return conceptsToAdd;
	}

	private Set<Concept> getRelevantConceptsToPotentiallyAdd(String branchPath) {
		// Concepts that do not have a Laterality of left/right/both
		String notLateralised = "<< 91723000 |Anatomical structure (body structure)| MINUS (* : 272741003 | Laterality (attribute) | = (7771000 |Left (qualifier value)| OR " +
				"24028007 |Right (qualifier value)| OR 51440002 |Right and left (qualifier value)|))";

		List<Concept> newConcepts = tsClient.findNewConcepts(branchPath, notLateralised, null);
		List<Concept> updatedConcepts = tsClient.findUpdatedConcepts(branchPath, true, null, notLateralised);
		Set<Concept> conceptsToAdd = new HashSet<>();
		conceptsToAdd.addAll(newConcepts);
		conceptsToAdd.addAll(updatedConcepts);

		return conceptsToAdd;
	}

	private boolean conceptInRefSet(String branchPath, String refsetId, Concept concept) {
		return !tsClient.findRefsetMemberByReferencedComponentId(branchPath, refsetId, concept.getId(), true).isEmpty();
	}

	private RefsetMemberPojo createRefSetMember(String branchPath, String refsetId, Concept concept) {
		RefsetMemberPojo rm = new RefsetMemberPojo()
				.withRefsetId(refsetId)
				.withActive(true)
				.withModuleId(SCTID_MODEL_MODULE)
				.withReferencedComponentId(concept.getId());
		return tsClient.createRefsetMember(branchPath, rm);
	}
}
