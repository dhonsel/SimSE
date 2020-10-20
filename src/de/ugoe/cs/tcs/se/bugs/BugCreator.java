package de.ugoe.cs.tcs.se.bugs;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;

import com.google.common.collect.Lists;

import de.ugoe.cs.tcs.se.SEContext;
import de.ugoe.cs.tcs.se.graph.SEFile;
import de.ugoe.cs.tcs.se.simparam.BugPriority;
import de.ugoe.cs.tcs.se.simparam.CoreData;

public class BugCreator {
	private Context<Object> context;
	private final CoreData cd;
	private final BugCreationProbability bcp;
	
	public BugCreator(Context<Object> context, final CoreData cd) {
		this.context = context;
		this.cd = cd;
		this.bcp = computeBugCreationProbabilities();
	}

	@ScheduledMethod(start = 100, interval = 1)
	public void createBug() {
		if (RandomHelper.nextDoubleFromTo(.0, 1.0) < bcp.getpNormal()) {
			Bug b = new NormalBugOpen();
			assignBugToModule(b);
		}
		if (RandomHelper.nextDoubleFromTo(.0, 1.0) < bcp.getpCritical()) {
			Bug b = new CriticalBugOpen();
			assignBugToModule(b);
		}
		if (RandomHelper.nextDoubleFromTo(.0, 1.0) < bcp.getpMajor()) {
			Bug b = new MajorBugOpen();
			assignBugToModule(b);			
		}
		if (RandomHelper.nextDoubleFromTo(.0, 1.0) < bcp.getpMinor()) {
			Bug b = new MinorBugOpen();
			assignBugToModule(b);			
		}
	}	
	
	private void assignBugToModule(Bug bug) {
		SEFile file = null;
		List<SEFile> files = Lists.newArrayList();
		
		//TODO: consider file selection based on error prone categories
		files = Lists.newArrayList(context.getObjects(SEFile.class)).stream()
				.map(o -> (SEFile) o)
				.collect(Collectors.toList());
		
		if (files.size() > 0) {

			if (SEContext.ASSIGN_BUGS_TO_HIGH_DEGREE_MODULES && RandomHelper.nextDoubleFromTo(.0, 1.0) < SEContext.P_ASSIGN_BUGS_TO_HIGH_DEGREE_MODULES) {
				Collections.sort(files, new Comparator<SEFile>() 
				{
					public int compare(SEFile a1, SEFile a2) {
						int degA1 = SEContext.changeCoupling.getDegree(a1);
						int degA2 = SEContext.changeCoupling.getDegree(a2);
						return degA1 < degA2 ? 1 : degA1 > degA2 ? -1 : 0;
					}
				});
				
				int max = files.size() -1 < SEContext.NUMBER_OF_HIGH_DEGREE_MODULES_TO_SELECT_FROM ? files.size() -1 : SEContext.NUMBER_OF_HIGH_DEGREE_MODULES_TO_SELECT_FROM;
				int idx = RandomHelper.nextIntFromTo(0, max);
				file = files.get(idx);
			} else {
				int idx = RandomHelper.nextIntFromTo(0, files.size() -1);
				file = files.get(idx);
			}			
			
			context.add(bug);
			SEContext.bugFile.addEdge(bug, file);		
		}
		
	}
	
	
	private BugCreationProbability computeBugCreationProbabilities() {
		double pNormal = cd.getIssueInformationComplete().get(BugPriority.NONE) * 1.0 / cd.getRoundsToSimulate() * 1.0;
		double pCritical = cd.getIssueInformationComplete().get(BugPriority.CRITICAL) * 1.0 / cd.getRoundsToSimulate() * 1.0;;
		double pMajor = cd.getIssueInformationComplete().get(BugPriority.MAJOR) * 1.0 / cd.getRoundsToSimulate() * 1.0;;
		double pMinor = cd.getIssueInformationComplete().get(BugPriority.MINOR) * 1.0 / cd.getRoundsToSimulate() * 1.0;;
		
		return new BugCreationProbability(pNormal, pCritical, pMajor, pMinor);
	}
	
}
