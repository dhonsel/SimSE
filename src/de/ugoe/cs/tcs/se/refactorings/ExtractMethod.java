package de.ugoe.cs.tcs.se.refactorings;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import de.ugoe.cs.tcs.se.SEContext;
import de.ugoe.cs.tcs.se.common.Util;
import de.ugoe.cs.tcs.se.graph.SEClass;
import de.ugoe.cs.tcs.se.graph.SEFile;
import de.ugoe.cs.tcs.se.graph.SEMethod;
import repast.simphony.context.Context;

public class ExtractMethod extends Refactoring {

	public ExtractMethod(Context<Object> context, List<SEFile> changedFiles) {
		super(context, changedFiles);
	}

	@Override
	public boolean findMatching() {
		List<SEMethod> methods = Lists.newArrayList(SEContext.baseContext().getObjects(SEMethod.class)).stream().map(a -> (SEMethod) a).filter(a -> a.getLOC() > 42).sorted(new Comparator<SEMethod>() {
			public int compare(SEMethod a1, SEMethod a2) {
				return a1.getLOC() < a2.getLOC() ? -1 : a1.getLOC() > a2.getLOC() ? 1 : 0;
			}
		}).collect(Collectors.toList());
		
		if (methods.size() > 0) {
			//TODO: Get any of the first x big methods.
			startPoint = methods.get(0); 
		}
		
		return startPoint == null ? false : true;
	}

	@Override
	public void applyRefactoring() {
		// get methods class
		SEMethod method = (SEMethod) startPoint;
		SEClass clazz = Misc.getClassOfMethod(SEContext.membershipMethod, method);
		changedFiles.add(clazz.getFile());
		
		int deltaLOC = Util.randomGeometric(SEContext.P_EM_METHOD_DELTA_LOC);
		int deltaMcCC = Util.randomGeometric(SEContext.P_EM_METHOD_DELTA_MCCC);
		int newLOC = Util.randomGeometric(SEContext.P_EM_METHOD_NEW_LOC);
		int newMcCC = Util.randomGeometric(SEContext.P_EM_METHOD_NEW_MCCC);
		int newNOI = Util.randomGeometric(SEContext.P_EM_METHOD_NEW_NOI);
		
		SEMethod newMethod = new SEMethod("new Method");
		context.add(newMethod);
		newMethod.setLOC(newLOC);
		newMethod.setMcCC(newMcCC);
		for (int i = 0; i < newNOI; i++) {
			SEMethod target = Util.randomMethod(clazz.getFile());
			if (target != null && newMethod != target) {
				SEContext.methodCall.addEdge(newMethod, target);
			}
		}		
		
		method.setLOC(method.getLOC() - deltaLOC);
		if (method.getLOC() < 3) {
			method.setLOC(3);
		}
		method.setMcCC(method.getMcCC() - deltaMcCC);
		if (method.getMcCC() < 0) {
			method.setMcCC(0);
		}
		
		SEContext.membershipMethod.addEdge(newMethod, clazz);
		SEContext.methodCall.addEdge(method, newMethod);
		SEContext.APPLIED_EM++;
	}

}
