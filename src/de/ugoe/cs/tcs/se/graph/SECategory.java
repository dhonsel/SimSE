package de.ugoe.cs.tcs.se.graph;

import java.util.List;

import com.google.common.collect.Lists;

public class SECategory extends SENode implements Comparable<SECategory> {
	private String name;
	private double p;
	private final List<SEFile> files;
	private long numOfSelections;
	
	public SECategory(String name, double p) {
		super();
		this.files = Lists.newArrayList();
		this.name = name;
		this.p = p;
	}

	public String getName() {
		return name;
	}

	public double getP() {
		return p;
	}
	
	public void addFile(SEFile file) {
		files.add(file);
	}
	
	public List<SEFile> getFiles() {
		return files;
	}
	
	public void increaseSelections() {
		numOfSelections++;
	}
	
	@Override
	public int compareTo(SECategory o) {
		return Double.compare(this.getP(), o.getP());
	}

	@Override
	public String toString() {
		return "SECategory [name=" + name + ", p=" + p + ", numOfSelections=" + numOfSelections + "]";
	}

	public double averageLabelValue() {
		double value = 0;
		for (SEFile a : files) {
			value += a.getLabelValue();
		}
		return value / files.size();
	}	
}
