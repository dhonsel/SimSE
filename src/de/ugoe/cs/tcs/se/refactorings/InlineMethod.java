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

public class InlineMethod extends Refactoring {

	public InlineMethod(Context<Object> context, List<SEFile> changedFiles) {
		super(context, changedFiles);
	}

	@Override
	public boolean findMatching() {
		List<SEMethod> methods = Lists.newArrayList(SEContext.baseContext().getObjects(SEMethod.class)).stream().map(a -> (SEMethod) a).filter(a -> a.getLOC() < 7).sorted(new Comparator<SEMethod>() {
			public int compare(SEMethod a1, SEMethod a2) {
				return a1.getLOC() > a2.getLOC() ? -1 : a1.getLOC() < a2.getLOC() ? 1 : 0;
			}
		}).collect(Collectors.toList());
		
		if (methods.size() > 0) {
			startPoint = methods.get(0); //TODO: Get any of the first x small methods.
		}
		
		return startPoint == null ? false : true;
	}

	@Override
	public void applyRefactoring() {
		// get methods class
		SEMethod method = (SEMethod) startPoint;
		SEClass clazz = Misc.getClassOfMethod(SEContext.membershipMethod, method);
		changedFiles.add(clazz.getFile());
		
		var inEdges = Lists.newArrayList(SEContext.methodCall.getInEdges(method));
		if (inEdges.size() > 0) {
			SEMethod caller = (SEMethod) inEdges.get(0).getSource(); //TODO: Get any method
			int deltaLOC = Util.randomGeometric(SEContext.P_IM_METHOD_DELTA_CALLER_LOC);
			int deltaMcCC = Util.randomGeometric(SEContext.P_IM_METHOD_DELTA_CALLER_MCCC);
			int deltaNOI = Util.randomGeometric(SEContext.P_IM_METHOD_DELTA_CALLER_NOI);
			caller.setLOC(caller.getLOC() + deltaLOC);
			caller.setMcCC(caller.getMcCC() + deltaMcCC);
			for (int i = 0; i < deltaNOI; i++) {
				SEMethod target = Util.randomMethod(clazz.getFile());
				if (target != null && caller != target) {
					SEContext.methodCall.addEdge(caller, target);
				}
			}
			context.remove(method);
			SEClass callerClass = Misc.getClassOfMethod(SEContext.membershipMethod, caller);
			if (!changedFiles.contains(callerClass.getFile())) {
				changedFiles.add(callerClass.getFile());
			}
			SEContext.APPLIED_IM++;
		}
	}

}
