package org.ihtsdo.otf.transformationandtemplate.service.script;

import org.ihtsdo.otf.rest.client.terminologyserver.pojo.RefsetMemberPojo;
import org.ihtsdo.otf.transformationandtemplate.domain.Concept;
import org.snomed.otf.scheduler.domain.*;
import org.snomed.otf.scheduler.domain.Job.ProductionStatus;

import java.util.*;
import java.util.stream.Collectors;

/**
 * See FRI-72 & FRI-158
 * The following is the steps to update the SE refset. The same approach can be applied to the SP refset by change 'entire' to 'part'.
 * 1. Identify all new entire concepts in the latest release
 * 2. Find the direct parent concept which is a structure concept by FSNs.
 *   - FSN of the entire concept starts with 'Entire' or 'All'.
 *   - FSN of structure concept starts with 'Structure' or contains 'structure'
 * 3. Create pairs of Structure and Entire concepts that are candidates for new additions to the S/E association refset
 * 4. Identify the existing row in the S/E association refset if a structure or entire concept is inactive in the latest release
 * 5. Replace the inactive concepts with active concepts
 * 6. The combination of outcomes from 3 and 5 is the delta file for review
 */
public class Update_SE_SP_Refsets extends AuthoringPlatformScript implements JobClass {
	
	public static final String SCTID_SE_REFSETID = "734138000";
	public static final String SCTID_SP_REFSETID = "734139008";
	
	Map<String, RefsetMemberPojo> seRefsetContent = new HashMap<>();
	Map<String, RefsetMemberPojo> spRefsetContent= new HashMap<>();

	public Update_SE_SP_Refsets(JobRun jobRun, ScriptManager mgr) {
		super(jobRun, mgr);
	}

	@Override
	public Job getJob() {
		return new Job()
			.withCategory(new JobCategory(JobType.BATCH_JOB, JobCategory.REFSET_UPDATE))
			.withName("Update SE SP Refsets")
			.withDescription("This job adds entries to the SE and SP refset where new Entire and Part " + 
			"body structure concepts have been created, and removes them when concepts have been inactivated.")
			.withProductionStatus(ProductionStatus.PROD_READY)
			//.withTag(INT)
			.build();
	}

	@Override
	public void runJob() {
		//Can't directly populate a map because SE refset > 10K rows and we don't yet have searchAfter
		//populateRefsetMap(seRefsetContent, SCTID_SE_REFSETID);
		//populateRefsetMap(spRefsetContent, SCTID_SP_REFSETID);
		
		updateRefset(SCTID_SE_REFSETID, "Entire");
		updateRefset(SCTID_SE_REFSETID, "All");
		updateRefset(SCTID_SP_REFSETID, "Part");
		
		//Now see if any inactivated concepts need to be removed
		info("Checking for body structure concepts recently inactivated");
		List<Concept> inactivatedConcepts = tsClient.findUpdatedConcepts(task.getBranchPath(), false, "(body structure)");
		removeInvalidEntries(SCTID_SE_REFSETID, inactivatedConcepts);
		removeInvalidEntries(SCTID_SP_REFSETID, inactivatedConcepts);
	}

	private void updateRefset(String refsetId, String termFilter) {
		String eclFilter = "< 123037004 |Body structure (body structure)|";
		List<Concept> newConcepts = tsClient.findNewConcepts(task.getBranchPath(), eclFilter, termFilter);
		info("Recovered " + newConcepts.size() + " new " + termFilter + " concepts");
		for (Concept c : newConcepts) {
			//Ensure the FSN starts with the term filter
			if (!c.getFsnTerm().startsWith(termFilter + " ")) {
				report (c, Severity.HIGH, ReportActionType.VALIDATION_CHECK, "FSN did not start with expected '" + termFilter + "'");
				continue;
			}
			List<Concept> parents = tsClient.getParents(task.getBranchPath(), c.getId());
			
			//Find the parent which is the structure
			List<Concept> structureParents = parents.stream()
					.filter(p -> p.getFsnTerm().toLowerCase().contains("structure"))
					.collect(Collectors.toList());
			if (structureParents.size() != 1) {
				report (c, Severity.HIGH, ReportActionType.VALIDATION_ERROR, termFilter + " concept has " + structureParents.size() + " structure parents.");
				continue;
			}
			
			//We have our new refset candidate
			RefsetMemberPojo newRM = createMember(refsetId, structureParents.get(0), c);
			report (c, Severity.LOW, ReportActionType.REFSET_MEMBER_ADDED, newRM);
			tsClient.createRefsetMember(task.getBranchPath(), newRM);
		}
	}

	private RefsetMemberPojo createMember(String refsetId, Concept structure, Concept child) {
		RefsetMemberPojo rm = new RefsetMemberPojo()
				.withRefsetId(refsetId)
				.withActive(true)
				.withModuleId(SCTID_CORE_MODULE)
				.withReferencedComponentId(structure.getId());
		rm.getAdditionalFields().setTargetComponentId(child.getId());
		return tsClient.createRefsetMember(task.getBranchPath(), rm);
	}


	private void removeInvalidEntries(String refsetId, List<Concept> inactivatedConcepts) {
		//Find all inactive concepts that are in this list
		//Can't call these in chunks because we'd need targetComponentId to also filter
		//Easier just to recover the whole refset once we have searchAfter available
		//for now we'll work out if we're searching Structure or Part and check 
		//on a per-inactivation basis
		
		/* Map<String, RefsetMemberPojo> refsetContentMap = new HashMap<>();
			for (List<Concept> chunk : Lists.partition(inactivatedConcepts, 100)) {
				for (RefsetMemberPojo rm : tsClient.getRefsetMembers(task.getBranchPath(), refsetId, chunk)) {
					if (refsetContentMap.containsKey(rm.getReferencedComponentId())) {
						throw new IllegalStateException("Already encountered structure concept " + rm.getReferencedComponentId());
					}
					refsetContentMap.put(rm.getReferencedComponentId(), rm);
					
					String target = rm.getAdditionalFields().getTargetComponentId();
					if (refsetContentMap.containsKey(target)) {
						throw new IllegalStateException("Already encountered target concept " + rm.getReferencedComponentId());
					}
					refsetContentMap.put(target, rm);
				}
		}*/
		
		for (Concept c : inactivatedConcepts) {
			List<RefsetMemberPojo> rmToInactivate = null;
			if (isStructure(c)) {
				rmToInactivate = tsClient.findRefsetMemberByReferencedComponentId(task.getBranchPath(), refsetId, c.getId());
			} else if (isPart(c) || isEntire(c)) {
				rmToInactivate = tsClient.findRefsetMemberByTargetComponentId(task.getBranchPath(), refsetId, c.getId());
			}
			
			if (rmToInactivate != null) {
				for (RefsetMemberPojo rm : rmToInactivate) {
					//Did we already inactivate this refset member relating to another concept
					if (rm.isActive()) {
						rm.setActive(false);
						tsClient.updateRefsetMember(project.getBranchPath(), rm);
						report(c, Severity.LOW, ReportActionType.REFSET_MEMBER_REMOVED, rm);
					} else {
						report(c, Severity.LOW, ReportActionType.NO_CHANGE, "RefsetMember previously inactivated", rm);
					}
				}
			}
		}
	}
	
	private boolean isEntire(Concept c) {
		return c.getFsnTerm().startsWith("Entire ");
	}

	private boolean isPart(Concept c) {
		return c.getFsnTerm().startsWith("Part ") || c.getFsnTerm().startsWith("All ");
	}

	private boolean isStructure(Concept c) {
		return c.getFsnTerm().startsWith("Structure ");
	}

}