package de.ugoe.cs.tcs.se.bugs;

import de.ugoe.cs.tcs.se.developer.SEDeveloper;
import repast.simphony.engine.environment.RunEnvironment;

public class MajorBugClosed extends MajorBug {
	private static int counter = 0;
	private static int numberOfDeletionFixes = 0;
	
	public MajorBugClosed(double creationTime, SEDeveloper developer) {
        this.closeTime = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
        this.creationTime = creationTime;
        counter++;
        
        if(developer==null) {
        	numberOfDeletionFixes++;
        } else {
        	Bug.statisticsForBugClosing(fixedByDeveloperType, developer);
        }
        	
    }

	@Override
	public Bug closeBug(SEDeveloper developer) {
		return this;
	}
	
	public static int getCount() {
		return counter;
	}	
	
	public static int getNumberOfDeletionFixes() {
		return numberOfDeletionFixes;
	} 
	
	public static void clearClosedBugCount() {
		counter=0;
		numberOfDeletionFixes=0;
	}
}
