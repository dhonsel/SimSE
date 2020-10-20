package de.ugoe.cs.tcs.se.graph;

import de.ugoe.cs.tcs.se.SEContext;
import de.ugoe.cs.tcs.se.bugs.Bug;
import de.ugoe.cs.tcs.se.bugs.CriticalBug;
import de.ugoe.cs.tcs.se.bugs.MajorBug;
import de.ugoe.cs.tcs.se.bugs.MinorBug;
import de.ugoe.cs.tcs.se.developer.SEDeveloper;
import repast.simphony.space.graph.RepastEdge;

public class SEFile extends SENode {
	private final int id;
	private int touches;
	private final SEDeveloper creator;
	private SEDeveloper owner;
	private SEDeveloper lastAuthor;
	private final SECategory category;
	private SEClass clazz;
	
	
	public SEFile(SEDeveloper creator, SECategory category) {
		super();
		this.id = 0;
		this.touches++;
		this.creator = creator;
		this.owner = creator;
		this.lastAuthor = creator;
		this.category = category;
	}
	
	public SEFile(SEDeveloper creator, SECategory category, int id) {
		super();
		this.id = id;
		this.touches++;
		this.creator = creator;
		this.owner = creator;
		this.lastAuthor = creator;
		this.category = category;
	}	
	
	public int getTouches() {
		return touches;
	}

	public void increaseTouches() {
		this.touches++;
	}

	public void setTouches(int touches) {
		this.touches = touches;
	}

	public SEDeveloper getOwner() {
		return owner;
	}

	public void setOwner(SEDeveloper owner) {
		this.owner = owner;
	}

	public SEDeveloper getLastAuthor() {
		return lastAuthor;
	}

	public void setLastAuthor(SEDeveloper lastAuthor) {
		this.lastAuthor = lastAuthor;
	}

	public SEDeveloper getCreator() {
		return creator;
	}

	public SECategory getCategory() {
		return category;
	}
	
	public SEClass getClazz() {
		return clazz;
	}

	public void setClazz(SEClass clazz) {
		this.clazz = clazz;
	}

	public int getId() {
		return id;
	}

	public void checkOwnership() {
		RepastEdge<Object> edgeToMaxChanges = null;
		RepastEdge<Object> edgeToCurrentOwner = SEContext.developerFile.getEdge(owner, this);
		int changes = 0;

		for (RepastEdge<Object> e : SEContext.developerFile.getEdges(this)) {
			if (changes < e.getWeight()) {
				edgeToMaxChanges = e;
				changes = (int) e.getWeight();
			}
		}

		if (edgeToMaxChanges.getWeight() > edgeToCurrentOwner.getWeight()) {
			this.owner = (SEDeveloper) edgeToMaxChanges.getSource();
		}
	}	
	
	public double numOfBugs() {
		return (double) SEContext.bugFile.getInDegree(this);
	}	
	
	/**
	 * This method returns the number of connected files/modules without
	 * direction.
	 * 
	 * @return The number of connected modules.
	 */
	public double getCouplingDegree() {
		return SEContext.changeCoupling.getDegree(this);
	}	
	
	public double getLabelValue() {
		int numOfNormalBugs = 0;
		int numOfCriticalBugs = 0;
		int numOfMajorBugs = 0;
		int numOfMinorBugs = 0;

		if (numOfBugs() > 0) {
			for (RepastEdge<Object> e : SEContext.bugFile.getInEdges(this)) {
				Bug bug = (Bug) e.getSource();
				if (bug instanceof CriticalBug) {
					numOfCriticalBugs++;
				} else if (bug instanceof MajorBug) {
					numOfMajorBugs++;
				} else if (bug instanceof MinorBug) {
					numOfMinorBugs++;
				} else {
					numOfNormalBugs++;
				}
			}
		}

		double labelValue = 1.0;
		if (numOfNormalBugs > 0) {
			labelValue *= Math.pow(SEContext.BUG_FACTOR_NORMAL, numOfNormalBugs);
		}		
		if (numOfCriticalBugs > 0) {
			labelValue *= Math.pow(SEContext.BUG_FACTOR_CRITICAL, numOfCriticalBugs);
		}
		if (numOfMajorBugs > 0) {
			labelValue *= Math.pow(SEContext.BUG_FACTOR_MAJOR, numOfMajorBugs);
		}
		if (numOfMinorBugs > 0) {
			labelValue *= Math.pow(SEContext.BUG_FACTOR_MINOR, numOfMinorBugs);
		}

		return labelValue;
	}	
}
