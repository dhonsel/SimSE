package de.ugoe.cs.tcs.se.common;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.common.collect.Lists;

import cern.jet.random.Distributions;
import cern.jet.random.engine.RandomEngine;
import de.ugoe.cs.tcs.se.SEContext;
import de.ugoe.cs.tcs.se.developer.SEDeveloper;
import de.ugoe.cs.tcs.se.graph.SECategory;
import de.ugoe.cs.tcs.se.graph.SEFile;
import de.ugoe.cs.tcs.se.graph.SEMethod;
import de.ugoe.cs.tcs.se.graph.init.DeveloperContribution;
import de.ugoe.cs.tcs.se.graph.init.ImportVertex;
import de.ugoe.cs.tcs.se.refactorings.Misc;
import repast.simphony.context.Context;
import repast.simphony.random.RandomHelper;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.collections.IndexedIterable;

public class Util {
	public static SECategory getRandomCategorie() {
		double r = RandomHelper.nextDoubleFromTo(.0, 1.0);
		double sum = .0;
		for (SECategory c : SEContext.CATEGORIES) {
			sum += c.getP();
			if (sum > r) {
				return c;
			}
		}
		return null;
	}

	public static int numberOfFiles() {
		// return (int)
		// StreamSupport.stream(SEContext.changeCoupling.getNodes().spliterator() ,
		// false).count();

		return SEContext.baseContext().getObjects(SEFile.class).size();
	}

	public static SEDeveloper findDeveloper(Context<Object> context, UUID objectID) {
		return (SEDeveloper) StreamSupport.stream(context.getObjects(SEDeveloper.class).spliterator(), false)
				.filter(x -> ((SEDeveloper) x).getObjectID().equals(objectID)).findFirst().get();
	}

	public static SEFile findFile(Context<Object> context, int id) {
		return (SEFile) StreamSupport.stream(context.getObjects(SEFile.class).spliterator(), false)
				.filter(x -> ((SEFile) x).getId() == id).findFirst().get();
	}

	public static List<DeveloperContribution> computeDeveloperContribution(Context<Object> context, ImportVertex v) {
		List<DeveloperContribution> result = Lists.newArrayList();

		for (int i = 1; i < v.getAttributes().size(); i++) {
			String attr = "dev" + Integer.toString(i);
			if (v.getAttributes().containsKey(attr)) {
				String dc = v.getAttributes().get(attr).getValue();
				UUID developerID = UUID.fromString(dc.split(";")[0]);
				SEDeveloper developer = findDeveloper(context, developerID);
				int touches = Integer.valueOf(dc.split(";")[1]);
				result.add(new DeveloperContribution(developer, touches));
			} else {
				break;
			}
		}

		return result;
	}

	@SuppressWarnings("rawtypes")
	public static int numberOfCommits(Class clazz) {
		int res = 0;
		for (Object d : SEContext.baseContext().getObjects(clazz)) {
			res += ((SEDeveloper) d).getCommitCount();
		}
		return res;
	}

	public static int getYear(Date d) {
		LocalDate ld = getLocalDate(d);
		return ld.getYear();
	}

	public static int getMonth(Date d) {
		LocalDate ld = getLocalDate(d);
		return ld.getMonthValue();
	}

	public static LocalDate getLocalDate(Date date) {
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	public static long computeMonthBetweenDates(Date d1, Date d2) {
		LocalDate ld1 = getLocalDate(d1);
		LocalDate ld2 = getLocalDate(d2);
		return ChronoUnit.MONTHS.between(ld1, ld2) + 1;
	}

	public static long computeDaysBetweenDates(Date d1, Date d2) {
		LocalDate ld1 = getLocalDate(d1);
		LocalDate ld2 = getLocalDate(d2);
		return ChronoUnit.DAYS.between(ld1, ld2) + 1;
	}
	
	public static int randomGeometric(double p) {
		Date date = new Date();
		changeDate(date);
		RandomEngine generator = new cern.jet.random.engine.MersenneTwister(date);
		
		return Distributions.nextGeometric(p, generator);
	}
	
	@SuppressWarnings("deprecation")
	private static void changeDate(Date date) {
		date.setHours(RandomHelper.nextIntFromTo(1, 24));
		date.setMinutes(RandomHelper.nextIntFromTo(1, 60));
		date.setMonth(RandomHelper.nextIntFromTo(1, 12));
		date.setYear(RandomHelper.nextIntFromTo(1900, 2050));
	}	
	
	public static SEMethod randomMethod(SECategory category, SEDeveloper developer) {
		SEFile file = getFileToChange(category, developer);
		return randomMethod(file);
	}
	
	public static SEMethod randomMethod(SEFile file) {
		if (file == null) {
			return null;
		}
		List<SEMethod> methods = Misc.getMethodsOfClass(SEContext.membershipMethod, file.getClazz());
		if (methods.size() > 0) {
			int idx = RandomHelper.nextIntFromTo(0, methods.size() - 1);
			return methods.get(idx);
		}
		return null;
	}	
	
	@SuppressWarnings("unchecked")
	public static SEFile getFileToChange(SECategory category, SEDeveloper developer) {
		// dice the probability for module selection (assumption: uniform distributed)
		double p = RandomHelper.nextDoubleFromTo(0.0, 1.0);
				
		// TODO: do not initialize this data structures for each updated module (better for each commit or each delete and update process once)
		Context<Object> context = ContextUtils.getContext(developer);		
		var ownFilesInCategory = category.getFiles().stream()
				.filter(a -> a.getOwner() == developer)
				.collect(Collectors.toCollection(ArrayList::new));
		var allFiles = Lists.newArrayList(context.getObjects(SEFile.class)).stream()
				.map(a -> (SEFile) a)
				.collect(Collectors.toCollection(ArrayList::new));
		var ownFiles = allFiles.stream()
				.filter(a -> a.getOwner() == developer)
				.collect(Collectors.toCollection(ArrayList::new));
		
		int idx = 0;
		SEFile file = null;
		if (p <= 0.7 && ownFilesInCategory.size() > 1) {
			if (p < 0.65) { // select a module that this developer owns and that is contained in the given category
				idx = RandomHelper.nextIntFromTo(0, ownFilesInCategory.size() - 1);
				file = ownFilesInCategory.get(idx);
			} else { // select a module that this developer owns
				idx = RandomHelper.nextIntFromTo(0, ownFiles.size() - 1);
				file = ownFiles.get(idx);
			}
		} else if (p >= 0.75 && category.getFiles().size() > 1) { // select a module that is contained in the given category - ownership does not matter
			idx = RandomHelper.nextIntFromTo(0, category.getFiles().size() - 1);
			file = category.getFiles().get(idx);
		} else if (allFiles.size() > 1) { // select a random module
			idx = RandomHelper.nextIntFromTo(0, allFiles.size() - 1);
			file = (SEFile) allFiles.get(idx);
		}
		
		return file;	
	}
	
	/**
	 * The method calculates the average label value over all instantiated files.
	 * @return The average label value.
	 */
	public static double computeAverageLabelValue() {
		double value = 0;
		IndexedIterable<Object> artifatcs = SEContext.baseContext().getObjects(SEFile.class);
		for (Object a : artifatcs) {
			value += ((SEFile) a).getLabelValue();
		}
		return value / artifatcs.size();		
	}	
}
