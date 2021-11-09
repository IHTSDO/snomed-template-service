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
	public static final List<String> refsetsOfInterest = new ArrayList<>(); 
	static {
		refsetsOfInterest.add(SCTID_SE_REFSETID);
		refsetsOfInterest.add(SCTID_SP_REFSETID);
	}
	
	public static final String BODY_STRUCTURE_ECL = "<< 123037004";
	public static final String LEGACY = "Check existing concepts";
	boolean legacy = false;
	private static Map<String, String> blockList = new HashMap<>();
	static {
		blockList.put("280432007","243950006"); // 280432007|Structure of region of mediastinum (body structure)| -> 243950006|Entire inferior mediastinum (body structure)|
		blockList.put("64237003","245621003"); // 64237003|Structure of left half of head (body structure)| -> 245621003|Entire primary upper left molar tooth (body structure)|
		blockList.put("29624005","245629001"); // 29624005|Structure of right half of head (body structure)| -> 245629001|Entire primary lower right molar tooth (body structure)|
		blockList.put("279455006","263982003"); // 279455006|Structure of subdivision of penile urethra (body structure)| -> 263982003|Entire distal urethra (body structure)|
		blockList.put("425220002","264481007"); // 425220002|Structure of tributary of popliteal vein (body structure)| -> 264481007|Entire gastrocnemius vein (body structure)|
		blockList.put("279450001","279433003"); // 279450001|Structure of region of male urethra (body structure)| -> 279433003|Entire preprostatic urethra (body structure)|
		blockList.put("310535003","310536002"); // 310535003|Intra-abdominal genital structure (body structure)| -> 310536002|Entire male internal genital organ (body structure)|
		blockList.put("314779008","361077001"); // 314779008|Musculoskeletal structure of larynx (body structure)| -> 361077001|Entire laryngeal bursa (body structure)|
		blockList.put("314260006","361287006"); // 314260006|Structure of bronchiole subdivision (body structure)| -> 361287006|Entire alveolar bronchiole (body structure)|
		blockList.put("118969007","361383007"); // 118969007|Structure of respiratory system subdivision (body structure)| -> 361383007|Entire larynx, trachea, bronchi and lungs, combined site (body structure)|
		blockList.put("129140006","361796005"); // 129140006|Structure of bony skeleton subdivision (body structure)| -> 361796005|Entire bones of ankle (body structure)|
		blockList.put("118971007","373871007"); // 118971007|Structure of digestive system subdivision (body structure)| -> 373871007|Entire gastrointestinal system (body structure)|
		blockList.put("362889002","38266002"); // 362889002|Entire anatomical structure (body structure)| -> 38266002|Entire body as a whole (body structure)|
		blockList.put("123847000","417340007"); // 123847000|Structure of visual system subdivision (body structure)| -> 417340007|Entire pupillomotor system (body structure)|
	}
	
	private enum CacheType { ReferencedComponent, TargetConcept };
	Map<String, Map<String, Set<RefsetMemberPojo>>> refsetMemberByReferencedConceptCache = new HashMap<>();
	Map<String, Map<String, Set<RefsetMemberPojo>>> refsetMemberByTargetConceptCache = new HashMap<>();
	Set<String> cachedMembers = new HashSet<>();
			
	Map<String, ConceptPojo> fullConceptCache = new HashMap<>();
	Map<String, IConcept> miniConceptCache = new HashMap<>();
	
	Map<String, RefsetMemberPojo> seRefsetContent = new HashMap<>();
	Map<String, RefsetMemberPojo> spRefsetContent= new HashMap<>();
	Map<String, RefsetMemberPojo> historicalReplacementMap = new HashMap<>();
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
		
		percentageComplete(legacy?1:10);
		
		info("Checking " + inactivatedConcepts.size() + " inactive body structure concepts");
		removeInvalidEntries(SCTID_SE_REFSETID, inactivatedConcepts);
		percentageComplete(legacy?2:30);
		
		removeInvalidEntries(SCTID_SP_REFSETID, inactivatedConcepts);
		percentageComplete(legacy?3:30);
		
		updateRefset(CacheType.TargetConcept, SCTID_SE_REFSETID, "Entire");
		percentageComplete(legacy?4:40);
		flushFiles(false, true);
		
		updateRefset(CacheType.TargetConcept, SCTID_SE_REFSETID, "All");
		percentageComplete(legacy?5:50);
		flushFiles(false, true);
		
		updateRefset(CacheType.TargetConcept, SCTID_SP_REFSETID, "Part");
		percentageComplete(legacy?6:60);
		
		checkNewStructureConcepts();
		percentageComplete(legacy?7:70);
		
		flushFiles(false, true);
		
		//Now see if any of those historical replacements can be used
		for (Map.Entry<String,RefsetMemberPojo> entry : historicalReplacementMap.entrySet()) {
			createOrUpdateRefsetMember(entry.getValue());
		}
		percentageComplete(legacy?8:80);
		
		if (legacy) {
			checkAllConcepts(10, CacheType.TargetConcept, SCTID_SE_REFSETID, "Entire");
			flushFiles(false, true);
			checkAllConcepts(40, CacheType.TargetConcept, SCTID_SE_REFSETID, "All");
			checkAllConcepts(70, CacheType.TargetConcept, SCTID_SP_REFSETID, "Part");
			flushFiles(false, true);
		}
	}
	
	private RefsetMemberPojo createOrUpdateRefsetMember(RefsetMemberPojo rm) throws TermServerScriptException {
		populateConceptCache(rm.getReferencedComponentId(), rm.getAdditionalFields().getTargetComponentId());
		IConcept sConcept = getConcept(rm.getReferencedComponentId());
		IConcept xConcept =  getConcept(rm.getAdditionalFields().getTargetComponentId()); 
		
		//Is this a black listed pair?
		String blockListedChild = blockList.get(rm.getReferencedComponentId());
		if (blockListedChild == rm.getAdditionalFields().getTargetComponentId()) {
			report(sConcept, Severity.LOW, ReportActionType.SKIPPING, sConcept + " -> " + xConcept, "Blocklisted");
			return null;
		}
		
		boolean create = true;
		rm.setActive(true);
		//If we already have a UUID then we can update
		if (rm.getId() != null) {
			create = false;
		} else {
			//Check if we have a refset member with this referenced component id
			//Either active or inactive
			List<RefsetMemberPojo> rmExisting = getRefsetMembers(CacheType.ReferencedComponent, rm.getRefsetId(), rm.getReferencedComponentId(), null);
			if (rmExisting.size() > 1) {
				report(sConcept, Severity.HIGH, ReportActionType.VALIDATION_ERROR, "Multiple refset entries detected for this 'S' concept");
				return null;
			}
			if (rmExisting.size() == 1) {
				rm.setId(rmExisting.get(0).getId());
				create = false;
			}
		}
		
		ReportActionType action = ReportActionType.REFSET_MEMBER_ADDED;
		if (create) {
			rm = tsClient.createRefsetMember(task.getBranchPath(), rm);
		} else {
			action = ReportActionType.REFSET_MEMBER_MODIFIED;
			rm = tsClient.updateRefsetMember(task.getBranchPath(), rm);
		}
		
		//We can populate both caches separately
		populateMemberCache(rm.getRefsetId(), refsetMemberByReferencedConceptCache, rm.getReferencedComponentId(), rm);
		populateMemberCache(rm.getRefsetId(), refsetMemberByTargetConceptCache, rm.getAdditionalFields().getTargetComponentId(), rm);
		report(sConcept, Severity.LOW,action, sConcept + " -> " + xConcept, rm);
		return rm;
	}

	private void updateRefset(CacheType cacheType, String refsetId, String termFilter) throws TermServerScriptException {
		String eclFilter = "< 123037004 |Body structure (body structure)|";
		List<Concept> newConcepts = tsClient.findNewConcepts(task.getBranchPath(), eclFilter, termFilter);
		info("Recovered " + newConcepts.size() + " new " + termFilter + " concepts");
		if (newConcepts.size() > 0) {
			updateRefset(cacheType, refsetId, termFilter, newConcepts);
		}
	}
	
	private void checkNewStructureConcepts() throws TermServerScriptException {
		String eclFilter = "< 123037004 |Body structure (body structure)|";
		List<Concept> newSConcepts = tsClient.findNewConcepts(task.getBranchPath(), eclFilter, "structure")
				.stream()
				.filter(c -> isStructure(c))
				.collect(Collectors.toList());
		populateMemberCache(CacheType.ReferencedComponent, newSConcepts, false);
		info("Recovered " + newSConcepts.size() + " new structure concepts");
		for (Concept c : newSConcepts) {
			final List<Concept> children = tsClient.getChildren(task.getBranchPath(), c.getConceptId());
			if (children.size() == 0) {
				report(c, Severity.MEDIUM, ReportActionType.SKIPPING, "New 'S' concept featured 0 children.");
			} else {
				checkForAvailableChildren(c, "Entire", true, SCTID_SE_REFSETID, children);
				checkForAvailableChildren(c, "Part", false, SCTID_SP_REFSETID, children);
				checkForAvailableChildren(c, "All", false, SCTID_SP_REFSETID, children);
			}
		}
	}
	
	private void checkForAvailableChildren(Concept c, String termFilter, boolean isEntire, String refsetId, List<Concept> children) throws TermServerScriptException {
		//Do we already have a refset entry for this S concept?
		List<RefsetMemberPojo> rmExisting = getRefsetMembers(CacheType.ReferencedComponent, refsetId, c.getConceptId(), true);
		if (rmExisting.size() > 0) {
			return;
		}
		List<Concept> validChildren = children.stream()
				.filter(child -> isEntire?isEntire(child):isPart(child))
				.collect(Collectors.toList());
		if (validChildren.size() == 1) {
			//There are no existing Refset entries for this concept and it only has one child, so safe to add
			RefsetMemberPojo newRM = new RefsetMemberPojo()
					.withRefsetId(refsetId)
					.withActive(true)
					.withModuleId(SCTID_CORE_MODULE)
					.withReferencedComponentId(c.getId());
			newRM.getAdditionalFields().setTargetComponentId(validChildren.get(0).getId());
			createOrUpdateRefsetMember(newRM);
		} else {
			//Don't worry about having 0 P/A children, just return quietly
			if (!isEntire && validChildren.size() == 0) {
				return;
			}
			report(c, Severity.MEDIUM, ReportActionType.SKIPPING, "New 'S' concept featured " + validChildren.size() + " '" + (isEntire?"E":"P/A") + "' children.");
		}
	}

	private void updateRefset(CacheType cacheType, String refsetId, String termFilter, Collection<Concept> concepts) throws TermServerScriptException {
		if (concepts.size() == 0) {
			return;
		}
		populateMemberCache(cacheType, concepts, false);
		
		//Let's also get all the parents for those concepts and pre-populate the ReferencedComponentId caches
		List<Concept> allParents = tsClient.getParents(task.getBranchPath(), concepts);
		populateMemberCache(CacheType.ReferencedComponent, allParents, false);
		
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
			attemptXAddition(refsetId, c, isAllConcept);
		}
	}

	private void attemptXAddition(String refsetId, IConcept c, boolean isAllConcept) throws TermServerScriptException {
		List<Concept> parents;
		try {
			parents = tsClient.getParents(task.getBranchPath(), c.getConceptId());
		} catch (Exception e) {
			report(c, Severity.HIGH, ReportActionType.API_ERROR, ExceptionUtils.getExceptionCause("Unable to recover parents", e));
			return;
		}
		//Find the parent which is the structure. Use the FSN with the semtag stripped
		List<Concept> structureParents = parents.stream()
				.filter(p -> SnomedUtils.deconstructFSN(p.getFsnTerm())[0].toLowerCase().contains("structure"))
				.collect(Collectors.toList());
		if (structureParents.size() != 1) {
			report(c, Severity.MEDIUM, ReportActionType.VALIDATION_ERROR, "Concept has " + structureParents.size() + " structure parents.");
			return;
		}
		
		Concept sConcept = structureParents.get(0);

		//From FRI-186 If this is an 'All' concept and we already have an 'Entire' then the 'All' is redundant
		if (isAllConcept) {
			List<RefsetMemberPojo> rmExisting = getRefsetMembers(CacheType.ReferencedComponent, refsetId, sConcept.getConceptId(), true);
			if (rmExisting.size() > 0) {
				report(c, Severity.MEDIUM, ReportActionType.VALIDATION_CHECK, "'All' concept considered redundant", rmExisting.get(0));
				return;
			}
		}
		
		//Check if we already have a refset for this 'S' parent which we may wish to inactivate if not
		//appropriate, or refrain from adding more if existing entry is valid
		List<RefsetMemberPojo> rmExisting = getRefsetMembers(CacheType.ReferencedComponent, refsetId, sConcept.getConceptId(), true);
		for (RefsetMemberPojo rm : rmExisting) {
			String otherChildId = rm.getAdditionalFields().getTargetComponentId();
			//If we're in danger of adding a duplicate, we can skip
			if (c.getConceptId().equals(otherChildId)) {
				report(c, Severity.MEDIUM, ReportActionType.NO_CHANGE, "Skipping attempt to create duplicate", rm);
				return;
			}
			
			//Now does this child REALLY have that structure as an immediate parent?  
			//Inactivate if not, or if so, don't try to add another one for the same 'S'!
			List<Concept> otherParents;
			try {
				otherParents = tsClient.getParents(task.getBranchPath(), otherChildId);
			} catch (Exception e) {
				report(c, Severity.HIGH, ReportActionType.API_ERROR, ExceptionUtils.getExceptionCause("Unable to recover parents", e));
				return;
			}
			boolean isValid = otherParents.stream()
					.anyMatch(op -> op.getId().equals(sConcept.getConceptId()));
			
			if (isValid) {
				report(c, Severity.HIGH, ReportActionType.VALIDATION_ERROR, "Unable to add due to existing refset member for same 'S' parent.", rm);
				return;
			} else {
				removeRefsetMember(c, rm);
				report(c, Severity.HIGH, ReportActionType.REFSET_MEMBER_INACTIVATED, "Removed invalid conflicting member", rm);
			}
		}
		
		//We have our new refset candidate
		RefsetMemberPojo rm = new RefsetMemberPojo()
				.withRefsetId(refsetId)
				.withActive(true)
				.withModuleId(SCTID_CORE_MODULE)
				.withReferencedComponentId(sConcept.getConceptId());
		rm.getAdditionalFields().setTargetComponentId(c.getConceptId());
		createOrUpdateRefsetMember(rm);
	}

	private void removeInvalidEntries(String refsetId, List<Concept> inactivatedConcepts) throws TermServerScriptException {
		//Attempt to populate both caches with all concept incase they're not well named
		populateMemberCache(CacheType.ReferencedComponent, inactivatedConcepts, false);
		populateMemberCache(CacheType.TargetConcept, inactivatedConcepts, false);
		
		for (Concept c : inactivatedConcepts) {
			try {
			List<RefsetMemberPojo> rmToInactivate = null;
				debug ("Checking for existing SEP refset entries for " + c);
				if (isStructure(c)) {
					rmToInactivate = getRefsetMembers(CacheType.ReferencedComponent, refsetId, c.getId(), true);
				} else if (isPart(c) || isEntire(c)) {
					rmToInactivate = getRefsetMembers(CacheType.TargetConcept, refsetId, c.getId(), true);
				} else {
					//Need to check all concepts for inactivation, as they might not be clearly S, E or P
					rmToInactivate = getRefsetMembers(CacheType.ReferencedComponent, refsetId, c.getId(), true);
					if (rmToInactivate == null || rmToInactivate.size() == 0) {
						rmToInactivate = getRefsetMembers(CacheType.TargetConcept, refsetId, c.getId(), true);
					}
				}
				
				if (rmToInactivate != null) {
					for (RefsetMemberPojo rm : rmToInactivate) {
						//Did we already inactivate this refset member relating to another concept
						if (rm.isActive()) {
							RefsetMemberPojo updatedRefsetMember = removeRefsetMember(c, rm);
							if (updatedRefsetMember == null) {
								unPopulateMemberCache(rm.getRefsetId(), refsetMemberByReferencedConceptCache, rm.getReferencedComponentId(), rm);
								unPopulateMemberCache(rm.getRefsetId(), refsetMemberByTargetConceptCache, rm.getAdditionalFields().getTargetComponentId(), rm);
							} else {
								populateMemberCache(rm.getRefsetId(), refsetMemberByReferencedConceptCache, rm.getReferencedComponentId(), rm);
								populateMemberCache(rm.getRefsetId(), refsetMemberByTargetConceptCache, rm.getAdditionalFields().getTargetComponentId(), rm);
							}
							//Is there a historical replacement that we should consider adding?
							ConceptPojo fullConcept = tsClient.getFullConcept(task.getBranchPath(), c.getConceptId());
							populateReplacementOrReport(fullConcept, rm);
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
	
	private List<RefsetMemberPojo> getRefsetMembers(CacheType cacheType, String refsetId, String id, Boolean active) {
		return getRefsetMembers(cacheType, refsetId, Collections.singleton(id), active);
	}

	private List<RefsetMemberPojo> getRefsetMembers(CacheType cacheType, String refsetId, Set<String> ids, Boolean active) {
		Map<String, Map<String, Set<RefsetMemberPojo>>> caches = null;
		switch (cacheType) {
			case ReferencedComponent: 
				caches = refsetMemberByReferencedConceptCache;
				break;
			case TargetConcept: 
				caches = refsetMemberByTargetConceptCache;
		}
		
		final Map<String, Set<RefsetMemberPojo>> refsetCache = caches.get(refsetId);
		//Check if we tried to populate the cache for these ids, even if it returned no members
		Set<String> missedCache = ids.stream()
				.filter(s -> !refsetCache.containsKey(s))
				.collect(Collectors.toSet());
		
		if (missedCache.size() > 0) {
			String missedCacheStr = missedCache.stream().collect(Collectors.joining(", "));
			warn(cacheType + " refset cache miss for " + missedCacheStr);
			populateMemberCache(cacheType, missedCache, false);
		}
		
		//Odd data situation where cache might not get populated if it was already populated
		//See a33f49e9-87cd-4af7-9f74-c94957095753 with E concept in place of S
		missedCache = ids.stream()
				.filter(s -> !refsetCache.containsKey(s))
				.collect(Collectors.toSet());
		populateMemberCache(cacheType, missedCache, true);
		
		List<RefsetMemberPojo> members = new ArrayList<>();
		for (String id : ids) {
			for (RefsetMemberPojo rm : refsetCache.get(id)) {
				if (active == null || rm.isActive() == active) {
					members.add(rm);
				}
			}
		}
		return members;
	}
	
	private void populateReplacementOrReport(ConceptPojo fullConcept, RefsetMemberPojo rm) throws TermServerScriptException {
		//Do we have any replacement targets?
		for (HistoricalAssociation associationType : associationTypes) {
			if (fullConcept.getAssociationTargets() == null) {
				report(fullConcept, Severity.HIGH, ReportActionType.VALIDATION_ERROR, "No associations available");
				return;
			}
			Set<String> targets = fullConcept.getAssociationTargets().get(associationType);
			if (targets != null) {
				if (targets.size() == 1) {
					String replacement = targets.iterator().next();
					RefsetMemberPojo proposed = rm.clone();
					//Are we suggesting a replacement for the S or X concept?
					if (isStructure(fullConcept)) {
						//If we change the referenced component id then we need a new UUID, so leave blank
						proposed.setReferencedComponentId(replacement);
					} else {
						//If we modify the target, then we can re-use the existing row
						proposed.setId(rm.getId());
						proposed.getAdditionalFields().setTargetComponentId(replacement);
					}
					historicalReplacementMap.put(replacement, proposed);
				} else if (targets.size() > 1) {
					String msg = "Concept indicated multiple historical associations";
					report(fullConcept, Severity.HIGH, ReportActionType.VALIDATION_ERROR, msg);
				}
			}
		}
		//If we don't find a replacement, don't worry about it
	}

	private void checkAllConcepts(int startingPercentage, CacheType cacheType, String refsetId, String termFilter) throws TermServerScriptException {
		info ("Checking all legacy '" + termFilter + "' concepts for potential inclusion");
		//Loop through all Body Structures containing 'termFilter' text
		//Get published concepts because new ones will already have been examined
		ConceptPage page = tsClient.fetchConceptPage(task.getBranchPath(), true, BODY_STRUCTURE_ECL, termFilter, null);
		long totalExpected = page.getTotal();
		long totalReceived = page.getItems().size();
		
		/*if (page.getItems().stream().anyMatch(c -> c.getId().equals("182028002"))) {
			debug("here");
		}*/
		
		while (totalReceived < totalExpected) {
			//The term filter isn't "starts with", so we can filter those out.
			//Also filter any concept we've already examined
			Map<String, Concept> conceptMap = page.getItems().stream()
					.filter(c -> c.getFsnTerm().startsWith(termFilter))
					.filter(c -> !examined.contains(c.getConceptId()))
					.collect(Collectors.toMap(Concept::getConceptId, c -> c));
			
			//Do we already have refset members for these concepts?
			//List<RefsetMemberPojo> existing = tsClient.findRefsetMemberByTargetComponentIds(task.getBranchPath(), refsetId, conceptMap.keySet(), true);
			List<RefsetMemberPojo> existing =  getRefsetMembers(CacheType.TargetConcept, refsetId, conceptMap.keySet(), true);
			debug (existing.size() + " / " + conceptMap.size() + " '" + termFilter + "' already have refset entries");
			existing.forEach(rm -> conceptMap.remove(rm.getAdditionalFields().getTargetComponentId()));
			
			//Attempt to add these 
			updateRefset(cacheType, refsetId, termFilter, conceptMap.values());
			
			//How many have we examined?  Can be up to 30% above starting percentage
			percentageComplete(startingPercentage + (int)((totalReceived / (double)totalExpected) * 30d));
			
			//Now get our next page
			page = tsClient.fetchConceptPage(task.getBranchPath(), true, BODY_STRUCTURE_ECL, termFilter, page.getSearchAfter());
			totalReceived += page.getItems().size();
		}
	}

	private boolean isEntire(IConcept c) {
		return c.getFsnTerm().startsWith("Entire ");
	}

	private boolean isPart(IConcept c) {
		return c.getFsnTerm().startsWith("Part ") || c.getFsnTerm().startsWith("All ");
	}

	private boolean isStructure(IConcept c) {
		//Don't look at the semantic tag
		String term = c.getFsnTerm().replace("(body structure)", "");
		return term.startsWith("Structure ") || 
				(term.contains("structure") && !isEntire(c) && !isPart(c));
	}
	
	private void populateMemberCache(CacheType cacheType, Collection<Concept> concepts, boolean forceRefresh) {
		populateMemberCache(cacheType, concepts.stream().map(c -> c.getId()).collect(Collectors.toSet()), forceRefresh);
	}
	
	private void populateMemberCache(CacheType cacheType, Set<String> sctIds, boolean forceRefresh) {
		if (sctIds == null || sctIds.size() == 0) {
			return;
		}
		
		if (!forceRefresh) {
			//No need to populate any member we already have cached
			sctIds.removeAll(cachedMembers);
		} else {
			String sctIdsStr = sctIds.stream().collect(Collectors.joining(", "));
			warn (cacheType + " forced refresh of " + sctIdsStr + " requires investigation");
		}
		
		//If we've no sctIds left to find, then job done!
		if (sctIds.size() == 0) {
			return;
		}
		
		//Pre-populate the caches with empty sets so we know - if they're empty - that we checked
		if (cacheType == CacheType.ReferencedComponent) {
			prePopulateMemberCaches(refsetMemberByReferencedConceptCache, sctIds);
		} else {
			prePopulateMemberCaches(refsetMemberByTargetConceptCache, sctIds);
		}
		
		//Get all refset members for these concepts and then pick out the ones we're interested in
		List<RefsetMemberPojo> members = new ArrayList<>();
		switch (cacheType) {
			case ReferencedComponent: 
				members =  tsClient.findRefsetMemberByReferencedComponentIds(task.getBranchPath(), null, sctIds, null);
				break;
			case TargetConcept: 
				members = tsClient.findRefsetMemberByTargetComponentIds(task.getBranchPath(), null, sctIds, null);
		}
		
		//Now regardless of which type of search we're doing, we can actually also populate the other cache
		//at the same time
		for (RefsetMemberPojo member : members) {
			if (refsetsOfInterest.contains(member.getRefsetId())) {
				populateMemberCache(member.getRefsetId(), refsetMemberByReferencedConceptCache, member.getReferencedComponentId(), member);
				populateMemberCache(member.getRefsetId(), refsetMemberByTargetConceptCache, member.getAdditionalFields().getTargetComponentId(), member);
			}
			//If we've got a referencedComponent we can also grab the FSN while we have it!
			if (member.getReferencedComponent() != null) {
				miniConceptCache.put(member.getReferencedComponentId(), member.getReferencedComponent());
			}
		}
		debug("Populated caches with " + members.size() + " refset members");
	}

	private void prePopulateMemberCaches(Map<String, Map<String, Set<RefsetMemberPojo>>> caches, Set<String> sctIds) {
		for (String refsetId : refsetsOfInterest) {
			Map<String, Set<RefsetMemberPojo>> refsetCache = caches.get(refsetId);
			if (refsetCache == null) {
				refsetCache = new HashMap<>();
				caches.put(refsetId, refsetCache);
			}
			
			for (String key : sctIds) {
				Set<RefsetMemberPojo> members = refsetCache.get(key);
				if (members == null) {
					members = new HashSet<>();
					refsetCache.put(key, members);
				}
			}
		}
	}

	private void populateMemberCache(String refsetId,
			Map<String, Map<String, Set<RefsetMemberPojo>>> caches,
			String key, RefsetMemberPojo member) {
		cachedMembers.add(key);
		prePopulateMemberCaches(caches, Collections.singleton(key));
		Map<String, Set<RefsetMemberPojo>> refsetCache = caches.get(refsetId);
		Set<RefsetMemberPojo> members = refsetCache.get(key);
		members.add(member);
	}
	
	private void unPopulateMemberCache(String refsetId,
			Map<String, Map<String, Set<RefsetMemberPojo>>> caches,
			String key, RefsetMemberPojo member) {
		cachedMembers.remove(key);
		Map<String, Set<RefsetMemberPojo>> refsetCache = caches.get(refsetId);
		Set<RefsetMemberPojo> members = refsetCache.get(key);
		if (members != null) {
			members.remove(member);
		}
	}

	private IConcept getConcept(String sctId) {
		if (!miniConceptCache.containsKey(sctId)) {
			warn ("Cache miss for " + sctId);
			populateConceptCache(sctId);
		}
		return miniConceptCache.get(sctId);
	}

	private void populateConceptCache(String... sctIds) {
		//List needs to be modifiable, so create afresh
		populateConceptCache(new ArrayList<String>(Arrays.asList(sctIds)));
	}

	private void populateConceptCache(List<String> sctIds) {
		//Remove any concepts we already have cachced
		sctIds.removeAll(miniConceptCache.keySet());
		for (Concept c : tsClient.getConcepts(task.getBranchPath(), sctIds)) {
			miniConceptCache.put(c.getConceptId(), c);
		}
	}


}
