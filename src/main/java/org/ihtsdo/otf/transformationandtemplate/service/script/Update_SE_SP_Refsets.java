package org.ihtsdo.otf.transformationandtemplate.service.script;

import org.ihtsdo.otf.exception.TermServerScriptException;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.ConceptMiniPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.ConceptPojo;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.ConceptPojo.HistoricalAssociation;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.IConcept;
import org.ihtsdo.otf.rest.client.terminologyserver.pojo.RefsetMemberPojo;
import org.ihtsdo.otf.transformationandtemplate.domain.Concept;
import org.ihtsdo.otf.transformationandtemplate.service.client.SnowstormClient.ConceptPage;
import org.ihtsdo.otf.transformationandtemplate.service.script.ScriptManager.ConfigItem;
import org.ihtsdo.otf.utils.ExceptionUtils;
import org.ihtsdo.otf.utils.SnomedUtilsBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snomed.otf.scheduler.domain.*;
import org.snomed.otf.scheduler.domain.Job.ProductionStatus;
import org.snomed.otf.scheduler.domain.JobParameter.Type;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

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
 * which will check every 'Entire' (or 'All') and 'Part' BodyStructure to see if they're in the refset.
 */
public class Update_SE_SP_Refsets extends AuthoringPlatformScript implements JobClass {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public static final String SCTID_SE_REFSETID = "734138000";
	public static final String SCTID_SP_REFSETID = "734139008";
	public static final List<String> refsetsOfInterest = new ArrayList<>(); 
	static {
		refsetsOfInterest.add(SCTID_SE_REFSETID);
		refsetsOfInterest.add(SCTID_SP_REFSETID);
	}
	
	public static final int MAX_ISSUES = 5;
	
	public static final String BODY_STRUCTURE_ECL = "<< 123037004";
	public static final String LEGACY = "Check existing concepts";
	private static final Multimap<String, String> blockList = ArrayListMultimap.create();
	static {
		blockList.put("280432007","243950006"); // 280432007|Structure of region of mediastinum (body structure)| -> 243950006|Entire inferior mediastinum (body structure)|
		blockList.put("64237003","245621003"); // 64237003|Structure of left half of head (body structure)| -> 245621003|Entire primary upper left molar tooth (body structure)|
		blockList.put("29624005","245629001"); // 29624005|Structure of right half of head (body structure)| -> 245629001|Entire primary lower right molar tooth (body structure)|
		blockList.put("279455006","263982003"); // 279455006|Structure of subdivision of penile urethra (body structure)| -> 263982003|Entire distal urethra (body structure)|
		blockList.put("425220002","264481007"); // 425220002|Structure of region of male urethra (body structure)| -> 279433003|Entire preprostatic urethra (body structure)|
		blockList.put("310535003","310536002"); // 310535003|Intra-abdominal genital structure (body structure)| -> 310536002|Entire male internal genital organ (body structure)|
		blockList.put("314779008","361077001"); // 314779008|Musculoskeletal structure of larynx (body structure)| -> 361077001|Entire laryngeal bursa (body structure)|
		blockList.put("314260006","361287006"); // 314260006|Structure of bronchiole subdivision (body structure)| -> 361287006|Entire alveolar bronchiole (body structure)|
		blockList.put("118969007","361383007"); // 118969007|Structure of respiratory system subdivision (body structure)| -> 361383007|Entire larynx, trachea, bronchi and lungs, combined site (body structure)|
		blockList.put("129140006","361796005"); // 129140006|Structure of bony skeleton subdivision (body structure)| -> 361796005|Entire bones of ankle (body structure)|
		blockList.put("118971007","373871007"); // 118971007|Structure of digestive system subdivision (body structure)| -> 373871007|Entire gastrointestinal system (body structure)|
		blockList.put("362889002","38266002"); // 362889002|Entire anatomical structure (body structure)| -> 38266002|Entire body as a whole (body structure)|
		blockList.put("123847000","417340007"); // 123847000|Structure of visual system subdivision (body structure)| -> 417340007|Entire pupillomotor system (body structure)|
		blockList.put("123847000","417340007");  //123847000|Structure of visual system subdivision (body structure)| -> 417340007|Entire pupillomotor system (body structure)|
		blockList.put("425220002","264481007");  //425220002|Structure of tributary of popliteal vein (body structure)| -> 264481007|Entire gastrocnemius vein (body structure)|
		blockList.put("279455006","263982003");  //279455006|Structure of subdivision of penile urethra (body structure)| -> 263982003|Entire distal urethra (body structure)|
		blockList.put("4583008","244851000");  //4583008|Structure of splenius muscle of trunk (body structure)| -> 244851000|Entire splenius cervicis muscle (body structure)|
		blockList.put("29624005","245629001");  //29624005|Structure of right half of head (body structure)| -> 245629001|Entire primary lower right molar tooth (body structure)|
		blockList.put("29624005","245614003");  //29624005|Structure of right half of head (body structure)| -> 245614003|Entire primary upper right molar tooth (body structure)|
		blockList.put("29624005","245589006");  //29624005|Structure of right half of head (body structure)| -> 245589006|Entire permanent lower right molar tooth (body structure)|
		blockList.put("29624005","245633008");  //29624005|Structure of right half of head (body structure)| -> 245633008|Entire primary lower right incisor tooth (body structure)|
		blockList.put("29624005","245618000");  //29624005|Structure of right half of head (body structure)| -> 245618000|Entire primary upper right incisor tooth (body structure)|
		blockList.put("29624005","245565004");  //29624005|Structure of right half of head (body structure)| -> 245565004|Entire permanent upper right molar tooth (body structure)|
		blockList.put("29624005","245573008");  //29624005|Structure of right half of head (body structure)| -> 245573008|Entire permanent upper right incisor tooth (body structure)|
		blockList.put("29624005","245598009");  //29624005|Structure of right half of head (body structure)| -> 245598009|Entire permanent lower right incisor tooth (body structure)|
		blockList.put("118969007","361383007");  //118969007|Structure of respiratory system subdivision (body structure)| -> 361383007|Entire larynx, trachea, bronchi and lungs, combined site (body structure)|
		blockList.put("280432007","243950006");  //280432007|Structure of region of mediastinum (body structure)| -> 243950006|Entire inferior mediastinum (body structure)|
		blockList.put("279450001","279433003");  //279450001|Structure of region of male urethra (body structure)| -> 279433003|Entire preprostatic urethra (body structure)|
		blockList.put("64237003","245621003");  //64237003|Structure of left half of head (body structure)| -> 245621003|Entire primary upper left molar tooth (body structure)|
		blockList.put("64237003","245636000");  //64237003|Structure of left half of head (body structure)| -> 245636000|Entire primary lower left molar tooth (body structure)|
		blockList.put("64237003","245640009");  //64237003|Structure of left half of head (body structure)| -> 245640009|Entire primary lower left incisor tooth (body structure)|
		blockList.put("64237003","245625007");  //64237003|Structure of left half of head (body structure)| -> 245625007|Entire primary upper left incisor tooth (body structure)|
		blockList.put("64237003","245576000");  //64237003|Structure of left half of head (body structure)| -> 245576000|Entire permanent upper left molar tooth (body structure)|
		blockList.put("64237003","245601004");  //64237003|Structure of left half of head (body structure)| -> 245601004|Entire permanent lower left molar tooth (body structure)|
		blockList.put("64237003","245609002");  //64237003|Structure of left half of head (body structure)| -> 245609002|Entire permanent lower left incisor tooth (body structure)|
		blockList.put("64237003","245585000");  //64237003|Structure of left half of head (body structure)| -> 245585000|Entire permanent upper left incisor tooth (body structure)|
		blockList.put("118971007","373871007");  //118971007|Structure of digestive system subdivision (body structure)| -> 373871007|Entire gastrointestinal system (body structure)|
		blockList.put("314260006","361287006");  //314260006|Structure of bronchiole subdivision (body structure)| -> 361287006|Entire alveolar bronchiole (body structure)|
		blockList.put("129140006","361796005");  //129140006|Structure of bony skeleton subdivision (body structure)| -> 361796005|Entire bones of ankle (body structure)|
		blockList.put("129168005","244534003");  //129168005|Joint structure of sacrum (body structure)| -> 244534003|Entire sacral intervertebral symphysis (body structure)|
		blockList.put("310535003","310536002");  //310535003|Intra-abdominal genital structure (body structure)| -> 310536002|Entire male internal genital organ (body structure)|
		blockList.put("362889002","38266002");  //362889002|Entire anatomical structure (body structure)| -> 38266002|Entire body as a whole (body structure)|
	}
	
	private enum CacheType { ReferencedComponent, TargetConcept }
	private final Map<String, Map<String, Set<RefsetMemberPojo>>> refsetMemberByReferencedConceptCache = new HashMap<>();
	private final Map<String, Map<String, Set<RefsetMemberPojo>>> refsetMemberByTargetConceptCache = new HashMap<>();
	private final Set<String> cachedMembers = new HashSet<>();
	private final Map<String, IConcept> miniConceptCache = new HashMap<>();
	
	private final Map<String, RefsetMemberPojo> historicalReplacementMap = new HashMap<>();
	private Set<String> examined = new HashSet<>();
	private Set<String> outOfScope = new HashSet<>();
	
	private final HistoricalAssociation[] associationTypes = new HistoricalAssociation[] { HistoricalAssociation.SAME_AS, HistoricalAssociation.REPLACED_BY, HistoricalAssociation.ALTERNATIVE, HistoricalAssociation.POSSIBLY_EQUIVALENT_TO };

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
		populateScopeExclusions();
		//Firstly see if any inactivated concepts need to be removed,
		//To ensure the path is clear for any new members to be added
		logger.info("Checking for body structure concepts recently inactivated");
		List<Concept> inactivatedConcepts = tsClient.findUpdatedConcepts(task.getBranchPath(), false, true, "(body structure)", null); //inactive and published
		boolean legacy = jobRun.getParamBoolean(LEGACY);
		
		percentageComplete(legacy ?1:10);

		logger.info("Checking {} inactive body structure concepts", inactivatedConcepts.size());
		removeInvalidEntries(SCTID_SE_REFSETID, inactivatedConcepts);
		percentageComplete(legacy ?2:30);
		
		removeInvalidEntries(SCTID_SP_REFSETID, inactivatedConcepts);
		percentageComplete(legacy ?3:30);
		
		updateRefset(CacheType.TargetConcept, SCTID_SE_REFSETID, "Entire");
		percentageComplete(legacy ?4:40);
		flushFiles(false);
		
		updateRefset(CacheType.TargetConcept, SCTID_SE_REFSETID, "All");
		percentageComplete(legacy ?5:50);
		flushFiles(false);
		
		updateRefset(CacheType.TargetConcept, SCTID_SP_REFSETID, "Part");
		percentageComplete(legacy ?6:60);
		
		checkNewStructureConcepts(); //Also recently reactivated
		percentageComplete(legacy ?7:70);
		
		flushFiles(false);
		
		//Now see if any of those historical replacements can be used
		for (Map.Entry<String,RefsetMemberPojo> entry : historicalReplacementMap.entrySet()) {
			createOrUpdateRefsetMember(entry.getValue());
		}
		percentageComplete(legacy ?8:80);
		
		if (legacy) {
			checkAllConcepts(10, CacheType.TargetConcept, SCTID_SE_REFSETID, "Entire");
			flushFiles(false);
			checkAllConcepts(40, CacheType.TargetConcept, SCTID_SE_REFSETID, "All");
			checkAllConcepts(70, CacheType.TargetConcept, SCTID_SP_REFSETID, "Part");
		}

		flushFiles(false);
	}
	
	private void populateScopeExclusions() throws TermServerScriptException {
		logger.info("Populate scope exclusions");
		try {
			List<Concept> conceptOOS = tsClient.conceptsByECL(task.getBranchPath(), mgr.getConfig(ConfigItem.SEP_OUT_OF_SCOPE));
			outOfScope = conceptOOS.stream()
					.map(ConceptMiniPojo::getConceptId)
					.collect(Collectors.toSet());
			logger.info ("Cached {} concepts as being out of scope", outOfScope.size());
		} catch (Exception e) {
			String msg = "ECL for scope exclusion failed.  Check template-service.script.SEP.out-of-scope in application.properties";
			throw new TermServerScriptException(msg, e);
		}
	}

	private RefsetMemberPojo createOrUpdateRefsetMember(RefsetMemberPojo rm) throws TermServerScriptException {
		populateConceptCache(rm.getReferencedComponentId(), rm.getAdditionalFields().getTargetComponentId());
		IConcept sConcept = getConcept(rm.getReferencedComponentId());
		IConcept xConcept = getConcept(rm.getAdditionalFields().getTargetComponentId());

		if (Objects.equals(rm.getReferencedComponentId(), rm.getAdditionalFields().getTargetComponentId())) {
			String sConceptId = sConcept.getConceptId() + "|" + sConcept.getFsnTerm() + "|";
			report(sConcept, Severity.MEDIUM, ReportActionType.SKIPPING, String.format("ReferenceSetMember cannot be updated as it would result in %s being linked to itself.", sConceptId));
			return null;
		}

		if (!isBodyStructure(sConcept)) {
			String sConceptId = sConcept.getConceptId() + "|" + sConcept.getFsnTerm() + "|";
			String xConceptId = xConcept.getConceptId() + "|" + xConcept.getFsnTerm() + "|";
			report(xConcept, Severity.MEDIUM, ReportActionType.SKIPPING, String.format("%s is not a body structure so cannot be used as a replacement for %s", sConceptId, xConceptId));
			return null;
		}
		
		//Is one of these concepts out of scope?
		if (outOfScope.contains(sConcept.getConceptId()) || outOfScope.contains(xConcept.getConceptId())) {
			report(sConcept, Severity.LOW, ReportActionType.SKIPPING, sConcept + " -> " + xConcept, "Out of Scope");
			return null;
		}
		
		//Or is this a black listed pair?
		Collection<String> blockListedChildren = blockList.get(rm.getReferencedComponentId());
		if (blockListedChildren.contains(rm.getAdditionalFields().getTargetComponentId())) {
			report(sConcept, Severity.LOW, ReportActionType.SKIPPING, sConcept + " -> " + xConcept, "Blocklisted");
			return null;
		}
		
		
		boolean create = true;
		rm.setActive(true);
		List<RefsetMemberPojo> rmExistingList = getRefsetMembers(CacheType.ReferencedComponent, rm.getRefsetId(), rm.getReferencedComponentId(), null);
		//Or maybe we already have updated it?
		if (rmExistingList.size() == 1) {
			RefsetMemberPojo rmExisting = rmExistingList.get(0);
			//Do we in fact need to make any changes at all?
			if (rmExisting.isActive() && 
					rmExisting.getAdditionalFields().getTargetComponentId().equals(xConcept.getConceptId())) {
				report(sConcept, Severity.LOW, ReportActionType.SKIPPING, sConcept + " -> " + xConcept, "No further updates required");
				return rmExisting;
			}
		}
		
		//If we already have a UUID then we can update
		if (rm.getId() != null) {
			create = false;
		} else {
			//Check if we have a refset member with this referenced component id
			//Either active or inactive
			if (rmExistingList.size() > 1) {
				report(sConcept, Severity.HIGH, ReportActionType.VALIDATION_ERROR, "Multiple refset entries detected for this 'S' concept");
				return null;
			} else if (rmExistingList.size() == 1) {
				RefsetMemberPojo rmExisting = rmExistingList.get(0);
				rm.setId(rmExisting.getId());
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
		logger.info("Recovered {} new {} concepts", newConcepts.size(), termFilter);
		if (newConcepts.size() > 0) {
			updateRefset(cacheType, refsetId, termFilter, newConcepts);
		}
	}
	
	private void checkNewStructureConcepts() throws TermServerScriptException {
		String eclFilter = "< 123037004 |Body structure (body structure)|";
		Set<Concept> newSConcepts = tsClient.findNewConcepts(task.getBranchPath(), eclFilter, "structure")
				.stream()
				.filter(this::isStructure)
				.collect(Collectors.toSet());
		populateMemberCache(CacheType.ReferencedComponent, newSConcepts, false);
		logger.info("Recovered {} new structure concepts", newSConcepts.size());
		
		//Also search for active, published concepts with a null effective time to identify re-activations.
		//Note will also pick up changes to definition status, so we might see some false positives 
		//reported as already present
		Set<Concept> reactivatedSConcepts = tsClient.findUpdatedConcepts(task.getBranchPath(), true, true, "structure",  eclFilter)
				.stream()
				.filter(this::isStructure)
				.collect(Collectors.toSet());
		populateMemberCache(CacheType.ReferencedComponent, newSConcepts, false);
		logger.info("Recovered {} reactivated structure concepts", reactivatedSConcepts.size());
		
		newSConcepts.addAll(reactivatedSConcepts);
		logger.info("Recovered {} unique new or reactivated structure concepts", newSConcepts.size());
		
		for (Concept c : newSConcepts) {
			final List<Concept> children = tsClient.getChildren(task.getBranchPath(), c.getConceptId());
			if (children.size() == 0) {
				report(c, Severity.MEDIUM, ReportActionType.SKIPPING, "New 'S' concept featured 0 children.");
			} else {
				checkForAvailableChildren(c, "Entire", true, SCTID_SE_REFSETID, children);
				checkForAvailableChildren(c, "Part", false, SCTID_SP_REFSETID, children);
				checkForAvailableChildren(c, "All", true, SCTID_SE_REFSETID, children);
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
			report(c, Severity.MEDIUM, ReportActionType.SKIPPING, "New 'S' concept featured " + validChildren.size() + " '" + (isEntire?"E/A":"P") + "' children.");
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
		int issuesEncountered = 0;
		
		nextNewConcept:
		for (Concept c : concepts) {
			examined.add(c.getId());
			//If it's not a body structure, don't even mention it
			//And I'm not sure why we're seeing these with that eclFilter above!
			if (!c.getFsnTerm().contains("(body structure)")) {
				logger.debug("ECL filter for Body Structure is not doing it's job: {}", c);
				continue nextNewConcept;
			}
			//Ensure the FSN starts with the term filter
			if (!c.getFsnTerm().startsWith(termFilter + " ")) {
				report(c, Severity.HIGH, ReportActionType.VALIDATION_CHECK, "FSN did not start with expected '" + termFilter + "'");
				continue nextNewConcept;
			}
			
			boolean isAllConcept = termFilter.equals("All");
			try {
				attemptXAddition(refsetId, c, isAllConcept);
			} catch (Exception e) {
				issuesEncountered++;
				String msg = ExceptionUtils.getExceptionCause("Unable to process " + c, e);
				report(c, Severity.CRITICAL, ReportActionType.API_ERROR, msg);
				if (issuesEncountered > MAX_ISSUES) {
					throw (e);
				}
			}
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
				.filter(p -> SnomedUtilsBase.deconstructFSN(p.getFsnTerm())[0].toLowerCase().contains("structure"))
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
				report(c, Severity.MEDIUM, ReportActionType.NO_CHANGE, "Skipping attempt to create 2nd RM for 'S'", rm);
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
				logger.debug ("Checking for existing SEP refset entries for {}", c);
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
							//Of course just because we've inactivated one half of the pair, doesn't mean that the other 
							//half might not also have been inactivated!
							ConceptPojo sConceptFull = tsClient.getFullConcept(task.getBranchPath(), rm.getReferencedComponentId());
							ConceptPojo xConceptFull = tsClient.getFullConcept(task.getBranchPath(), rm.getAdditionalFields().getTargetComponentId());
							populateReplacementOrReport(sConceptFull, xConceptFull, rm);
						} else {
							report(c, Severity.LOW, ReportActionType.NO_CHANGE, "RefsetMember previously inactivated", rm);
						}
					}
				}
			} catch (Exception e) {
				String msg = ExceptionUtils.getExceptionCause("Failed to inactivate any refset entries relating to " + c, e);
				logger.error(msg, e);
				report(c, Severity.HIGH, ReportActionType.API_ERROR, msg);
			}
		}
	}
	
	private List<RefsetMemberPojo> getRefsetMembers(CacheType cacheType, String refsetId, String id, Boolean active) {
		return getRefsetMembers(cacheType, refsetId, Collections.singleton(id), active);
	}

	private List<RefsetMemberPojo> getRefsetMembers(CacheType cacheType, String refsetId, Set<String> ids, Boolean active) {
		Map<String, Map<String, Set<RefsetMemberPojo>>> caches = switch (cacheType) {
            case ReferencedComponent -> refsetMemberByReferencedConceptCache;
            case TargetConcept -> refsetMemberByTargetConceptCache;
        };

        final Map<String, Set<RefsetMemberPojo>> refsetCache = caches.get(refsetId);
		//Check if we tried to populate the cache for these ids, even if it returned no members
		Set<String> missedCache = ids.stream()
				.filter(s -> !refsetCache.containsKey(s))
				.collect(Collectors.toSet());
		
		if (!missedCache.isEmpty()) {
			String missedCacheStr = missedCache.stream().collect(Collectors.joining(", "));
			logger.warn("{} refset cache miss for {}", cacheType, missedCacheStr);
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
	
	private void populateReplacementOrReport(ConceptPojo sConceptFull, ConceptPojo xConceptFull, RefsetMemberPojo rm) throws TermServerScriptException {
		RefsetMemberPojo proposed = rm.clone();
		String sReplacementId = getReplacement(sConceptFull);
		String xReplacementId = getReplacement(xConceptFull);
		
		//If we don't have a full set of replacements, don't worry, we tried.
		if (sReplacementId == null || xReplacementId == null) {
			return;
		}
		
		//If the S concept is unchanged, we can keep the same id
		if (sReplacementId.equals(sConceptFull.getConceptId())) {
			proposed.setId(rm.getId());
		}
		
		//We'll only store against the S concept to ensure we don't end up with 
		//multiple replacents for the same S
		historicalReplacementMap.put(sReplacementId, proposed);
		proposed.setReferencedComponentId(sReplacementId);
		proposed.getAdditionalFields().setTargetComponentId(xReplacementId);
		proposed.setActive(true);
	}

	private String getReplacement(ConceptPojo fullConcept) throws TermServerScriptException {
		//If this concept is still active, we don't need to replace it.
		if (fullConcept.isActive()) {
			return fullConcept.getConceptId();
		}
		
		if (fullConcept.getAssociationTargets() == null) {
			report(fullConcept, Severity.HIGH, ReportActionType.VALIDATION_ERROR, "No associations available");
			return null;
		}
		
		for (HistoricalAssociation associationType : associationTypes) {
			Set<String> targets = fullConcept.getAssociationTargets().get(associationType);
			if (targets != null) {
				if (targets.size() == 1) {
					return targets.iterator().next();
				} else if (targets.size() > 1) {
					String msg = "Concept indicated multiple historical associations";
					report(fullConcept, Severity.HIGH, ReportActionType.VALIDATION_ERROR, msg);
					return null;
				}
			}
		}
		return null;
	}

	private void checkAllConcepts(int startingPercentage, CacheType cacheType, String refsetId, String termFilter) throws TermServerScriptException {
		logger.info("Checking all legacy '{}' concepts for potential inclusion", termFilter);
		//Loop through all Body Structures containing 'termFilter' text
		//Get published concepts because new ones will already have been examined
		ConceptPage page = tsClient.fetchConceptPageBlocking(task.getBranchPath(), true, BODY_STRUCTURE_ECL, termFilter, null);
		long totalExpected = page.getTotal();
		long totalReceived = page.getItems().size();

		//Even when we have as many items as expected we still want to run through this loop one last time
		while (totalReceived <= totalExpected) {
			//The term filter isn't "starts with", so we can filter those out.
			//Also filter any concept we've already examined
			Map<String, Concept> conceptMap = page.getItems().stream()
					.filter(c -> c.getFsnTerm().startsWith(termFilter))
					.filter(c -> !examined.contains(c.getConceptId()))
					.collect(Collectors.toMap(Concept::getConceptId, c -> c));
			
			//Do we already have refset members for these concepts?
			//List<RefsetMemberPojo> existing = tsClient.findRefsetMemberByTargetComponentIds(task.getBranchPath(), refsetId, conceptMap.keySet(), true);
			List<RefsetMemberPojo> existing =  getRefsetMembers(CacheType.TargetConcept, refsetId, conceptMap.keySet(), true);
			logger.info("{} / {} '{}' already have refset entries", existing.size(), conceptMap.size(), termFilter);
			existing.forEach(rm -> conceptMap.remove(rm.getAdditionalFields().getTargetComponentId()));
			
			//Attempt to add these 
			updateRefset(cacheType, refsetId, termFilter, conceptMap.values());
			
			//How many have we examined?  Can be up to 30% above starting percentage
			percentageComplete(startingPercentage + (int)((totalReceived / (double)totalExpected) * 30d));
			
			if (totalReceived == totalExpected) {
				break;
			}
			
			//Now get our next page
			page = tsClient.fetchConceptPageBlocking(task.getBranchPath(), true, BODY_STRUCTURE_ECL, termFilter, page.getSearchAfter());
			totalReceived += page.getItems().size();
		}
	}

	private boolean isEntire(IConcept c) {
		return c.getFsnTerm().startsWith("Entire ") || c.getFsnTerm().startsWith("All ");
	}

	private boolean isPart(IConcept c) {
		return c.getFsnTerm().startsWith("Part ");
	}

	private boolean isStructure(IConcept c) {
		//Don't look at the semantic tag
		String term = c.getFsnTerm().replace("(body structure)", "");
		return term.startsWith("Structure ") || 
				(term.contains("structure") && !isEntire(c) && !isPart(c));
	}

	private boolean isBodyStructure(IConcept c) {
		String term = c.getFsnTerm();
		return term.contains("(body structure)");
	}

	private void populateMemberCache(CacheType cacheType, Collection<Concept> concepts, boolean forceRefresh) {
		populateMemberCache(cacheType, concepts.stream().map(Concept::getId).collect(Collectors.toSet()), forceRefresh);
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
			logger.warn("{} forced refresh of {} requires investigation", cacheType, sctIdsStr);
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
		List<RefsetMemberPojo> members = switch (cacheType) {
            case ReferencedComponent ->
                    tsClient.findRefsetMemberByReferencedComponentIds(task.getBranchPath(), null, sctIds, null);
            case TargetConcept ->
                    tsClient.findRefsetMemberByTargetComponentIds(task.getBranchPath(), null, sctIds, null);
        };

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
		logger.debug("Populated caches with {} refset members",  members.size());
	}

	private void prePopulateMemberCaches(Map<String, Map<String, Set<RefsetMemberPojo>>> caches, Set<String> sctIds) {
		for (String refsetId : refsetsOfInterest) {
            Map<String, Set<RefsetMemberPojo>> refsetCache = caches.computeIfAbsent(refsetId, k -> new HashMap<>());
            for (String key : sctIds) {
                Set<RefsetMemberPojo> members = refsetCache.computeIfAbsent(key, k -> new HashSet<>());
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
			logger.warn("Cache miss for {}", sctId);
			populateConceptCache(sctId);
		}
		return miniConceptCache.get(sctId);
	}

	private void populateConceptCache(String... sctIds) {
		//List needs to be modifiable, so create afresh
		populateConceptCache(new ArrayList<>(Arrays.asList(sctIds)));
	}

	private void populateConceptCache(List<String> sctIds) {
		//Remove any concepts we already have cached
		sctIds.removeAll(miniConceptCache.keySet());
		for (Concept c : tsClient.getConcepts(task.getBranchPath(), sctIds)) {
			miniConceptCache.put(c.getConceptId(), c);
		}
	}

}
