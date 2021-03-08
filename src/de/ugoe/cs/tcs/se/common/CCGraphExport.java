package de.ugoe.cs.tcs.se.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.StringUtils;

import de.ugoe.cs.tcs.se.SEContext;
import de.ugoe.cs.tcs.se.graph.SECategory;
import de.ugoe.cs.tcs.se.graph.SEFile;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.space.graph.RepastEdge;

public class CCGraphExport {

	public void writeChangeCouplingGraph() {
		String year = String.valueOf((((int) RunEnvironment.getInstance().getCurrentSchedule().getTickCount()) / 365)
				+ 1 + RunEnvironment.getInstance().getParameters().getInteger("startYear"));

		try {
			File f = new File("output/change_coupling");
			if (!f.exists()) {
				f.mkdirs();
			}
			PrintWriter writerNode = new PrintWriter(createFilename(year, "csv", "Nodes_"), "UTF-8");
			writerNode.println("Id,Category,Value");
			for (Object file : SEContext.baseContext().getObjects(SEFile.class)) {
				writerNode.println(file.toString() + "," + ((SEFile) file).getCategory().getName() + ","
						+ ((SEFile) file).getLabelValue());
			}
			writerNode.close();
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
			PrintWriter writerNode = new PrintWriter(createFilename(year, "csv", "Edges_"), "UTF-8");
			writerNode.println("Source,Target,Weight,Type");
			for (RepastEdge<Object> edge : SEContext.changeCoupling.getEdges()) {
				String source = ((SEFile) edge.getSource()).toString();
				String target = ((SEFile) edge.getTarget()).toString();
				writerNode.println(source + "," + target + "," + edge.getWeight() + ",Undirected");
			}
			writerNode.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * If the simulation starts at a given time, default graphs with only one node
	 * are generated here for the non-simulated years. They are used by the tools
	 * for further evaluation 
	 * @param y The year for which the default graph should be generated.
	 */
	public void writeChangeCouplingGraph(int y) {
		String year = String.valueOf(y);

		try {
			File f = new File("output/change_coupling");
			if (!f.exists()) {
				f.mkdirs();
			}
			PrintWriter writerNode = new PrintWriter(createFilename(year, "csv", "Nodes_"), "UTF-8");
			writerNode.println("Id,Category,Value");
			RepastEdge<Object> edge = SEContext.changeCoupling.getEdges().iterator().next();
			writerNode.println(edge.getSource().toString() + "," + ((SEFile) edge.getSource()).getCategory().getName()
					+ "," + ((SEFile) edge.getSource()).getLabelValue());
			writerNode.println(edge.getTarget().toString() + "," + ((SEFile) edge.getTarget()).getCategory().getName()
					+ "," + ((SEFile) edge.getTarget()).getLabelValue());
			writerNode.close();
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
			PrintWriter writerNode = new PrintWriter(createFilename(year, "csv", "Edges_"), "UTF-8");
			writerNode.println("Source,Target,Weight,Type");
			RepastEdge<Object> edge = SEContext.changeCoupling.getEdges().iterator().next();
			String source = ((SEFile) edge.getSource()).toString();
			String target = ((SEFile) edge.getTarget()).toString();
			writerNode.println(source + "," + target + "," + edge.getWeight() + ",Undirected");
			writerNode.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void writeChangeCouplingGraphDOT() {
		String year = String.valueOf((((int) RunEnvironment.getInstance().getCurrentSchedule().getTickCount()) / 365)
				+ 1 + RunEnvironment.getInstance().getParameters().getInteger("startYear"));
		try {
			File f = new File("output/change_coupling");
			if (!f.exists()) {
				f.mkdirs();
			}
			PrintWriter writer = new PrintWriter(createFilename(year, "dot", ""), "UTF-8");
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

	/**
	 * If the simulation starts at a given time, default graphs with only one node
	 * are generated here for the non-simulated years. They are used by the tools
	 * for further evaluation 
	 * @param y The year for which the default graph should be generated.
	 */
	public void writeChangeCouplingGraphDOT(int y) {
		String year = String.valueOf(y);
		try {
			File f = new File("output/change_coupling");
			if (!f.exists()) {
				f.mkdirs();
			}
			PrintWriter writer = new PrintWriter(createFilename(year, "dot", ""), "UTF-8");
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

	public void writeChangeCouplingGraphCGR() {
		String year = String.valueOf((((int) RunEnvironment.getInstance().getCurrentSchedule().getTickCount()) / 365)
				+ 1 + RunEnvironment.getInstance().getParameters().getInteger("startYear"));
		try {
			File f = new File("output/change_coupling");
			if (!f.exists()) {
				f.mkdirs();
			}
			PrintWriter writer =  new PrintWriter(createFilename(year, "cgr", ""), "UTF-8");
			writer.println("#graph#");
			writer.println("Name=Simulated year: " + StringUtils.leftPad(year, 2, '0'));
			writer.println('\n' + "#nodes#");
			writer.println("Id,Category,Value");
			for (Object artifact : SEContext.baseContext().getObjects(SEFile.class)) {
				writer.println(artifact.toString() + "," + ((SEFile) artifact).getCategory().getName() + ","
						+ ((SEFile) artifact).getLabelValue());
			}
			writer.println('\n' + "#edges#");
			writer.println("Source,Target,Weight,Type");
			for (RepastEdge<Object> edge : SEContext.changeCoupling.getEdges()) {
				String source = ((SEFile) edge.getSource()).toString();
				String target = ((SEFile) edge.getTarget()).toString();
				writer.println(source + "," + target + "," + edge.getWeight() + ",Undirected");
			}
			writer.println('\n' + "#statistics#");
			writer.println("Average Label Value: " + Util.computeAverageLabelValue());
			for (SECategory c : SEContext.CATEGORIES) {
				writer.println("Average Label Value of Category: " + c.getName() + " Value: " + c.averageLabelValue());
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	
	public void writeChangeCouplingGraphCGR(int y) {
		String year = String.valueOf(y);
		try {
			File f = new File("output/change_coupling");
			if (!f.exists()) {
				f.mkdirs();
			}
			RepastEdge<Object> edge = SEContext.changeCoupling.getEdges().iterator().next();
			SEFile source = (SEFile) edge.getSource();
			SEFile target = (SEFile) edge.getTarget();

			PrintWriter writer = new PrintWriter(createFilename(year, "cgr", ""), "UTF-8");
			writer.println("#graph#");
			writer.println("Name=Simulated year: " + StringUtils.leftPad(year, 2, '0'));
			writer.println('\n' + "#nodes#");
			writer.println("Id,Category,Value");
			writer.println(source.toString() + "," + source.getCategory().getName() + "," + source.getLabelValue());
			writer.println(target.toString() + "," + target.getCategory().getName() + "," + target.getLabelValue());
			writer.println('\n' + "#edges#");
			writer.println("Source,Target,Weight,Type");
			writer.println(source.toString() + "," + target.toString() + "," + edge.getWeight() + ",Undirected");
			writer.println('\n' + "#statistics#");
			writer.println("Average Label Value: " + Util.computeAverageLabelValue());
			SECategory c = source.getCategory();
			writer.println("Average Label Value of Category: " + c.getName() + " Value: " + c.averageLabelValue());
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private String createFilename(String year, String fileType, String nameSuffix) {
		return "output/change_coupling/ChangeCoupling_" + nameSuffix
				+ RunEnvironment.getInstance().getParameters().getString("projectName")
				+ (RunEnvironment.getInstance().getParameters().getBoolean("refactoring") ? "_ref" : "") + "_"
				+ StringUtils.leftPad(year, 2, '0') + "." + fileType;
	}
}
