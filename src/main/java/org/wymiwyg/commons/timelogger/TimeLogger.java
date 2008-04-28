package org.wymiwyg.commons.timelogger;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.wymiwyg.commons.util.io.IndentPrintWriter;

/**
 * A time-logger logs the time elapsed between invocations of
 * <code>startSection</code> and <code>endSection</code>.
 */
public class TimeLogger {
	SectionTimeLogger currentTimeSectionLogger;
	private List<SectionTimeLogger> sections = new ArrayList<SectionTimeLogger>();
	private static final List<TimeLogger> activeSectionsPath = new ArrayList<TimeLogger>();

	public TimeLogger startSection(String description) {
		if (currentTimeSectionLogger != null) {
			throw new RuntimeException("Previous section not ended");
		}
		currentTimeSectionLogger = new SectionTimeLogger(description);
		activeSectionsPath.add(currentTimeSectionLogger);
		return currentTimeSectionLogger;
	}

	public void endSection() {
		currentTimeSectionLogger.subLoggerEnd();
		//with multiple indepented time logger its not always the last
		activeSectionsPath.remove(currentTimeSectionLogger);
		sections.add(currentTimeSectionLogger);
		currentTimeSectionLogger = null;
	}

	public int countSetions() {
		return sections.size();
	}

	public void writeReport(PrintWriter writer) {
		for (SectionTimeLogger section : sections) {
			writer.println(section.getTimeElapsed() + "ms - "
					+ section.description);
			if (section.countSetions() > 0) {
				section.writeReport(new IndentPrintWriter(writer));
			}
		}
		writer.flush();
	}

	/**
	 * Returns the timeLogger returned by the last invocation of startSection
	 * for a section that hasn't yet ended
	 * 
	 * Note: there is no special handling of the case when multiple independent
	 * timeloggers are used.
	 * 
	 * @return the TimeLogger returned by the last invocation of startSection or
	 *         null if the section ended or startSection was not invoked by the
	 *         current thread.
	 */
	public static TimeLogger getCurrentSectionTimeLogger() {
		return activeSectionsPath.get(activeSectionsPath.size()-1);
	}
}
