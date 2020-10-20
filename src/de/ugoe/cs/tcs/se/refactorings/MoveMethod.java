package de.ugoe.cs.tcs.se.refactorings;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import de.ugoe.cs.tcs.se.SEContext;
import de.ugoe.cs.tcs.se.graph.SEClass;
import de.ugoe.cs.tcs.se.graph.SEFile;
import de.ugoe.cs.tcs.se.graph.SEMethod;
import repast.simphony.context.Context;
import repast.simphony.space.graph.RepastEdge;

public class MoveMethod extends Refactoring {

	public MoveMethod(Context<Object> context, List<SEFile> changedFiles) {
		super(context, changedFiles);
	}

	@Override
	public boolean findMatching() {
		// find class with high NOI
		// TODO: get number of HIGH from mined data to find a realistic threshold
		// TODO: do not compute class.NOI every time?
		
		List<SEClass> classes = Lists.newArrayList(SEContext.baseContext().getObjects(SEClass.class)).stream().map(a -> (SEClass) a).filter(a -> a.getNOI() > 20).sorted(new Comparator<SEClass>() {
			public int compare(SEClass a1, SEClass a2) {
				return a1.getNOI() < a2.getNOI() ? -1 : a1.getNOI() > a2.getNOI() ? 1 : 0;
			}
		}).collect(Collectors.toList());
		
		if (classes.size() > 0) { 
			//TODO: Get any of the first x big methods.
			startPoint = classes.get(0); 
		}
		
		return startPoint == null ? false : true;				
	}

	@Override
	public void applyRefactoring() {
		// find method with most NOI edges
		
		// Get all methods of the current class.
		List<SEMethod> methods = Misc.getMethodsOfClass(SEContext.membershipMethod, (SEClass)startPoint);
		
		// Get first method with outgoing invocation
		SEMethod methodToMove = null;
		SEMethod calledMethod = null;		
		for (SEMethod m : methods) { // TODO: find strategy to get more appropriate edges instead of the first one
			for (RepastEdge<Object> e : SEContext.methodCall.getOutEdges(m)) {
				if (!Misc.getClassOfMethod(SEContext.membershipMethod, (SEMethod)e.getTarget()).equals(startPoint)) {
					methodToMove = (SEMethod)e.getSource();
					calledMethod = (SEMethod)e.getTarget();
				}
			}				
		}		
				
		// find target class of called methods
		SEClass classToMove = Misc.getClassOfMethod(SEContext.membershipMethod, calledMethod);
		
		// move method from startingPoint class to other class
		RepastEdge<Object> edgeToRemove = SEContext.membershipMethod.getEdge(methodToMove, startPoint);
		SEContext.membershipMethod.removeEdge(edgeToRemove);
		SEContext.membershipMethod.addEdge(methodToMove, classToMove);
		changedFiles.add(classToMove.getFile());
		changedFiles.add(((SEClass)startPoint).getFile());
		SEContext.APPLIED_MM++;
	}

}
