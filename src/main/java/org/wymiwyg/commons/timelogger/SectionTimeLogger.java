package org.wymiwyg.commons.timelogger;

import java.util.Date;

public class SectionTimeLogger extends TimeLogger {

	private Date start, end;
	String description;
	
	SectionTimeLogger(String description) {
		this.description = description;
		start = new Date();
	}
	
	void subLoggerEnd() {
		end = new Date();
	}

	long getTimeElapsed() {
		return end.getTime() - start.getTime();
	}
	
}
