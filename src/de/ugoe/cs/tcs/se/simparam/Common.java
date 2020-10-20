package de.ugoe.cs.tcs.se.simparam;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import repast.simphony.engine.environment.RunEnvironment;

public class Common {
	public final static int MAX_FITNESS = 20;
	public final static int MAX_COMPLEXETY = 20;
	public final static int MAX_EXPERIENCE = 20;
	public final static int MAX_DEVELOPER = 250;
	
	private Common() {
		init();
	}
	private static Common theInstance;
		
	public static Common getInstance() {
		if (theInstance != null) {
			return theInstance;
		} else {
			theInstance = new Common();
			return theInstance;
		}
	}
	
	private CoreData coreData;

	public CoreData getCoreData() {
		return coreData;
	}

	private void setCoreData(CoreData coreData) {
		this.coreData = coreData;
	}

	private void init() {
		// read simulation parameters
	    ObjectMapper objectMapper = new ObjectMapper();
	    StringBuilder filename = new StringBuilder();
	    filename.append("input/");
	    filename.append(RunEnvironment.getInstance().getParameters().getValueAsString("projectName"));
	    filename.append("_data.json");
	    try {
	    	setCoreData(objectMapper.readValue(new File(filename.toString()), CoreData.class));
	    } catch (IOException e) {
	      e.printStackTrace();
	    }		
	}
	
}
