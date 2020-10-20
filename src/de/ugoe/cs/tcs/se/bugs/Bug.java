package de.ugoe.cs.tcs.se.bugs;

import java.util.List;
import java.util.Map;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.space.graph.RepastEdge;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.ugoe.cs.tcs.se.SEContext;
import de.ugoe.cs.tcs.se.developer.SEDeveloper;
import de.ugoe.cs.tcs.se.graph.SEFile;

public abstract class Bug {	
	private static final Map<Class<SEDeveloper>, IncrementableInt> fixedByDeveloperType = Maps.newHashMap();
	
	protected static class IncrementableInt {
		  private int value = 1;
		  public void increment() { ++value; }
		  public int get()        { return value; }
	}	
	
	protected double creationTime;
	protected double closeTime;

	public double getCreationTime() {
		return creationTime;
	}

	public double getCloseTime() {
		return closeTime;
	}

	public double getLifespan() {
		if (this.closeTime > 0) {
			return this.closeTime - this.creationTime;
		} else {
			double currentTime = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
			return currentTime - this.creationTime;
		}
	}
	
	public abstract Bug closeBug(SEDeveloper developer);
	
	public List<SEFile> getAssignedFiles() {
		List<SEFile> list = Lists.newArrayList();
		for (RepastEdge<Object> e : SEContext.bugFile.getEdges(this)) {
			list.add((SEFile)e.getTarget());
		}
		return list;
	}
	
	public static int numberOfCreatedBugs() {
		return MajorBugOpen.getCount() + MinorBugOpen.getCount() + CriticalBugOpen.getCount() + NormalBugOpen.getCount();
	}
	
	public static int numberOfClosedBugs() {
		return MajorBugClosed.getCount() + MinorBugClosed.getCount() + CriticalBugClosed.getCount() + NormalBugClosed.getCount();
	}
	
	public static int numberOfDeletionFixes() {
		return MajorBugClosed.getNumberOfDeletionFixes() + MinorBugClosed.getNumberOfDeletionFixes() + CriticalBugClosed.getNumberOfDeletionFixes() + NormalBugClosed.getNumberOfDeletionFixes();
	}	
	
	public static int numberOfOpenBugs() {
		return numberOfCreatedBugs() - numberOfClosedBugs();
	}
	
	public static String printBugCreationStatistics() {
		String res = "";
		res += "Number of created bugs: " + numberOfCreatedBugs() + '\n';
		res += '\t' + "Normal:  " + NormalBugOpen.getCount() + '\n';
		res += '\t' + "Critical:  " + CriticalBugOpen.getCount() + '\n';
		res += '\t' + "Major: " + MajorBugOpen.getCount() + '\n';
		res += '\t' + "Minor:  " + MinorBugOpen.getCount() + '\n';
		return res;
	}
	
	public static String printBugFixStatistics() {
		String res = "";
		res += "Number of fixed bugs: " + numberOfClosedBugs() + '\n';
		res += '\t' + "Deletion fixes of all types: " + numberOfDeletionFixes() + '\n';
		res += '\t' + "Normal:  " + NormalBugClosed.getCount() + '\n';
		res += '\t' + "Critical:  " + CriticalBugClosed.getCount() + '\n';
		res += '\t' + "Major: " + MajorBugClosed.getCount() + '\n';
		res += '\t' + "Minor:  " + MinorBugClosed.getCount() + '\n';
		return res;
	}	
	
	protected static void statisticsForBugClosing(Map<Class<SEDeveloper>, IncrementableInt> map, SEDeveloper developer) {
		addEntryForBugClosing(map, developer);
		addEntryForBugClosing(fixedByDeveloperType, developer);
	}
	
	@SuppressWarnings("unchecked")
	private static void addEntryForBugClosing(Map<Class<SEDeveloper>, IncrementableInt> map, SEDeveloper developer) {
    	if (map.containsKey(developer.getClass())) {
    		IncrementableInt value = map.get(developer.getClass());
    		if (value != null) {
    			value.increment();
    		}
    	} else {
    		map.put((Class<SEDeveloper>) developer.getClass(), new IncrementableInt());
    	}		
	}
	
	public static String printBugsFixedByDeveloperType() {
		String res = "";
		res += "Number of all bugs fixed by developer type:" + '\n';
		for (Class<SEDeveloper> c : fixedByDeveloperType.keySet()) {
			res += '\t' + c.getSimpleName() + ": " + fixedByDeveloperType.get(c).get() + '\n';
		}
		return res;
	}	
	
	public static void clearAllStatistics() {
		fixedByDeveloperType.clear();
		NormalBug.clearStatistics();
		MajorBug.clearStatistics();
		MinorBug.clearStatistics();
		CriticalBug.clearStatistics();
		NormalBugOpen.clearOpenBugCount();
		MajorBugOpen.clearOpenBugCount();
		MinorBugOpen.clearOpenBugCount();
		CriticalBugOpen.clearOpenBugCount();	
		NormalBugClosed.clearClosedBugCount();
		MajorBugClosed.clearClosedBugCount();
		MinorBugClosed.clearClosedBugCount();
		CriticalBugClosed.clearClosedBugCount();	
	}	
	
}
