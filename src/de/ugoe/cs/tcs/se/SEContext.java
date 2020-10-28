package de.ugoe.cs.tcs.se;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;

import de.ugoe.cs.tcs.se.bugs.Bug;
import de.ugoe.cs.tcs.se.bugs.BugCreator;
import de.ugoe.cs.tcs.se.bugs.CriticalBug;
import de.ugoe.cs.tcs.se.bugs.MajorBug;
import de.ugoe.cs.tcs.se.bugs.MinorBug;
import de.ugoe.cs.tcs.se.bugs.NormalBug;
import de.ugoe.cs.tcs.se.common.DebugAgent;
import de.ugoe.cs.tcs.se.common.CCGraphExport;
import de.ugoe.cs.tcs.se.common.Util;
import de.ugoe.cs.tcs.se.developer.CommitProbabilities;
import de.ugoe.cs.tcs.se.developer.CoreDeveloper;
import de.ugoe.cs.tcs.se.developer.KeyDeveloper;
import de.ugoe.cs.tcs.se.developer.MajorDeveloper;
import de.ugoe.cs.tcs.se.developer.MinorDeveloper;
import de.ugoe.cs.tcs.se.developer.PeripheralDeveloper;
import de.ugoe.cs.tcs.se.developer.SEDeveloper;
import de.ugoe.cs.tcs.se.graph.SECategory;
import de.ugoe.cs.tcs.se.graph.SEFile;
import de.ugoe.cs.tcs.se.graph.init.ImportCCGraph;
import de.ugoe.cs.tcs.se.graph.init.ImportDevelopers;
import de.ugoe.cs.tcs.se.simparam.Common;
import de.ugoe.cs.tcs.se.simparam.CoreData;
import repast.simphony.context.Context;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;

public class SEContext implements ContextBuilder<Object> {
	private static Context<Object> CONTEXT;
	private static int bugsCreated = 0;
	private static int bugsFixed = 0;
	private static List<String> NOC_ENTRIES = Lists.newArrayList();
	
	// Refactoring count
	public static int APPLIED_EM = 0;
	public static int APPLIED_IM = 0;
	public static int APPLIED_MM = 0;
	
		
	public static final List<SECategory> CATEGORIES = Lists.newArrayList();
	
	// average change probabilities
	public static final double P_AVERAGE_UPDATE = 0.2688983708192090;
	public static final double P_AVERAGE_DELETE = 0.8872781271239790;
	public static final double P_AVERAGE_ADD = 0.7187768217385840;
	
	// for label computation (used for crf)
	public static final double BUG_FACTOR_NORMAL = 0.9;
	public static final double BUG_FACTOR_CRITICAL = 0.825;
	public static final double BUG_FACTOR_MAJOR = 0.9;
	public static final double BUG_FACTOR_MINOR = 0.98;
		
	// network names
	public static final String changeCouplingName = "Change Coupling";
	public static final String developerFileName = "Developer connected to file";
	public static final String bugFileName = "Bugs assigned to file";
	public static final String membershipMethodName = "membership_method";
	public static final String methodCallName = "method_call";
	
	
	// whether bugs are assigned to high degree modules or not
	public static final boolean ASSIGN_BUGS_TO_HIGH_DEGREE_MODULES = true;
	public static final double P_ASSIGN_BUGS_TO_HIGH_DEGREE_MODULES = 0.85;
	public static final int NUMBER_OF_HIGH_DEGREE_MODULES_TO_SELECT_FROM = 35;	
	
	// networks
	public static Network<Object> changeCoupling;
	public static Network<Object> developerFile;	
	public static Network<Object> bugFile;
	public static Network<Object> membershipMethod;
	public static Network<Object> methodCall;
	
	// Software graph average probabilities (used for refactoring simulation)
	public static final double P_NEW_CLASS_NOI = 1.0 / (3.256 + 1);
	public static final double P_NEW_CLASS_NII = 1.0 / (1.534 + 1);
	public static final double P_UPDATE_CLASS_NOI = 1.0 / (2.283 + 1);	
	public static final double P_UPDATE_CLASS_NII = 1.0 / (1.097 + 1);
	public static final double P_METHOD_A = 1.0 / (4.872 + 1);
	public static final double P_METHOD_U = 1.0 / (3.58 + 1);
	public static final double P_METHOD_D = 1.0 / (0.898 + 1);
	public static final double P_A_METHOD_LOC = 1.0 / (10.728 + 1);
	public static final double P_A_METHOD_MCCC = 1.0 / (2.118 + 1);
	public static final double P_A_METHOD_NII = 1.0 / (0.839 + 1);
	public static final double P_A_METHOD_NOI = 1.0 / (0.913 + 1);
	public static final double P_U_METHOD_LOC = 1.0 / (0.118 + 1);
	public static final double P_U_METHOD_MCCC = 1.0 / (0.003 + 1);
	public static final double P_U_METHOD_NII = 1.0 / (0.09 + 1);
	public static final double P_U_METHOD_NOI = 1.0 / (0.154 + 1);
	
	// Refactoring probabilities (Extract Method EM)
	public static final double P_EM_METHOD_DELTA_LOC = 1.0 / (8.555 + 1);
	public static final double P_EM_METHOD_DELTA_MCCC = 1.0 / (1.263 + 1);
	public static final double P_EM_METHOD_NEW_LOC = 1.0 / (12.112 + 1);
	public static final double P_EM_METHOD_NEW_MCCC = 1.0 / (2.402 + 1);
	public static final double P_EM_METHOD_NEW_NOI = 1.0 / (1.597 + 1);	

	// Refactoring probabilities (Inline Method IM)
	public static final double P_IM_METHOD_DELTA_CALLER_LOC = 1.0 / (8.889 + 1);
	public static final double P_IM_METHOD_DELTA_CALLER_MCCC = 1.0 / (1.45 + 1);
	public static final double P_IM_METHOD_DELTA_CALLER_NOI = 1.0 / (0.155 + 1);		

	// Refactoring probabilities (Move Method MM)
	
	
	public static final List<Integer> PROJECT_GROWTH = Lists.newArrayList();
	
	
	@Override
	public Context<Object> build(Context<Object> context) {
		context.setId("SimSE");
		
		// init projections
		NetworkBuilder<Object> netBuilder = new NetworkBuilder<Object>(changeCouplingName, context, false);
		changeCoupling = netBuilder.buildNetwork();
		
		netBuilder = new NetworkBuilder<Object>(developerFileName, context, false);
		developerFile = netBuilder.buildNetwork();
		
		netBuilder = new NetworkBuilder<Object>(bugFileName, context, false);
		bugFile = netBuilder.buildNetwork();

		netBuilder = new NetworkBuilder<Object>(membershipMethodName, context, false);
		membershipMethod = netBuilder.buildNetwork();
		
		netBuilder = new NetworkBuilder<Object>(methodCallName, context, true);
		methodCall = netBuilder.buildNetwork();		
		
		// read parameters
		Parameters params = RunEnvironment.getInstance().getParameters();
		CoreData cd = Common.getInstance().getCoreData();
		
		// init
		initCategories(cd);
		CommitProbabilities cp = initCommitProbabilities(cd, params);
		
		// import developer
		ImportDevelopers.create(context, cd, cp, params);
		
		// init graph according to import dot file
		if (params.getInteger("startYear") > 0) {
			StringBuilder fileName = new StringBuilder();
			fileName.append("input/cc_");
			fileName.append(params.getString("projectName"));
			fileName.append("_");
			fileName.append(String.format("%02d", params.getInteger("startYear")));
			fileName.append(".dot");
			ImportCCGraph ig = new ImportCCGraph(fileName.toString(), context);
			ig.generateSimGraph();
		}
		
		// TODO: Consider if creation of developers instead of import them is required anymore
		
		// create only one test developer
//		double coreCpr = (cd.getCoreDeveloperCommits() / cd.getCoreDeveloper() * 1.0) / cd.getRoundsToSimulate();
//		SEDeveloper d = new CoreDeveloper(coreCpr, cp);
//		context.add(d);
		
		//Debug agent
		context.add(DebugAgent.getInstance());
		
		int simRounds =  (int) (cd.getRoundsToSimulate() - RunEnvironment.getInstance().getParameters().getInteger("startYear") * 365);
		RunEnvironment.getInstance().endAt(simRounds);
		
		BugCreator bugCreator = new BugCreator(context, cd);
		RunEnvironment.getInstance().getCurrentSchedule().schedule(bugCreator);
		
		CCGraphExport export = new CCGraphExport();
		ScheduleParameters sRepeat = ScheduleParameters.createRepeating(computeFirtYearTicks(), 365);		
		RunEnvironment.getInstance().getCurrentSchedule().schedule(sRepeat, export, "writeChangeCouplingGraph");
		RunEnvironment.getInstance().getCurrentSchedule().schedule(sRepeat, export, "writeChangeCouplingGraphDOT");
		RunEnvironment.getInstance().getCurrentSchedule().schedule(sRepeat, export, "writeChangeCouplingGraphCGR");
		RunEnvironment.getInstance().getCurrentSchedule().schedule(sRepeat, this, "writeNoCEntry");
		
		ScheduleParameters  sLastRound = ScheduleParameters.createOneTime(simRounds);
		RunEnvironment.getInstance().getCurrentSchedule().schedule(sLastRound, this, "exportNoC");
		RunEnvironment.getInstance().getCurrentSchedule().schedule(sLastRound, this, "writeDefaultCCFiles");
		RunEnvironment.getInstance().getCurrentSchedule().schedule(sLastRound, this, "printStatics", params);		
		
		CONTEXT = context;
		return context;
	}
	
	private long computeFirtYearTicks() {
		CoreData cd = Common.getInstance().getCoreData();
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, Util.getYear(cd.getFirstCommitDate()));
		cal.set(Calendar.MONTH, Calendar.DECEMBER);
		cal.set(Calendar.DAY_OF_MONTH, 31);
		Date dateRepresentation = cal.getTime();
		return Util.computeDaysBetweenDates(cd.getFirstCommitDate(), dateRepresentation);		
	}
	
	private void initCategories(CoreData cd) {
		CATEGORIES.clear();
		for (var p : cd.getExportPackages()) {
			CATEGORIES.add(new SECategory(p.getName(), p.getPercent() / 100.0));
		}
		Collections.sort(CATEGORIES);
	}
	
	private CommitProbabilities initCommitProbabilities(CoreData cd, Parameters params) {
		CommitProbabilities cp = new CommitProbabilities();
		
		if (params.getBoolean("averageChange")) {
			cp.setpAdd(P_AVERAGE_ADD);
			cp.setpAddInit(P_AVERAGE_ADD);
			cp.setpAddDev(P_AVERAGE_ADD);
			cp.setpDelete(P_AVERAGE_DELETE);
			cp.setpDeleteInit(P_AVERAGE_DELETE);
			cp.setpDeleteDev(P_AVERAGE_DELETE);
			cp.setpUpdate(P_AVERAGE_UPDATE);
			cp.setpUpdateInit(P_AVERAGE_UPDATE);
			cp.setpUpdateDev(P_AVERAGE_UPDATE);			
		} else {
			cp.setpAdd(cd.getpAverageCommitAdd());
			cp.setpAddInit(cd.getpInitialCommitAdd());
			cp.setpAddDev(cd.getpDevelopmentCommitAdd());
			cp.setpDelete(cd.getpAverageCommitDelete());
			cp.setpDeleteInit(cd.getpInitialCommitDelete());
			cp.setpDeleteDev(cd.getpDevelopmentCommitDelete());
			cp.setpUpdate(cd.getpAverageCommitUpdate());
			cp.setpUpdateInit(cd.getpInitialCommitUpdate());
			cp.setpUpdateDev(cd.getpDevelopmentCommitUpdate());
		}
		
		return cp;
	}
	
	public static Context<Object> baseContext() {
		return CONTEXT;
	}
	
	public static void fileChangedTogetherWith(SEFile f1, SEFile f2) {
		if (f1 != null && f2 != null) {
			RepastEdge<Object> foundEdge = changeCoupling.getEdge(f1, f2);
			if (foundEdge != null) {
				foundEdge.setWeight(foundEdge.getWeight() + 1.0);
			} else {
				RepastEdge<Object> edge = changeCoupling.addEdge(f1, f2);
				edge.setWeight(1.0);
			}
		}
	}	
	
	public static int simTickCount() {
		return (int) RunEnvironment.getInstance().getCurrentSchedule().getTickCount() + RunEnvironment.getInstance().getParameters().getInteger("startYear") * 365;
	}
	
	public static void bugFixed() {
		bugsFixed++;
	}
	
	public static void bugCreated() {
		bugsCreated++;
	}
	
	public static int numberOfBugsCreated() {
		return bugsCreated;
	}

	public static int numberOfBugsFixed() {
		return bugsFixed;
	}
	
	/**
	 * If the simulation starts at a given time, standard graphs with only one node
	 * are generated here for the non-simulated years. They are used by the tools
	 * for further evaluation 
	 */
	public void writeDefaultCCFiles() {
		CCGraphExport export = new CCGraphExport();
		for (int i = 0; i < RunEnvironment.getInstance().getParameters().getInteger("startYear"); i++) {
			export.writeChangeCouplingGraph(i+1);
			export.writeChangeCouplingGraphDOT(i+1);
			export.writeChangeCouplingGraphCGR(i+1);
		}
	}
	
	public void writeNoCEntry() {
		if (NOC_ENTRIES.size() == 0) {
			NOC_ENTRIES.add("no,core,peripheral,key,major,minor"); //number, roles, types
			// add 0 entries 
			for (int i = 0; i < RunEnvironment.getInstance().getParameters().getInteger("startYear"); i++) {
				StringBuilder entry = new StringBuilder();
				entry.append(NOC_ENTRIES.size());
				entry.append(",");
				entry.append(0);
				entry.append(",");
				entry.append(0);
				entry.append(",");
				entry.append(0);
				entry.append(",");
				entry.append(0);
				entry.append(",");
				entry.append(0);
				NOC_ENTRIES.add(entry.toString());
			}			
		}
		StringBuilder entry = new StringBuilder();
		entry.append(NOC_ENTRIES.size());
		entry.append(",");
		entry.append(Util.numberOfCommits(CoreDeveloper.class));
		entry.append(",");
		entry.append(Util.numberOfCommits(PeripheralDeveloper.class));
		entry.append(",");
		entry.append(Util.numberOfCommits(KeyDeveloper.class));
		entry.append(",");
		entry.append(Util.numberOfCommits(MajorDeveloper.class));
		entry.append(",");
		entry.append(Util.numberOfCommits(MinorDeveloper.class));
		NOC_ENTRIES.add(entry.toString());
	}
	
	public void exportNoC() {
		try {
			File f = new File("output/change_coupling");
			if (!f.exists()) {
				f.mkdirs();
			}
			PrintWriter writer = new PrintWriter("output/change_coupling/sim_noc_" +  RunEnvironment.getInstance().getParameters().getString("projectName") + (RunEnvironment.getInstance().getParameters().getBoolean("refactoring") ? "_ref" : "") + ".txt", "UTF-8");
			for (String entry : NOC_ENTRIES) {
				writer.println(entry);
			}
			writer.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}	
		NOC_ENTRIES.clear();
	}
	
	public void printStatics(Parameters params) {
		System.out.println('\n' + "++++++++++++++ Statistics ++++++++++++++");
		assert(numberOfBugsCreated() == Bug.numberOfCreatedBugs());
		assert(numberOfBugsFixed() == Bug.numberOfClosedBugs());
		
		String statistics = "";
		
		statistics += Bug.printBugCreationStatistics();
		
		statistics += '\n';
		
		statistics += Bug.printBugFixStatistics();
		
		statistics += '\n';

		statistics += "Number of open bugs:    " + Bug.numberOfOpenBugs() + '\n';
		
		statistics += '\n';		
		
		statistics += Bug.printBugsFixedByDeveloperType();
		
		statistics += '\n';
		
		statistics += NormalBug.printBugsFixedByDeveloperType();
		
		statistics += '\n';		

		statistics += CriticalBug.printBugsFixedByDeveloperType();
		
		statistics += '\n';

		statistics += MajorBug.printBugsFixedByDeveloperType();
		
		statistics += '\n';

		statistics += MinorBug.printBugsFixedByDeveloperType();
		
		statistics += '\n';

		statistics += "Number of commits:" + '\n';
		statistics += '\t' + "Core:" + '\t' + Util.numberOfCommits(CoreDeveloper.class) + '\n';
		statistics += '\t' + "Peripheral:" + '\t' + Util.numberOfCommits(PeripheralDeveloper.class) + '\n';
		statistics += '\t' + "Key:"+ '\t' + Util.numberOfCommits(KeyDeveloper.class) + '\n';
		statistics += '\t' + "Major:"+ '\t' + Util.numberOfCommits(MajorDeveloper.class) + '\n';
		statistics += '\t' + "Minor:"+ '\t' + Util.numberOfCommits(MinorDeveloper.class);		
		
		System.out.println(statistics);
		
		try {
			File f = new File("output/change_coupling");
			if (!f.exists()) {
				f.mkdirs();
			}
			PrintWriter writer = new PrintWriter("output/change_coupling/statistics_" +  RunEnvironment.getInstance().getParameters().getString("projectName") + (RunEnvironment.getInstance().getParameters().getBoolean("refactoring") ? "_ref" : "") + ".log", "UTF-8");			
			writer.println(statistics);
			writer.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
		try {
			File f = new File("output/change_coupling");
			if (!f.exists()) {
				f.mkdirs();
			}
			PrintWriter writer = new PrintWriter("output/change_coupling/sim_growth_" +  RunEnvironment.getInstance().getParameters().getString("projectName") + (RunEnvironment.getInstance().getParameters().getBoolean("refactoring") ? "_ref" : "") + ".txt", "UTF-8");
			for (int i : PROJECT_GROWTH) {
				writer.println(Integer.toString(i));
			}
			writer.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}		
		
		printCategories();
		
		if (params.getBoolean("refactoring")) {
			System.out.println('\n' + "REFACTORINGS:");
			System.out.println("Applied Extract Methods: " + APPLIED_EM);
			System.out.println("Applied Inline Methods: "  + APPLIED_IM);
			System.out.println("Applied Move Methods: "    + APPLIED_MM);
		}
		
		PROJECT_GROWTH.clear();
		Bug.clearAllStatistics();
		bugsFixed = 0;
		bugsCreated = 0;
		SEDeveloper.COMMIT_COUNT = 0;
		APPLIED_EM = 0;
		APPLIED_MM = 0;
		APPLIED_IM = 0;
	}
	
	public static void printCategories() {
		for (var c : CATEGORIES) {
			System.out.println('\n' + "Categories");
			System.out.println(c);
		}
	}
	
}
