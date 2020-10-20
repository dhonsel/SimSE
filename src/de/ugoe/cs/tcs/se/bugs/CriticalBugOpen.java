package de.ugoe.cs.tcs.se.bugs;

import de.ugoe.cs.tcs.se.developer.SEDeveloper;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.util.ContextUtils;

public class CriticalBugOpen extends CriticalBug {
	private static int counter = 0;
	
    public CriticalBugOpen() {
        this.creationTime = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
        counter++;
    }
    
    @Override
    public CriticalBugClosed closeBug(SEDeveloper developer) {
		@SuppressWarnings("unchecked")
		Context<Object> context = ContextUtils.getContext(this);
		
		CriticalBugClosed closed = new CriticalBugClosed(this.creationTime, developer);
    	
    	context.remove(this);
    	context.add(closed);
    	
    	return closed;
    }     
    
	public static int getCount() {
		return counter;
	}   
	
	public static void clearOpenBugCount() {
		counter=0;
	}
}
