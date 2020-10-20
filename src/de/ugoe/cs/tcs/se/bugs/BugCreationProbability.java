package de.ugoe.cs.tcs.se.bugs;

public class BugCreationProbability {
	private final double pNormal;
	private final double pCritical;
	private final double pMajor;
	private final double pMinor;
	
	public BugCreationProbability(double pNormal, double pCritical, double pMajor, double pMinor) {
		super();
		this.pNormal = pNormal;
		this.pCritical = pCritical;
		this.pMajor = pMajor;
		this.pMinor = pMinor;
	}

	public double getpNormal() {
		return pNormal;
	}

	public double getpCritical() {
		return pCritical;
	}

	public double getpMajor() {
		return pMajor;
	}

	public double getpMinor() {
		return pMinor;
	}
	
}
