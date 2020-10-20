package de.ugoe.cs.tcs.se.graph;

import java.util.List;

import de.ugoe.cs.tcs.se.SEContext;
import de.ugoe.cs.tcs.se.refactorings.Misc;
import repast.simphony.space.graph.RepastEdge;

public class SEClass extends SENode {
	private String name;
	private SEFile file;
	
	public SEClass() { }
	
	public SEClass(String name) {
		this.name = name;
	}
		
	public String getName() {
		return name;
	}
	
	public SEFile getFile() {
		return file;
	}

	public void setFile(SEFile file) {
		this.file = file;
	}

	public int getLOC() {
		int loc = 0;
	
		// Get all methods of the current class.
		List<SEMethod> methods = Misc.getMethodsOfClass(SEContext.membershipMethod, this);
		
		// Count outgoing invocations to other classes.
		// TODO: Consider some constant value for class variables (mining).
		for (SEMethod m : methods) {
			loc += m.getLOC();
		}
		
		return loc;
	}
	
	public int getNOI() {
		int noi = 0;
		
		// Get all methods of the current class.
		List<SEMethod> methods = Misc.getMethodsOfClass(SEContext.membershipMethod, this);
		
		// Count outgoing invocations to other classes.
		for (SEMethod m : methods) {
			for (RepastEdge<Object> e : SEContext.methodCall.getOutEdges(m)) {
				if (!Misc.getClassOfMethod(SEContext.membershipMethod, (SEMethod)e.getTarget()).equals(this)) {
					noi++;
				}
			}				
		}
		
		methods.clear();
		return noi;		
	}
	
	public double getWMC() {
		double wmc = 0;
		
		// Get all methods of the current class.
		List<SEMethod> methods = Misc.getMethodsOfClass(SEContext.membershipMethod, this);
		double count = methods.size();
		
		// Build sum of methods McCC.
		for (SEMethod m : methods) {
			wmc += m.getMcCC();
		}
				
		methods.clear();
		return count == 0 ? 0 : wmc / count;				
	}
}
