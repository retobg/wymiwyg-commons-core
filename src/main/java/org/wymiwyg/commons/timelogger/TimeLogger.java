package org.wymiwyg.commons.timelogger;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wymiwyg.commons.util.io.IndentPrintWriter;

/**
 * A time-logger logs the time elapsed between invocations of
 * <code>startSection</code> and <code>endSection</code>.
 */
public class TimeLogger {

	final private Logger log = LoggerFactory.getLogger(TimeLogger.class);
	/**
	 * @author reto
	 *
	 */
	private static final class IndentedReportWriter implements ReportWriter {
		/**
		 * @param sections
		 * @param writer
		 */
		private void printSections(Iterable<Section> sections,
				PrintWriter writer) {
			for (Section section : sections) {
				writer.println(section.getTimeElapsedInMillis() + "ms - "
						+ section.getIdentifier());
				printSections(section.subSections(), new IndentPrintWriter(writer));
			}
			writer.flush();
		}

		public void write(Iterable<Section> sections, Writer writer) {
			PrintWriter printWriter = new PrintWriter(writer);
			printSections(sections, printWriter);
			printWriter.flush();
			
		}
	}

	private SectionTimeLogger currentTimeSectionLogger;
	private List<Section> sections = new ArrayList<Section>();
	private static ReportWriterFactory reportWriterFactory = new ReportWriterFactory() {

		public ReportWriter getReportWriter() {
			return new IndentedReportWriter();
		}
		
	};
	private ReportWriter reportWriter = reportWriterFactory.getReportWriter();
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
		log.info("ending section {} after {} ms",
				currentTimeSectionLogger.getIdentifier(),
				currentTimeSectionLogger.getTimeElapsedInMillis());
		currentTimeSectionLogger = null;
	}

	public void writeReport(Writer writer) throws IOException {
		reportWriter.write(sections, writer);
		
		
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

	/**
	 * @return the sections
	 */
	public List<Section> getSections() {
		return sections;
	}

	/**
	 * @return the reportWriter
	 */
	public ReportWriter getReportWriter() {
		return reportWriter;
	}

	/**
	 * @param reportWriter the reportWriter to set
	 */
	public void setReportWriter(ReportWriter reportWriter) {
		this.reportWriter = reportWriter;
	}


	
	
}
