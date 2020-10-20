package de.ugoe.cs.tcs.se.refactorings;

import java.util.List;

import de.ugoe.cs.tcs.se.graph.SEFile;
import de.ugoe.cs.tcs.se.graph.SENode;
import repast.simphony.context.Context;

public abstract class Refactoring {
	protected Context<Object> context;
	protected List<SEFile> changedFiles;
	
	protected SENode startPoint;
	
	public Refactoring(Context<Object> context, List<SEFile> changedFiles) {
		this.context = context;
		this.changedFiles = changedFiles;
	}

	abstract boolean findMatching();

	abstract void applyRefactoring();
}
