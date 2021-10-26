package org.ihtsdo.otf.transformationandtemplate.service.script;

import org.ihtsdo.otf.exception.TermServerScriptException;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.RefsetMemberPojo;
import org.ihtsdo.otf.transformationandtemplate.domain.Concept;
import org.ihtsdo.otf.transformationandtemplate.service.client.SnowstormClient.ConceptPage;
import org.ihtsdo.otf.utils.ExceptionUtils;
import org.snomed.otf.scheduler.domain.*;
import org.snomed.otf.scheduler.domain.Job.ProductionStatus;
import org.snomed.otf.scheduler.domain.JobParameter.Type;

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
 * 
 * FRI-299 Adding the option to pick up legacy content as well with a 'legacy' parameter
 * which will check every 'Entire', 'Part' and 'All' BodyStructure to see if they're in the refset.
 */
public class Update_SE_SP_Refsets extends AuthoringPlatformScript implements JobClass {
	
	public static final String SCTID_SE_REFSETID = "734138000";
	public static final String SCTID_SP_REFSETID = "734139008";
	public static final String BODY_STRUCTURE_ECL = "<< 123037004";
	public static final String LEGACY = "Check existing concepts";
	boolean legacy = false;
	
	Map<String, RefsetMemberPojo> seRefsetContent = new HashMap<>();
	Map<String, RefsetMemberPojo> spRefsetContent= new HashMap<>();
	
	Set<String> examined = new HashSet<>();

	public Update_SE_SP_Refsets(JobRun jobRun, ScriptManager mgr) {
		super(jobRun, mgr);
	}

	@Override
	public Job getJob() {
		JobParameters params = new JobParameters()
				.add(LEGACY).withType(Type.BOOLEAN).withDefaultValue(false).withDescription("Check all existing 'Entire', 'Part' and 'All' BodyStructures and attempt to add to the refset if required")
				.build();
		return new Job()
			.withCategory(new JobCategory(JobType.BATCH_JOB, JobCategory.REFSET_UPDATE))
			.withName("Update SE SP Refsets")
			.withDescription("This job adds entries to the SE and SP refset where new Entire and Part " + 
			"body structure concepts have been created, and removes them when concepts have been inactivated.")
			.withProductionStatus(ProductionStatus.PROD_READY)
			.withParameters(params)
			//.withTag(INT)
			.build();
	}

	@Override
	public void runJob() throws TermServerScriptException {
		//Can't directly populate a map because SE refset > 10K rows and we don't yet have searchAfter
		//populateRefsetMap(seRefsetContent, SCTID_SE_REFSETID);
		//populateRefsetMap(spRefsetContent, SCTID_SP_REFSETID);
		
		//Firstly see if any inactivated concepts need to be removed,
		//To ensure the path is clear for any new members to be added
		info("Checking for body structure concepts recently inactivated");
		List<Concept> inactivatedConcepts = tsClient.findUpdatedConcepts(task.getBranchPath(), false, "(body structure)", null);
		legacy = jobRun.getParamBoolean(LEGACY);
				
		info("Checking " + inactivatedConcepts.size() + " inactive body structure concepts");
		removeInvalidEntries(SCTID_SE_REFSETID, inactivatedConcepts);
		removeInvalidEntries(SCTID_SP_REFSETID, inactivatedConcepts);
		percentageComplete(legacy?5:50);
		
		updateRefset(SCTID_SE_REFSETID, "Entire");
		percentageComplete(legacy?6:60);
		updateRefset(SCTID_SE_REFSETID, "All");
		percentageComplete(legacy?7:70);
		updateRefset(SCTID_SP_REFSETID, "Part");
		percentageComplete(legacy?8:80);
		
		if (legacy) {
			checkAllConcepts(10, SCTID_SE_REFSETID, "Entire");
			checkAllConcepts(40, SCTID_SE_REFSETID, "All");
			checkAllConcepts(70, SCTID_SP_REFSETID, "Part");
		}
	}
	
	private void updateRefset(String refsetId, String termFilter) throws TermServerScriptException {
		String eclFilter = "< 123037004 |Body structure (body structure)|";
		List<Concept> newConcepts = tsClient.findNewConcepts(task.getBranchPath(), eclFilter, termFilter);
		info("Recovered " + newConcepts.size() + " new " + termFilter + " concepts");
		updateRefset(refsetId, termFilter, newConcepts);
	}

	private void updateRefset(String refsetId, String termFilter, Collection<Concept> concepts) throws TermServerScriptException {
		for (Concept c : concepts) {
			examined.add(c.getId());
			//If it's not a body structure, don't even mention it
			//And I'm not sure why we're seeing these with that eclFilter above!
			if (!c.getFsnTerm().contains("(body structure)")) {
				debug("ECL filter for Body Structure is not doing it's job: " + c);
				continue;
			}
			//Ensure the FSN starts with the term filter
			if (!c.getFsnTerm().startsWith(termFilter + " ")) {
				report(c, Severity.HIGH, ReportActionType.VALIDATION_CHECK, "FSN did not start with expected '" + termFilter + "'");
				continue;
			}
			List<Concept> parents = tsClient.getParents(task.getBranchPath(), c.getId());
			
			//Find the parent which is the structure
			List<Concept> structureParents = parents.stream()
					.filter(p -> p.getFsnTerm().toLowerCase().contains("structure"))
					.collect(Collectors.toList());
			if (structureParents.size() != 1) {
				report(c, Severity.HIGH, ReportActionType.VALIDATION_ERROR, termFilter + " concept has " + structureParents.size() + " structure parents.");
				continue;
			}
			
			Concept sConcept = structureParents.get(0);
			//From FRI-186 If this is an 'All' concept and we already have an 'Entire' then the 'All' is redundant
			if (termFilter.equals("All")) {
				List<RefsetMemberPojo> rmExisting = tsClient.findRefsetMemberByReferencedComponentId(task.getBranchPath(), refsetId, sConcept.getConceptId(), true);
				if (rmExisting.size() > 0) {
					report(c, Severity.MEDIUM, ReportActionType.VALIDATION_CHECK, "'All' concept considered redundant", rmExisting.get(0));
					continue;
				}
			}
			
			//We have our new refset candidate
			RefsetMemberPojo newRM = createMember(refsetId, sConcept, c);
			report(c, Severity.LOW, ReportActionType.REFSET_MEMBER_ADDED, "", newRM);
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


	private void removeInvalidEntries(String refsetId, List<Concept> inactivatedConcepts) throws TermServerScriptException {
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
		
		//TODO We should find the replacements for these concepts and ensure that they've had
		//rows created for them by the time the job completes.
		for (Concept c : inactivatedConcepts) {
			try {
			List<RefsetMemberPojo> rmToInactivate = null;
				debug ("Checking for existing SEP refset entries for " + c);
				
				if (isStructure(c)) {
					rmToInactivate = tsClient.findRefsetMemberByReferencedComponentId(task.getBranchPath(), refsetId, c.getId(), true);
				} else if (isPart(c) || isEntire(c)) {
					rmToInactivate = tsClient.findRefsetMemberByTargetComponentId(task.getBranchPath(), refsetId, c.getId());
				} else {
					//Need to check all concepts for inactivation, as they might not be clearly S, E or P
					rmToInactivate = tsClient.findRefsetMemberByReferencedComponentId(task.getBranchPath(), refsetId, c.getId(), true);
					if (rmToInactivate == null || rmToInactivate.size() == 0) {
						rmToInactivate = tsClient.findRefsetMemberByTargetComponentId(task.getBranchPath(), refsetId, c.getId());
					}
				}
				
				if (rmToInactivate != null) {
					for (RefsetMemberPojo rm : rmToInactivate) {
						//Did we already inactivate this refset member relating to another concept
						if (rm.isActive()) {
							removeRefsetMember(c, rm);
						} else {
							report(c, Severity.LOW, ReportActionType.NO_CHANGE, "RefsetMember previously inactivated", rm);
						}
					}
				}
			} catch (Exception e) {
				String msg = ExceptionUtils.getExceptionCause("Failed to inactivate any refset entries relating to " + c, e);
				error(msg, e);
				report(c, Severity.HIGH, ReportActionType.API_ERROR, msg);
			}
		}
	}
	

	private void checkAllConcepts(int startingPercentage, String refsetId, String termFilter) throws TermServerScriptException {
		info ("Checking all legacy '" + termFilter + "' concepts for potential inclusion");
		//Loop through all Body Structures containing 'termFilter' text
		//Get published concepts because new ones will already have been examined
		ConceptPage page = tsClient.fetchConceptPage(task.getBranchPath(), true, BODY_STRUCTURE_ECL, termFilter, null);
		long totalExpected = page.getTotal();
		long totalReceived = page.getItems().size();
		
		while (totalReceived < totalExpected) {
			//The term filter isn't "starts with", so we can filter those out.
			//Also filter any concept we've already examined
			Map<String, Concept> conceptMap = page.getItems().stream()
					.filter(c -> c.getFsnTerm().startsWith(termFilter))
					.filter(c -> !examined.contains(c.getConceptId()))
					.collect(Collectors.toMap(Concept::getConceptId, c -> c));
			
			//Do we already have refset members for these concepts?
			List<RefsetMemberPojo> existing = tsClient.findRefsetMemberByTargetComponentIds(task.getBranchPath(), refsetId, conceptMap.keySet());
			debug ( existing.size() + " / " + conceptMap.size() + " '" + termFilter + "' already have refset entries");
			existing.forEach(rm -> conceptMap.remove(rm.getAdditionalFields().getTargetComponentId()));
			
			//Attempt to add these 
			updateRefset(refsetId, termFilter, conceptMap.values());
			
			//How many have we examined?  Can be up to 30% above starting percentage
			percentageComplete(startingPercentage + (int)((totalReceived / (double)totalExpected) * 30d));
			
			//Now get our next page
			page = tsClient.fetchConceptPage(task.getBranchPath(), true, BODY_STRUCTURE_ECL, termFilter, page.getSearchAfter());
			totalReceived += page.getItems().size();
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
