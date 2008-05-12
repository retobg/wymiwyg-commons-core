package org.wymiwyg.commons.timelogger;

import java.io.IOException;
import java.io.PrintWriter;

public class TimeLoggerDemo {

	/**
	 * @param args
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws InterruptedException, IOException {
		TimeLogger timeLogger = new TimeLogger();
		timeLogger.startSection("Just waiting half a sec");
		Thread.sleep(500);
		timeLogger.endSection();	
		TimeLogger subLogger = timeLogger.startSection("Doing various stuff");
		Thread.sleep(50);
		subLogger.startSection("sub task 1");
		Thread.sleep(100);
		subLogger.endSection();
		subLogger.startSection("sub task 2");
		Thread.sleep(100);
		subLogger.endSection();
		for (int i = 0; i < 7; i++) {
			subLogger.startSection("repeated subtask");
			Thread.sleep(50);
			subLogger.endSection();
		}
		
		timeLogger.endSection();
		timeLogger.writeReport(new PrintWriter(System.out));
		System.out.println("Using SummaryReportWriter:");
		timeLogger.setReportWriter(new SummaryReportWriter());
		timeLogger.writeReport(new PrintWriter(System.out));
	}

}
