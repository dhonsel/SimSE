package de.ugoe.cs.tcs.se.developer;

import java.util.UUID;

import de.ugoe.cs.tcs.se.simparam.Common;
import de.ugoe.cs.tcs.se.simparam.CoreData;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;

public class MajorDeveloper extends SEDeveloper {
	
	public MajorDeveloper(double commitsPerRound,  CommitProbabilities cp, Parameters params) {
		super(commitsPerRound, cp, params);
	}

	public MajorDeveloper(double commitsPerRound, CommitProbabilities cp, Parameters params, UUID objectID, String name, boolean maintainer) {
		super(commitsPerRound, cp, params, objectID, name, maintainer);
	}	
	
	@ScheduledMethod(start = 1, interval = 1)
	public void doSomeWork() {
		if (!isActive()) {
			return;
		}
		
		baseWork();
	}

	@Override
	public double computeFixProbability() {
		CoreData cd = Common.getInstance().getCoreData();
		return cd.getMajorDeveloperFixes() * 1.0 / cd.getMajorDeveloperCommits() * 1.0;
	}

	@Override
	public void bugfix() {
		simpleBugfix();
		for (int i = 0; i < params.getInteger("fixRuns"); i++) {
			seperateBugfix();
		}
	}
}
