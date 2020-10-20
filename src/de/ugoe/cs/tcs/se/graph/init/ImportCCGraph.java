package de.ugoe.cs.tcs.se.graph.init;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.jgrapht.graph.Multigraph;
import org.jgrapht.io.DOTImporter;
import org.jgrapht.io.EdgeProvider;
import org.jgrapht.io.ImportException;
import org.jgrapht.io.VertexProvider;

import de.ugoe.cs.tcs.se.SEContext;
import de.ugoe.cs.tcs.se.common.Util;
import de.ugoe.cs.tcs.se.developer.SEDeveloper;
import de.ugoe.cs.tcs.se.graph.SECategory;
import de.ugoe.cs.tcs.se.graph.SEFile;
import repast.simphony.context.Context;
import repast.simphony.space.graph.RepastEdge;

public class ImportCCGraph {
	private final String fileName;
	private final Context<Object> context;

	public ImportCCGraph(String fileName, Context<Object> context) {
		this.fileName = fileName;
		this.context = context;
	}

	private String readFile() {
		String graphDOT = "";
		File file = new File(fileName);
		try {
			graphDOT = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return graphDOT;
	}

	private Multigraph<ImportVertex, ImportEdge> createImportGraph() {
		String input = readFile();

		Multigraph<ImportVertex, ImportEdge> result = new Multigraph<ImportVertex, ImportEdge>(ImportEdge.class);

		VertexProvider<ImportVertex> vp = (label, attrs) -> new ImportVertex(label, attrs);

		EdgeProvider<ImportVertex, ImportEdge> ep = (f, t, l, attrs) -> new ImportEdge(l, attrs);

		DOTImporter<ImportVertex, ImportEdge> importer = new DOTImporter<ImportVertex, ImportEdge>(vp, ep);

		try {
			importer.importGraph(result, new StringReader(input));
		} catch (ImportException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public void generateSimGraph() {
		Multigraph<ImportVertex, ImportEdge> importGraph = createImportGraph();
		
		for (var v : importGraph.vertexSet()) {
			SECategory cat = Util.getRandomCategorie();
			SEDeveloper creator = Util.findDeveloper(context, UUID.fromString(v.getAttributes().get("creator").getValue()));
			SEDeveloper owner = Util.findDeveloper(context, UUID.fromString(v.getAttributes().get("owner").getValue()));
			
			// compute touches
			var dc = Util.computeDeveloperContribution(context, v);
			int touches = 0;
			for (var d : dc) {
				touches += d.getTouches();
			}
			
			// create file as vertex for simulated cc graph
			SEFile f = new SEFile(creator, cat, Integer.valueOf(v.getId()));
			f.setOwner(owner);
			f.setTouches(touches);
			context.add(f);
			
			// created edges for developer file network
			for (var d : dc) {
				RepastEdge<Object> edge = SEContext.developerFile.addEdge(d.getDeveloper(), f);
				edge.setWeight(d.getTouches());				
			}
		}
		
		// create edges for cc network
		for (var e : importGraph.edgeSet()) {
			SEFile s = Util.findFile(context, Integer.valueOf(e.getSource().getId().toString()));
			SEFile t = Util.findFile(context, Integer.valueOf(e.getTarget().getId().toString()));
			double w = Double.valueOf(e.getAttributes().get("weight").getValue());
			SEContext.changeCoupling.addEdge(s, t, w);
		}
		
	}
	
	
	
}
