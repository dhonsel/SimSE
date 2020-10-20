package de.ugoe.cs.tcs.se.refactorings;

import java.util.List;

import com.google.common.collect.Lists;

import de.ugoe.cs.tcs.se.graph.SEClass;
import de.ugoe.cs.tcs.se.graph.SEMethod;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;

public class Misc {
	
	/**
	 * This method gets the method were the given method is implemented in.
	 * @param membership The graph representing the software under simulation.
	 * @param method The method which class is requested.
	 * @return The class of the given method.
	 */
	public static SEClass getClassOfMethod(Network<Object> membership, SEMethod method) {
		SEClass clazz = null;
		if (membership == null || method == null) {
			return clazz;
		}
		for (RepastEdge<Object> e : membership.getEdges(method)) {
			clazz = (SEClass) e.getTarget();
		}
		return clazz;
	}
	
	/**
	 * This method returns all methods implemented in the given class.
	 * @param membership The graph representing the software under simulation.
	 * @param clazz The class which methods are requested.
	 * @return The methods of the given class.
	 */
	public static List<SEMethod> getMethodsOfClass(Network<Object> membership, SEClass clazz) {
		List<SEMethod> methods = Lists.newArrayList();
		if (clazz == null) {
			return methods;
		}
		for (RepastEdge<Object> e : membership.getEdges(clazz)) {
			methods.add((SEMethod)e.getSource());
		}
		return methods;
	}
	
}
