package de.ugoe.cs.tcs.se.graph;

import de.ugoe.cs.tcs.se.SEContext;

public class SEMethod extends SENode {
	private int loc;
	private int mccc;
	private String name;
	
	public SEMethod() { }
	
	public SEMethod(String name) {
		this.name = name;
	}
		
	public String getName() {
		return name;
	}	
	
	public int getLOC() {
		return loc;
	}
	
	public void setLOC(int loc) {
		this.loc = loc;
	}	
	
	public int getMcCC() {
		return mccc;
	}

	public void setMcCC(int mccc) {
		this.mccc = mccc;
	}

	public int getNOI() {			
		return SEContext.methodCall.getOutDegree(this);
	}
}
