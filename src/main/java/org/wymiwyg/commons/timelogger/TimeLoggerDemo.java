package org.wymiwyg.commons.timelogger;

import java.io.PrintWriter;

public class TimeLoggerDemo {

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
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
		timeLogger.endSection();
		timeLogger.writeReport(new PrintWriter(System.out));

	}

}
