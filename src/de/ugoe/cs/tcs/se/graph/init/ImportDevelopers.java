package de.ugoe.cs.tcs.se.graph.init;

import de.ugoe.cs.tcs.se.developer.CommitProbabilities;
import de.ugoe.cs.tcs.se.developer.CoreDeveloper;
import de.ugoe.cs.tcs.se.developer.KeyDeveloper;
import de.ugoe.cs.tcs.se.developer.MajorDeveloper;
import de.ugoe.cs.tcs.se.developer.MinorDeveloper;
import de.ugoe.cs.tcs.se.developer.PeripheralDeveloper;
import de.ugoe.cs.tcs.se.developer.SEDeveloper;
import de.ugoe.cs.tcs.se.simparam.CoreData;
import de.ugoe.cs.tcs.se.simparam.DeveloperRole;
import de.ugoe.cs.tcs.se.simparam.DeveloperType;
import repast.simphony.context.Context;
import repast.simphony.parameter.Parameters;

public class ImportDevelopers {

	public static void create(final Context<Object> context, CoreData cd, CommitProbabilities cp, Parameters params) {
		//TODO: consider developer specific commit probabilities for imported developers
		if (params.getString("developer").equals("role")) {
			for (var i : cd.getIdentities()) {
				if (i.getRole().equals(DeveloperRole.core)) {
					double coreCpr = (cd.getCoreDeveloperCommits() / cd.getCoreDeveloper() * 1.0) / cd.getRoundsToSimulate();
					SEDeveloper d = new CoreDeveloper(coreCpr, cp, params, i.getObjectID(), i.getName(), i.isMaintainer());
					context.add(d);
				} else {
					double peripheralCpr = (cd.getPeripheralDeveloperCommits() / cd.getPeripheralDeveloper() * 1.0) / cd.getRoundsToSimulate();
					SEDeveloper d = new PeripheralDeveloper(peripheralCpr, cp, params, i.getObjectID(), i.getName(), i.isMaintainer());
					context.add(d);
				}
			}
		} else if (params.getString("developer").equals("type")) {
			for (var i : cd.getIdentities()) {
				if (i.getType().equals(DeveloperType.key)) {
					double coreCpr = (cd.getKeyDeveloperCommits() / cd.getKeyDeveloper() * 1.0) / cd.getRoundsToSimulate();
					SEDeveloper d = new KeyDeveloper(coreCpr, cp, params, i.getObjectID(), i.getName(), i.isMaintainer());
					context.add(d);
				} else if (i.getType().equals(DeveloperType.major)) {
					double peripheralCpr = (cd.getMajorDeveloperCommits() / cd.getMajorDeveloper() * 1.0) / cd.getRoundsToSimulate();
					SEDeveloper d = new MajorDeveloper(peripheralCpr, cp, params, i.getObjectID(), i.getName(), i.isMaintainer());
					context.add(d);
				} else {
					double peripheralCpr = (cd.getMinorDeveloperCommits() / cd.getMinorDeveloper() * 1.0) / cd.getRoundsToSimulate();
					SEDeveloper d = new MinorDeveloper(peripheralCpr, cp, params, i.getObjectID(), i.getName(), i.isMaintainer());
					context.add(d);
				}
			}			
		} else {
			System.err.println("Wrong developer type!");
		}
	}
}
