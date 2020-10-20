package de.ugoe.cs.tcs.se.bugs;

import java.util.Map;

import com.google.common.collect.Maps;

import de.ugoe.cs.tcs.se.developer.SEDeveloper;

public abstract class MinorBug extends Bug {
	protected static final Map<Class<SEDeveloper>, IncrementableInt> fixedByDeveloperType = Maps.newHashMap();
	
	public static String printBugsFixedByDeveloperType() {
		String res = "";
		res += "Number of minor bugs fixed by developer type:" + '\n';
		for (Class<SEDeveloper> c : fixedByDeveloperType.keySet()) {
			res += '\t' + c.getSimpleName() + ": " + fixedByDeveloperType.get(c).get() + '\n';
		}
		return res;
	}		
	
	public static void clearStatistics() {
		fixedByDeveloperType.clear();
	}	
}
