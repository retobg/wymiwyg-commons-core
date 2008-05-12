package org.wymiwyg.commons.timelogger;

import java.util.List;

/**
 * @author reto
 * 
 * A TimeLogger for a section, i.e. a timelogger with a duration and
 * an arbitrary number of sub-sections
 *
 */
public class SectionTimeLogger extends TimeLogger implements Section {

	private long start, end;
	String description;
	
	SectionTimeLogger(String description) {
		this.description = description;
		start = System.nanoTime();
	}
	
	void subLoggerEnd() {
		end = System.nanoTime();
	}

	public long getTimeElapsedInMillis() {
		return (end - start) / 1000000;
	}

	public Object getIdentifier() {
		return description;
	}

	public List<Section> subSections() {
		return getSections();
	}

	public long getTimeElapsedInNanos() {
		return end - start;
	}
	
}
