package org.ihtsdo.otf.transformationandtemplate.service.script;

import org.ihtsdo.otf.exception.TermServerScriptException;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.ConceptPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.ConceptPojo.HistoricalAssociation;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.IConcept;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.RefsetMemberPojo;
import org.ihtsdo.otf.transformationandtemplate.domain.Concept;
import org.ihtsdo.otf.transformationandtemplate.service.client.SnowstormClient.ConceptPage;
import org.ihtsdo.otf.utils.ExceptionUtils;
import org.ihtsdo.otf.utils.SnomedUtils;
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
	Map<String, String> historicalReplacementMap = new HashMap<>();
	
	Set<String> examined = new HashSet<>();
	
	HistoricalAssociation[] associationTypes = new HistoricalAssociation[] { HistoricalAssociation.SAME_AS, HistoricalAssociation.REPLACED_BY, HistoricalAssociation.ALTERNATIVE, HistoricalAssociation.POSSIBLY_EQUIVALENT_TO };

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
			.withName("Update SEP Refsets")
			.withDescription("This job adds entries to the SE and SP refset where new Entire and Part " + 
			"body structure concepts have been created, and removes them when concepts have been inactivated.")
			.withProductionStatus(ProductionStatus.PROD_READY)
			.withParameters(params)
			.build();
	}

	@Override
	public void runJob() throws TermServerScriptException {
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
		flushFiles(false, true);
		
		updateRefset(SCTID_SE_REFSETID, "All");
		percentageComplete(legacy?7:70);
		flushFiles(false, true);
		
		updateRefset(SCTID_SP_REFSETID, "Part");
		percentageComplete(legacy?8:80);
		flushFiles(false, true);
		
		//Now see if any of those historical replacements can be used
		//Load them concepts

		for (Map.Entry<String,String> entry : historicalReplacementMap.entrySet()) {
			String refsetId = entry.getValue();
			ConceptPojo replacement = tsClient.getFullConcept(task.getBranchPath(), entry.getKey()); 
			attemptAddition(refsetId, replacement, false);
		}
		
		if (legacy) {
			checkAllConcepts(10, SCTID_SE_REFSETID, "Entire");
			flushFiles(false, true);
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
		nextNewConcept:
		for (Concept c : concepts) {
			examined.add(c.getId());
			//If it's not a body structure, don't even mention it
			//And I'm not sure why we're seeing these with that eclFilter above!
			if (!c.getFsnTerm().contains("(body structure)")) {
				debug("ECL filter for Body Structure is not doing it's job: " + c);
				continue nextNewConcept;
			}
			//Ensure the FSN starts with the term filter
			if (!c.getFsnTerm().startsWith(termFilter + " ")) {
				report(c, Severity.HIGH, ReportActionType.VALIDATION_CHECK, "FSN did not start with expected '" + termFilter + "'");
				continue nextNewConcept;
			}
			
			boolean isAllConcept = termFilter.equals("All");
			attemptAddition(refsetId, c, isAllConcept);
		}
	}

	private void attemptAddition(String refsetId, IConcept c, boolean isAllConcept) throws TermServerScriptException {
		List<Concept> parents = tsClient.getParents(task.getBranchPath(), c.getConceptId());
		
		//Find the parent which is the structure. Use the FSN with the semtag stripped
		List<Concept> structureParents = parents.stream()
				.filter(p -> SnomedUtils.deconstructFSN(p.getFsnTerm())[0].toLowerCase().contains("structure"))
				.collect(Collectors.toList());
		if (structureParents.size() != 1) {
			report(c, Severity.HIGH, ReportActionType.VALIDATION_ERROR, "Concept has " + structureParents.size() + " structure parents.");
			return;
		}
		
		Concept sConcept = structureParents.get(0);

		//From FRI-186 If this is an 'All' concept and we already have an 'Entire' then the 'All' is redundant
		if (isAllConcept) {
			List<RefsetMemberPojo> rmExisting = tsClient.findRefsetMemberByReferencedComponentId(task.getBranchPath(), refsetId, sConcept.getConceptId(), true);
			if (rmExisting.size() > 0) {
				report(c, Severity.MEDIUM, ReportActionType.VALIDATION_CHECK, "'All' concept considered redundant", rmExisting.get(0));
				return;
			}
		}
		
		//Check if we already have a refset for this 'S' parent which we may wish to inactivate if not
		//appropriate, or refrain from adding more if existing entry is valid
		List<RefsetMemberPojo> rmExisting = tsClient.findRefsetMemberByReferencedComponentId(task.getBranchPath(), refsetId, sConcept.getConceptId(), true);
		for (RefsetMemberPojo rm : rmExisting) {
			String otherChildId = rm.getAdditionalFields().getTargetComponentId();
			//If we're in danger of adding a duplicate, we can skip
			if (c.getConceptId().equals(otherChildId)) {
				report(c, Severity.MEDIUM, ReportActionType.NO_CHANGE, "Skipping attempt to create duplicate", rm);
				return;
			}
			
			//Now does this child REALLY have that structure as an immediate parent?  
			//Inactivate if not, or if so, don't try to add another one for the same 'S'!
			List<Concept> otherParents = tsClient.getParents(task.getBranchPath(), otherChildId);
			boolean isValid = otherParents.stream()
					.anyMatch(op -> op.getId().equals(sConcept.getConceptId()));
			
			if (isValid) {
				report(c, Severity.HIGH, ReportActionType.VALIDATION_ERROR, "Unable to add refset memember due to existing conflicting member", rm);
				return;
			} else {
				removeRefsetMember(c, rm);
				report(c, Severity.HIGH, ReportActionType.REFSET_MEMBER_INACTIVATED, "Removed invalid conflicting member", rm);
			}
		}
		
		//We have our new refset candidate
		RefsetMemberPojo newRM = createMember(refsetId, sConcept, c);
		report(c, Severity.LOW, ReportActionType.REFSET_MEMBER_ADDED, sConcept + " -> " + c, newRM);
		
	}

	private RefsetMemberPojo createMember(String refsetId, IConcept structure, IConcept child) {
		RefsetMemberPojo rm = new RefsetMemberPojo()
				.withRefsetId(refsetId)
				.withActive(true)
				.withModuleId(SCTID_CORE_MODULE)
				.withReferencedComponentId(structure.getConceptId());
		rm.getAdditionalFields().setTargetComponentId(child.getConceptId());
		return tsClient.createRefsetMember(task.getBranchPath(), rm);
	}


	private void removeInvalidEntries(String refsetId, List<Concept> inactivatedConcepts) throws TermServerScriptException {
		//Find all inactive concepts that are in this list
		//Can't call these in chunks because we'd need targetComponentId to also filter
		//Easier just to recover the whole refset once we have searchAfter available
		//for now we'll work out if we're searching Structure or Part and check 
		//on a per-inactivation basis
		
		//TODO This functionality is now available, but the numbers involved don't yet necessitate the
		//peformance improvement
		
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
							//Is there a historical replacement that we should consider adding?
							ConceptPojo fullConcept = tsClient.getFullConcept(task.getBranchPath(), c.getConceptId());
							populateReplacementOrReport(c, fullConcept, rm.getRefsetId());
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
	

	private void populateReplacementOrReport(Concept orig, ConceptPojo fullConcept, String refsetId) throws TermServerScriptException {
		//Do we have any replacement targets?
		for (HistoricalAssociation associationType : associationTypes) {
			Set<String> targets = fullConcept.getAssociationTargets().get(associationType);
			if (targets.size() == 1) {
				historicalReplacementMap.put(targets.iterator().next(), refsetId);
			} else if (targets.size() > 1) {
				String msg = "Concept indicated multiple historical associations";
				report(orig, Severity.HIGH, ReportActionType.VALIDATION_ERROR, msg);
			}
		}
		//If we don't find a replacement, don't worry about it
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
