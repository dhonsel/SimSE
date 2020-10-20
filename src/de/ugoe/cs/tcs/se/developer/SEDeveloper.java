package de.ugoe.cs.tcs.se.developer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import de.ugoe.cs.tcs.se.SEContext;
import de.ugoe.cs.tcs.se.bugs.Bug;
import de.ugoe.cs.tcs.se.bugs.BugCollection;
import de.ugoe.cs.tcs.se.common.Util;
import de.ugoe.cs.tcs.se.graph.SECategory;
import de.ugoe.cs.tcs.se.graph.SEClass;
import de.ugoe.cs.tcs.se.graph.SEFile;
import de.ugoe.cs.tcs.se.graph.SEMethod;
import de.ugoe.cs.tcs.se.refactorings.ExtractMethod;
import de.ugoe.cs.tcs.se.refactorings.InlineMethod;
import de.ugoe.cs.tcs.se.refactorings.Misc;
import de.ugoe.cs.tcs.se.refactorings.MoveMethod;
import de.ugoe.cs.tcs.se.simparam.BugPriority;
import de.ugoe.cs.tcs.se.simparam.Common;
import de.ugoe.cs.tcs.se.simparam.CoreData;
import repast.simphony.context.Context;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.util.ContextUtils;

public abstract class SEDeveloper {
	public static int COMMIT_COUNT = 0;

	protected final UUID objectID;
	protected final String name;
	protected List<SEFile> changedFiles = Lists.newArrayList();
	protected final Parameters params;
	private boolean active;
	private boolean maintainer;
	private final double commitsPerRound;
	protected CommitProbabilities cp;
	protected final double fixProbability;
	protected int commitCount;
	protected int bugfixes;
	
	// fields for testing purposes
	protected int numOfUpdatedFiles;
	protected int numOfAddedFiles;
	protected int numOfDeletedFiles;
	
	protected int sumOfUpdatedFiles;
	protected int sumOfAddedFiles;
	protected int sumOfDeletedFiles;
		
	public SEDeveloper(double commitsPerRound, CommitProbabilities cp, Parameters params) {
		this.commitsPerRound = commitsPerRound;
		this.active = true;
		this.commitCount = 0;
		this.cp = cp;
		this.params = params;
		this.objectID = UUID.randomUUID();
		this.name = "";
		this.fixProbability = computeFixProbability();
	}
	
	public SEDeveloper(double commitsPerRound, CommitProbabilities cp, Parameters params, UUID objectID, String name, boolean maintainer) {
		this.commitsPerRound = commitsPerRound;
		this.active = true;
		this.commitCount = 0;
		this.cp = cp;
		this.params = params;
		this.objectID = objectID;
		this.name = name;
		this.maintainer = maintainer;
		this.fixProbability = computeFixProbability();
	}
	
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public boolean isMaintainer() {
		return maintainer;
	}
	public void setMaintainer(boolean maintainer) {
		this.maintainer = maintainer;
	}
	public double getCommitsPerMonth() {
		return commitsPerRound;
	}
	public int getCommitCount() {
		return commitCount;
	}

	public abstract void doSomeWork();
	
	public abstract double computeFixProbability();
	
	public abstract void bugfix();
	

	public int getSumOfUpdatedFiles() {
		return sumOfUpdatedFiles;
	}

	public int getSumOfAddedFiles() {
		return sumOfAddedFiles;
	}

	public int getSumOfDeletedFiles() {
		return sumOfDeletedFiles;
	}
	
	public int getNumOfUpdatedFiles() {
		return numOfUpdatedFiles;
	}

	public int getNumOfAddedFiles() {
		return numOfAddedFiles;
	}

	public int getNumOfDeletedFiles() {
		return numOfDeletedFiles;
	}

	@SuppressWarnings("unchecked")
	protected void createFiles(int num, SECategory category) {
		Context<Object> context = ContextUtils.getContext(this);
		SEFile file;
		
		numOfAddedFiles = num;
		sumOfAddedFiles += numOfAddedFiles;		
		
		for (int i = 0; i < num; i++) {
			file = new SEFile(this, category);
			file.setLastAuthor(this);
			context.add(file);
			changedFiles.add(file);
			RepastEdge<Object> edge = SEContext.developerFile.addEdge(this, file);
			edge.setWeight(1.0);
			category.addFile(file);
			if (params.getBoolean("refactoring")) {
				createClass(file);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void deleteFiles(int num, SECategory category) {
		Context<Object> context = ContextUtils.getContext(this);
		
		numOfDeletedFiles = 0;
		int deletedFiles = 0;		
		
		if (context.getObjects(SEFile.class).size() <= num)
			return;
		
		ArrayList<Bug> assignedBugs = Lists.newArrayList();
		
		for (int i = 0; i < num; ++i) {
			SEFile fileToDelete = Util.getFileToChange(category, this);		

			boolean fileHasBugs = fileToDelete.numOfBugs() > 0;
			boolean delete = fileToDelete != null  && fileToDelete.getCouplingDegree() < 6;
			
			if (fileHasBugs) {
				delete = delete && RandomHelper.nextDoubleFromTo(0.0, 1.0) < 0.6 ? false : true;
			}
				
			if (delete) {
				SECategory c = fileToDelete.getCategory();
				c.getFiles().remove(fileToDelete);				
				
				assignedBugs.clear();
				if (fileToDelete.numOfBugs() > 0) {
					for (RepastEdge<Object> e : SEContext.bugFile.getInEdges(fileToDelete)) {
						assignedBugs.add((Bug) e.getSource());
					}
				}					
				
				// delete corresponding class and methods
				if (params.getBoolean("refactoring")) {
					deleteClass(fileToDelete);
				}
				
				context.remove(fileToDelete); 	// edges in corresponding networks
												// will be removed automatically
								
				numOfDeletedFiles++;
				
				for (Bug b : assignedBugs) {
					if (SEContext.bugFile.getOutDegree(b) <= 1) {
						// this bug gets only fixed because the file will be deleted
						// we assume no intention to fix the bug
						b.closeBug(null);
						SEContext.bugFixed();
					}
				}				
			}
		}
		
		numOfDeletedFiles = deletedFiles;
		sumOfDeletedFiles += numOfDeletedFiles;
	}
	
	protected void updateFiles(int num, SECategory category) {
		// TODO: create bugs with certain probability according to mining data
		// -> maybe later - currently bug reports created by the system are
		// simulated (generated by users or developers)

		List<SEFile> updatedFiles = Lists.newArrayList();
		numOfUpdatedFiles = 0;
				
		for (int i = 0; i < num; i++) {
			SEFile file = Util.getFileToChange(category, this);
			
			if (!updatedFiles.contains(file)) {
				updateFile(file);
				updatedFiles.add(file);
				numOfUpdatedFiles++;
				if (params.getBoolean("refactoring")) {
					updateCLass(file);
				}
				
			}
		}
		
		sumOfUpdatedFiles += numOfUpdatedFiles;
		updatedFiles.clear();
	}
	
	private void updateFile(SEFile file) {
		if (file == null)
			return;
		
		RepastEdge<Object> edge = SEContext.developerFile.getEdge(this, file);
		
		if (edge != null) {	// this developer has modified (could just be the creation) the selected module at least once
			file.increaseTouches();
			changedFiles.add(file);
			int weight = (int) edge.getWeight();
			edge.setWeight(++weight);
			file.checkOwnership();
			file.setLastAuthor(this);
		} else { // create a new edge to a module this developer never touched before			
			edge = SEContext.developerFile.addEdge(this, file);
			file.increaseTouches();
			changedFiles.add(file);
			edge.setWeight(1.0);
			file.checkOwnership();		
			file.setLastAuthor(this);
		}
	}	
	
	
	protected void createCoupling() {
		 // great commits are not taken into account
		 if (changedFiles.size() > 25)
		 	return;
		
		for (int i = 0; i < changedFiles.size(); i++) {
			for (int j = i+1; j < changedFiles.size(); j++) {
				SEContext.fileChangedTogetherWith(changedFiles.get(i), changedFiles.get(j));
			}
		}
	}	
	
	protected int initBoost (int filesToAdd) {
		return (int) (filesToAdd * params.getDouble("initBoostFactor"));
	}
	
	protected int boost (int filesToAdd) {
		return (int) (filesToAdd * params.getDouble("boostFactor"));
	}
	
	protected int clipGrowth(int filesToAdd) {
		double percent = Util.numberOfFiles() * 1.0 / Common.getInstance().getCoreData().getMaxFiles() * 100.0;
		int add = filesToAdd;
		
		if (percent > 110.0) {
			add = 0;
		} else if (percent > 95.0) {
			add = (int) (add * 0.5);
		} else if (percent > 85.0) {
			add = (int) (add * 0.75);
		} 
		
		return add;
	}

	/**
	 * Simple bugfix strategy. If you do not want to implement a own strategy
	 * for a specialized developer, call this method.
	 */
	protected void simpleBugfix() {
		if (!isActive()) {
			return;
		}
		
		BugCollection bugs = new BugCollection(changedFiles);
		double pFixType = RandomHelper.nextDoubleFromTo(0.0, 1.0);

		if (RandomHelper.nextDoubleFromTo(0.0, 1.0) > fixProbability)
			return;

		if (bugs.getNormalBugs().size() > 0 ) {
			bugs.getNormalBugs().get(0).closeBug(this);
			bugfixes++;
			SEContext.bugFixed();
		} else if (bugs.getMinorBugs().size() > 0 ) {
			bugs.getMinorBugs().get(0).closeBug(this);
			bugfixes++;
			SEContext.bugFixed();
		} else if (bugs.getMajorBugs().size() > 0 && pFixType >= 0.82) {
			bugs.getMajorBugs().get(0).closeBug(this);
			bugfixes++;
			SEContext.bugFixed();
		} else if (bugs.getCriticalBugs().size() > 0) {
			bugs.getCriticalBugs().get(0).closeBug(this);
			bugfixes++;
			SEContext.bugFixed();
		}
	}
	
	// This fix method is independent from the selected files for the commit.
	// It selects few own files according to the assigned file to the bug.
	protected void seperateBugfix() {
		CoreData cd = Common.getInstance().getCoreData();
		List<SEFile> files = Lists.newArrayList(SEContext.baseContext().getObjects(SEFile.class))
				.stream()
				.map(o -> (SEFile) o)
				.collect(Collectors.toList());
		BugCollection bugs = new BugCollection(files);

		if (RandomHelper.nextDoubleFromTo(0.0, 1.0) > fixProbability)
			return;
		
		int sumFixed = cd.getIssueInformationCompleteFixed().get(BugPriority.CRITICAL)
				+ cd.getIssueInformationCompleteFixed().get(BugPriority.MAJOR)
				+ cd.getIssueInformationCompleteFixed().get(BugPriority.MINOR)
				+ cd.getIssueInformationCompleteFixed().get(BugPriority.NONE);
		
		double pCritical = sumFixed > 0 ? cd.getIssueInformationCompleteFixed().get(BugPriority.CRITICAL) * 1.0 / sumFixed * 1.0 : 0.0;
		double pMajor = sumFixed > 0 ? cd.getIssueInformationCompleteFixed().get(BugPriority.MAJOR) * 1.0 / sumFixed * 1.0 : 0.0;
		double pMinor = sumFixed > 0 ? cd.getIssueInformationCompleteFixed().get(BugPriority.MINOR) * 1.0 / sumFixed * 1.0 : 0.0;
		double pNone = sumFixed > 0 ? cd.getIssueInformationCompleteFixed().get(BugPriority.NONE) * 1.0 / sumFixed * 1.0 : 0.0;
		
		double pType = RandomHelper.nextDoubleFromTo(0.0, 1.0);
		
		if (pType <= pCritical && bugs.getCriticalBugs().size() > 0) {
			bugs.getCriticalBugs().get(0).closeBug(this);
			bugfixes++;
			SEContext.bugFixed();			
		} else if (pType <= pCritical + pMajor && bugs.getMajorBugs().size() > 0) {
			bugs.getMajorBugs().get(0).closeBug(this);
			bugfixes++;
			SEContext.bugFixed();		
		} else if (pType <= pCritical + pMajor + pMinor && bugs.getMinorBugs().size() > 0) {
			bugs.getMinorBugs().get(0).closeBug(this);
			bugfixes++;
			SEContext.bugFixed();			
		} else if (pType <= pCritical + pMajor + pMinor + pNone && bugs.getNormalBugs().size() > 0) {
			bugs.getNormalBugs().get(0).closeBug(this);
			bugfixes++;
			SEContext.bugFixed();			
		}
	}
	
	public UUID getObjectID() {
		return objectID;
	}

	public String getName() {
		return name;
	}


	public int getBugfixes() {
		return bugfixes;
	}
	
	protected void baseWork() {
		if (!isActive()) {
			return;
		}
		boolean commit = false;

		
		SECategory cat = Util.getRandomCategorie();
		cat.increaseSelections();
		
		double r = RandomHelper.nextDoubleFromTo(.0, 1.0);
		if (r <= getCommitsPerMonth()) {
			commitCount++;
			COMMIT_COUNT++;
			commit = true;
			
			// refactoring
			double ref = RandomHelper.nextDoubleFromTo(.0, 1.0);
			boolean refApplied = false;
			if (params.getBoolean("refactoring") && ref <= 0.1) {
				changedFiles.clear();
				ExtractMethod em = new ExtractMethod(SEContext.baseContext(), changedFiles);
				if (em.findMatching()) {
					em.applyRefactoring();
				}
				createCoupling();
				refApplied = true;
			} else if (params.getBoolean("refactoring") && ref <= 0.2) {
				changedFiles.clear();
				InlineMethod im = new InlineMethod(SEContext.baseContext(), changedFiles);
				if (im.findMatching()) {
					im.applyRefactoring();
				}
				createCoupling();
				refApplied = true;
			} else if (params.getBoolean("refactoring") && ref <= 0.3) {
				changedFiles.clear();
				MoveMethod mm = new MoveMethod(SEContext.baseContext(), changedFiles);
				if (mm.findMatching()) {
					mm.applyRefactoring();
				}
				createCoupling();				
				refApplied = true;
			} 
			
			double doCommit = RandomHelper.nextDoubleFromTo(.0, 1.0);
			if (!refApplied || doCommit < 0.5) {
				int u = Util.randomGeometric(cp.getpUpdate());
				int a =  Util.randomGeometric(cp.getpAdd());
				int d = Util.randomGeometric(cp.getpDelete());
				
				if (params.getInteger("initRounds") > 0 && SEContext.simTickCount() > params.getInteger("initRounds") 
						|| params.getInteger("initRounds") == 0 && COMMIT_COUNT > Common.getInstance().getCoreData().getInitialCommits()) {
					if (cp.getpUpdateDev() > 0) {
						u = Util.randomGeometric(cp.getpUpdateDev());
					}
					if (cp.getpAddDev() > 0) {
						a =  Util.randomGeometric(cp.getpAddDev());
					}
					if (cp.getpDeleteDev() > 0) {
						d = Util.randomGeometric(cp.getpDeleteDev());
					}
					a = boost(a);
					a = clipGrowth(a);
					u = clipUpdate(u);
				} else {
					if (cp.getpUpdateInit() > 0) {
						u = Util.randomGeometric(cp.getpUpdateInit());					
					}
					if (cp.getpAddInit() > 0) {
						a =  Util.randomGeometric(cp.getpAddInit());
					}
					if (cp.getpDeleteInit() > 0) {
						d = Util.randomGeometric(cp.getpDeleteInit());
					}
					a = initBoost(a);
				}
				
				// Deletion bonus
				d = (int) (d * params.getDouble("deleteBonus"));
				
				sumOfUpdatedFiles += u;
				sumOfAddedFiles += a;
				sumOfDeletedFiles += d;
				
				changedFiles.clear();
				
				deleteFiles(d, cat);
				updateFiles(u, cat);
				createFiles(a, cat);
							
				createCoupling();
				bugfix();
			}
		}	
		
		if (commit) {
			SEContext.PROJECT_GROWTH.add(SEContext.baseContext().getObjects(SEFile.class).size());
		}
	}

	private int clipUpdate(int u) {
		double percent = Util.numberOfFiles() * 1.0 / Common.getInstance().getCoreData().getMaxFiles() * 100.0;
		int update = u;
		
		if (percent > 110.0) {
			update = (int) (update * 0.1);
		} else if (percent > 95.0) {
			update = (int) (update * 0.4);
		} else if (percent > 85.0) {
			update = (int) (update * 0.6);
		} else {
			update = (int) (update * 0.85);
		}
		
		return update;
	}
	
	protected void createClass(SEFile file) {
		int numOfMethods = Util.randomGeometric(SEContext.P_METHOD_A);
		int classNii = Util.randomGeometric(SEContext.P_NEW_CLASS_NII);
		int classNoi = Util.randomGeometric(SEContext.P_NEW_CLASS_NOI);
		SEClass clazz = new SEClass();
		SEContext.baseContext().add(clazz);
		clazz.setFile(file);
		file.setClazz(clazz);

		addMethods(clazz, numOfMethods, file);
		
		changeClassCoupling(file, classNii, classNoi);
	}
	
	private void addMethods(SEClass clazz, int numOfMethods, SEFile file) {
		List<SEMethod> methods = Lists.newArrayList();
		
		for (int i = 0; i < numOfMethods; i++) {
			int loc = Util.randomGeometric(SEContext.P_A_METHOD_LOC);
			int mccc = Util.randomGeometric(SEContext.P_A_METHOD_MCCC);
			SEMethod m = new SEMethod();
			SEContext.baseContext().add(m);
			m.setLOC(loc);
			m.setMcCC(mccc);
			SEContext.membershipMethod.addEdge(m, clazz);
			methods.add(m);
		}
		
		for (var m : methods) {
			int noi = Util.randomGeometric(SEContext.P_A_METHOD_NOI);
			for (int i = 0; i < noi; i++) {
				SEMethod target = Util.randomMethod(file);
				if (target != null && m != target) {
					SEContext.methodCall.addEdge(m, target);
				}
			}
		}		
	}
	
	protected void updateCLass(SEFile file) {
		if (file == null) {
			return;
		}
		
		int numOfNewMethods = Util.randomGeometric(SEContext.P_METHOD_A);
		int numOfUpdateMethods = Util.randomGeometric(SEContext.P_METHOD_U);
		int numOfDelteMethods = Util.randomGeometric(SEContext.P_METHOD_D);
		int classNiiUpdate = Util.randomGeometric(SEContext.P_UPDATE_CLASS_NII);
		int classNoiUpdate = Util.randomGeometric(SEContext.P_UPDATE_CLASS_NOI);
		
		// add methods
		addMethods(file.getClazz(), numOfNewMethods, file);
		
		// update methods
		for (int i = 0; i < numOfUpdateMethods; i++) {
			SEMethod methodToUpdate = Util.randomMethod(file);
			if (methodToUpdate != null) {
				int loc = Util.randomGeometric(SEContext.P_U_METHOD_LOC);
				int mccc = Util.randomGeometric(SEContext.P_U_METHOD_MCCC);
				int noi = Util.randomGeometric(SEContext.P_U_METHOD_NOI);
				
				methodToUpdate.setLOC(methodToUpdate.getLOC() + loc);
				methodToUpdate.setMcCC(methodToUpdate.getMcCC() + mccc);
				for (int x = 0; x < noi; x++) {
					SEMethod target = Util.randomMethod(file);
					if (target != null && methodToUpdate != target) {
						SEContext.methodCall.addEdge(methodToUpdate, target);
					}
				}
			}
		}
		
		// delete methods
		for (int i = 0; i < numOfDelteMethods; i++) {
			SEMethod methodToDelete = Util.randomMethod(file);
			if (methodToDelete != null) {
				SEContext.baseContext().remove(methodToDelete);
			}
		}
		
		// change coupling
		changeClassCoupling(file, classNiiUpdate, classNoiUpdate);
	}
	
	private void changeClassCoupling(SEFile file, int classNii, int classNoi) {
		for (int i = 0; i < classNii; i++) {
			SEMethod source = Util.randomMethod(file.getCategory(), this);
			SEMethod target = Util.randomMethod(file);
			
			if (source != null && target != null && source != target) {
				SEContext.methodCall.addEdge(source, target);
				var sourceFile = Misc.getClassOfMethod(SEContext.membershipMethod, source).getFile();
				var targetFile = Misc.getClassOfMethod(SEContext.membershipMethod, target).getFile();
				if (!changedFiles.contains(sourceFile)) {
					changedFiles.add(sourceFile);
				}
				if (!changedFiles.contains(targetFile)) {
					changedFiles.add(targetFile);
				}
			}
		}
		
		for (int i = 0; i < classNoi; i++) {
			SEMethod source = Util.randomMethod(file);
			SEMethod target = Util.randomMethod(file.getCategory(), this);
			
			if (source != null && target != null && source != target) {
				SEContext.methodCall.addEdge(source, target);
				var sourceFile = Misc.getClassOfMethod(SEContext.membershipMethod, source).getFile();
				var targetFile = Misc.getClassOfMethod(SEContext.membershipMethod, target).getFile();
				if (!changedFiles.contains(sourceFile)) {
					changedFiles.add(sourceFile);
				}
				if (!changedFiles.contains(targetFile)) {
					changedFiles.add(targetFile);
				}
			}
		}			
	}
	
	protected void deleteClass(SEFile file) {
		// delete methods and edges in corresponding networks
		for (var m : Misc.getMethodsOfClass(SEContext.membershipMethod, file.getClazz())) {
			SEContext.baseContext().remove(m);
		}
		
		// delete corresponding class
		SEContext.baseContext().remove(file.getClazz());
		file.setClazz(null);
	}
	
}
