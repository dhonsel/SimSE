package de.ugoe.cs.tcs.se.graph.init;

import de.ugoe.cs.tcs.se.developer.SEDeveloper;

public class DeveloperContribution {
	private SEDeveloper developer;
	private int touches;
	
	public DeveloperContribution(SEDeveloper developer, int touches) {
		this.developer = developer;
		this.touches = touches;
	}

	public SEDeveloper getDeveloper() {
		return developer;
	}

	public int getTouches() {
		return touches;
	}

	@Override
	public String toString() {
		return "DeveloperContribution [developer=" + developer + ", touches=" + touches + "]";
	}
	
	
}
