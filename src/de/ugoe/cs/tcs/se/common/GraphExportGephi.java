package de.ugoe.cs.tcs.se.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.StringUtils;

import de.ugoe.cs.tcs.se.SEContext;
import de.ugoe.cs.tcs.se.graph.SEFile;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.space.graph.RepastEdge;


public class GraphExportGephi {

	
	
	public void writeChangeCouplingGraph() {
		String year = String.valueOf((((int) RunEnvironment.getInstance().getCurrentSchedule().getTickCount()) / 365) + 1 +  RunEnvironment.getInstance().getParameters().getInteger("startYear"));

		try {
			File f = new File("output/change_coupling");
			if (!f.exists()) {
				f.mkdirs();
			}
			PrintWriter writerEdge = new PrintWriter("output/change_coupling/ChangeCoupling_Nodes_" + RunEnvironment.getInstance().getParameters().getString("projectName") + (RunEnvironment.getInstance().getParameters().getBoolean("refactoring") ? "_ref" : "") + "_" + StringUtils.leftPad(year, 2, '0') + ".csv", "UTF-8");
			writerEdge.println("Id,Category,Value");
			for (Object file : SEContext.baseContext().getObjects(SEFile.class)) {
				writerEdge.println(file.toString() + "," + ((SEFile) file).getCategory().getName() + "," + ((SEFile) file).getLabelValue());
			}
			writerEdge.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		try {
			File f = new File("output/change_coupling");
			if (!f.exists()) {
				f.mkdirs();
			}
			PrintWriter writer = new PrintWriter("output/change_coupling/ChangeCoupling_Edges_"  + RunEnvironment.getInstance().getParameters().getString("projectName") + (RunEnvironment.getInstance().getParameters().getBoolean("refactoring") ? "_ref" : "") + "_" +  StringUtils.leftPad(year, 2, '0') + ".csv", "UTF-8");
			writer.println("Source,Target,Weight,Type");
			for (RepastEdge<Object> edge : SEContext.changeCoupling.getEdges()) {
				String source = ((SEFile) edge.getSource()).toString();
				String target = ((SEFile) edge.getTarget()).toString();
				writer.println(source + "," + target + "," + edge.getWeight() + ",Undirected");
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void writeChangeCouplingGraph(int y) {
		String year = String.valueOf(y);

		try {
			File f = new File("output/change_coupling");
			if (!f.exists()) {
				f.mkdirs();
			}
			PrintWriter writerEdge = new PrintWriter("output/change_coupling/ChangeCoupling_Nodes_" + RunEnvironment.getInstance().getParameters().getString("projectName") + (RunEnvironment.getInstance().getParameters().getBoolean("refactoring") ? "_ref" : "") + "_" + StringUtils.leftPad(year, 2, '0') + ".csv", "UTF-8");
			writerEdge.println("Id,Category,Value");
			RepastEdge<Object> edge = SEContext.changeCoupling.getEdges().iterator().next();
			writerEdge.println(edge.getSource().toString() + "," + ((SEFile) edge.getSource()).getCategory().getName() + "," + ((SEFile) edge.getSource()).getLabelValue());
			writerEdge.println(edge.getTarget().toString() + "," + ((SEFile) edge.getTarget()).getCategory().getName() + "," + ((SEFile) edge.getTarget()).getLabelValue());
			writerEdge.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		try {
			File f = new File("output/change_coupling");
			if (!f.exists()) {
				f.mkdirs();
			}
			PrintWriter writer = new PrintWriter("output/change_coupling/ChangeCoupling_Edges_"  + RunEnvironment.getInstance().getParameters().getString("projectName") + (RunEnvironment.getInstance().getParameters().getBoolean("refactoring") ? "_ref" : "") + "_" +  StringUtils.leftPad(year, 2, '0') + ".csv", "UTF-8");
			writer.println("Source,Target,Weight,Type");
			RepastEdge<Object> edge = SEContext.changeCoupling.getEdges().iterator().next();
			String source = ((SEFile) edge.getSource()).toString();
			String target = ((SEFile) edge.getTarget()).toString();
			writer.println(source + "," + target + "," + edge.getWeight() + ",Undirected");
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public void writeChangeCouplingGraphDOT() {
		String year = String.valueOf((((int) RunEnvironment.getInstance().getCurrentSchedule().getTickCount()) / 365) + 1 + RunEnvironment.getInstance().getParameters().getInteger("startYear"));
		try {
			File f = new File("output/change_coupling");
			if (!f.exists()) {
				f.mkdirs();
			}
			PrintWriter writer = new PrintWriter("output/change_coupling/ChangeCoupling_"  + RunEnvironment.getInstance().getParameters().getString("projectName") + (RunEnvironment.getInstance().getParameters().getBoolean("refactoring") ? "_ref" : "") + "_" + StringUtils.leftPad(year, 2, '0') + ".dot", "UTF-8");
			writer.println("graph {");
			for (RepastEdge<Object> edge : SEContext.changeCoupling.getEdges()) {
				String source = ((SEFile) edge.getSource()).toString();
				String target = ((SEFile) edge.getTarget()).toString();
				writer.println("  " + source + " -- " + target + " [ weight=\"" + edge.getWeight() + "\" ];");
			}			
			writer.println("}");
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public void writeChangeCouplingGraphDOT(int y) {
		String year = String.valueOf(y);
		try {
			File f = new File("output/change_coupling");
			if (!f.exists()) {
				f.mkdirs();
			}
			PrintWriter writer = new PrintWriter("output/change_coupling/ChangeCoupling_"  + RunEnvironment.getInstance().getParameters().getString("projectName") + (RunEnvironment.getInstance().getParameters().getBoolean("refactoring") ? "_ref" : "") + "_" + StringUtils.leftPad(year, 2, '0') + ".dot", "UTF-8");
			writer.println("graph {");
			RepastEdge<Object> edge = SEContext.changeCoupling.getEdges().iterator().next();
			String source = ((SEFile) edge.getSource()).toString();
			String target = ((SEFile) edge.getTarget()).toString();
			writer.println("  " + source + " -- " + target + " [ weight=\"" + edge.getWeight() + "\" ];");
			writer.println("}");
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}	

}
