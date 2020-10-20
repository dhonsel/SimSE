package de.ugoe.cs.tcs.se.bugs;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import de.ugoe.cs.tcs.se.SEContext;
import de.ugoe.cs.tcs.se.graph.SEFile;
import repast.simphony.space.graph.RepastEdge;

public class BugCollection {
	private final ArrayList<NormalBugOpen> normalBugs = Lists.newArrayList();
	private final ArrayList<CriticalBugOpen> criticalBugs = Lists.newArrayList();
	private final ArrayList<MajorBugOpen> majorBugs = Lists.newArrayList();
	private final ArrayList<MinorBugOpen> minorBugs = Lists.newArrayList();

	public BugCollection(List<SEFile> changedFiles) {
		for (SEFile a : changedFiles) {
			if (((SEFile) a).numOfBugs() > 0) {
				for (RepastEdge<Object> e : SEContext.bugFile.getInEdges(a)) {
					if (e.getSource() instanceof MajorBugOpen) {
						majorBugs.add((MajorBugOpen) e.getSource());
					} else if (e.getSource() instanceof NormalBugOpen) {
						normalBugs.add((NormalBugOpen) e.getSource());
					} else if (e.getSource() instanceof MinorBugOpen) {
						minorBugs.add((MinorBugOpen) e.getSource());
					} else if (e.getSource() instanceof CriticalBugOpen) {
						criticalBugs.add((CriticalBugOpen) e.getSource());
					}
				}
			}
		}
	}

	public ArrayList<MajorBugOpen> getMajorBugs() {
		return majorBugs;
	}

	public ArrayList<NormalBugOpen> getNormalBugs() {
		return normalBugs;
	}

	public ArrayList<MinorBugOpen> getMinorBugs() {
		return minorBugs;
	}

	public ArrayList<CriticalBugOpen> getCriticalBugs() {
		return criticalBugs;
	}
	
}
