package de.ugoe.cs.tcs.se.common;

import java.util.stream.StreamSupport;

import de.ugoe.cs.tcs.se.SEContext;

public class DebugAgent {
	private static DebugAgent instance = new DebugAgent();

	private DebugAgent() { }

	public static DebugAgent getInstance() {
		return instance;
	}

	public double debugNumberOfCCEdges() {
		return StreamSupport.stream(SEContext.changeCoupling.getEdges().spliterator(), false).count();
	}

}
