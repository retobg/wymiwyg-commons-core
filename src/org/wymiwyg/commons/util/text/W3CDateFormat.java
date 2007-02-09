/*
 * Created on Apr 26, 2004
 * 
 * 
 * ====================================================================
 * 
 * The WYMIWYG Software License, Version 1.0
 * 
 * Copyright (c) 2002-2003 WYMIWYG All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any, must
 * include the following acknowlegement: "This product includes software
 * developed by WYMIWYG." Alternately, this acknowlegement may appear in the
 * software itself, if and wherever such third-party acknowlegements normally
 * appear.
 * 
 * 4. The name "WYMIWYG" or "WYMIWYG.org" must not be used to endorse or promote
 * products derived from this software without prior written permission. For
 * written permission, please contact wymiwyg@wymiwyg.org.
 * 
 * 5. Products derived from this software may not be called "WYMIWYG" nor may
 * "WYMIWYG" appear in their names without prior written permission of WYMIWYG.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL WYMIWYG OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals on
 * behalf of WYMIWYG. For more information on WYMIWYG, please see
 * http://www.WYMIWYG.org/.
 * 
 * This licensed is based on The Apache Software License, Version 1.1, see
 * http://www.apache.org/.
 */
package org.wymiwyg.commons.util.text;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

//TODO support all formats


/** implements http://www.w3.org/TR/NOTE-datetime 
 * 
 *    Year:
      YYYY (eg 1997)
   Year and month:
      YYYY-MM (eg 1997-07)
   Complete date:
      YYYY-MM-DD (eg 1997-07-16)
   Complete date plus hours and minutes:
      YYYY-MM-DDThh:mmTZD (eg 1997-07-16T19:20+01:00)
   Complete date plus hours, minutes and seconds:
      YYYY-MM-DDThh:mm:ssTZD (eg 1997-07-16T19:20:30+01:00)
   Complete date plus hours, minutes, seconds and a decimal fraction of a
second
      YYYY-MM-DDThh:mm:ss.sTZD (eg 1997-07-16T19:20:30.45+01:00)
      
 * @author reto
 */
public class W3CDateFormat extends DateFormat {

	private static final TimeZone utcTZ = new SimpleTimeZone(0, "UTC");

	private static final long serialVersionUID = 3258407344076372025L;

	/**
	 * @see java.text.DateFormat#format(java.util.Date, java.lang.StringBuffer,
	 *      java.text.FieldPosition)
	 */
	public StringBuffer format(Date date, StringBuffer toAppendTo,
			FieldPosition fieldPosition) {

		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ssZ");
		String string = dateFormat.format(date);
		StringBuffer result = new StringBuffer(string);
		result.insert(string.length() - 2, ':');
		return result;
	}

	/**
	 * @see java.text.DateFormat#parse(java.lang.String,
	 *      java.text.ParsePosition)
	 */
	public Date parse(String dateString, ParsePosition parsePos) {

		int position = parsePos.getIndex();

		int y1 = dateString.charAt(position++) - '0';
		int y2 = dateString.charAt(position++) - '0';
		int y3 = dateString.charAt(position++) - '0';
		int y4 = dateString.charAt(position++) - '0';
		int year = 1000 * y1 + 100 * y2 + 10 * y3 + y4;
		position++; // skip '-'
		int m1 = dateString.charAt(position++) - '0';
		int m2 = dateString.charAt(position++) - '0';
		int month = 10 * m1 + m2;
		position++; // skip '-'
		int d1 = dateString.charAt(position++) - '0';
		int d2 = dateString.charAt(position++) - '0';
		int day = 10 * d1 + d2;
		position++; // skip 'T'
		int hour;
		int minutes;
		int secs;
		Calendar resultCalendar;
		if (dateString.length() > position) {
			int h1 = dateString.charAt(position++) - '0';
			int h2 = dateString.charAt(position++) - '0';

			hour = 10 * h1 + h2;
			position++; // skip ':'
			int min1 = dateString.charAt(position++) - '0';
			int min2 = dateString.charAt(position++) - '0';

			minutes = 10 * min1 + min2;
			position++; // skip ':'
			int s1 = dateString.charAt(position++) - '0';
			int s2 = dateString.charAt(position++) - '0';

			secs = 10 * s1 + s2;
//			if there is time there is an explicit time-zone which will deduct or a Z
			
			resultCalendar = new GregorianCalendar(year, month - 1, day,
					hour, minutes, secs);
			resultCalendar.setTimeZone(utcTZ);
		} else {
			resultCalendar = new GregorianCalendar(year, month - 1, day);
		}
		long timeInMillis = resultCalendar.getTimeInMillis();
		if (dateString.length() > position) {
			if (dateString.charAt(position) == '.') {
				position++; // skip '.'
				int ms1 = dateString.charAt(position++) - '0';
				char msc2 = dateString.charAt(position);
				int ms2;
				int ms3;
				if ((msc2 != 'Z') && (msc2 != '-') && (msc2 != '+')) {
					position++;
					ms2 = msc2 - '0';
					char msc3 = dateString.charAt(position);
					if ((msc3 != 'Z') && (msc3 != '-') && (msc3 != '+')) {
						position++;
						ms3 = msc3 - '0';
					} else {
						ms3 = 0;
					}
				} else {
					ms2 = 0;
					ms3 = 0;
				}
				int msecs = 100 * ms1 + 10 * ms2 + ms3;
				timeInMillis += msecs;
			}
			
			char tzd1 = dateString.charAt(position++);
			if (tzd1 != 'Z') {
				int htz1 = dateString.charAt(position++) - '0';
				int htz2 = dateString.charAt(position++) - '0';
				int hourtz = 10 * htz1 + htz2;
				position++; // skip ':'
				int mintz1 = dateString.charAt(position++) - '0';
				int mintz2 = dateString.charAt(position++) - '0';
				int minutestz = 10 * mintz1 + mintz2;
				int offSetInMillis = (hourtz * 60 + minutestz) * 60000;
				if (tzd1 == '+') {
					timeInMillis -= offSetInMillis;
				} else {
					timeInMillis += offSetInMillis;
				}
			}
		}
		parsePos.setIndex(position);
		return new Date(timeInMillis);

	}

	public Date parseOld(String dateString, ParsePosition pos) {
		if (dateString.charAt(dateString.length() - 3) == ':') {
			StringBuffer buffer = new StringBuffer(dateString);
			buffer.deleteCharAt(buffer.length() - 3);
			dateString = buffer.toString();
		}
		if (Character.toUpperCase(dateString.charAt(dateString.length() - 1)) == 'Z') {
			StringBuffer buffer = new StringBuffer(dateString);
			buffer.deleteCharAt(buffer.length() - 1);
			buffer.append("+0000");
			dateString = buffer.toString();
		}
		Date result = null;
		try {
			result = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSZ")
					.parse(dateString, pos);
		} catch (Exception ex) {
		}
		if (result == null) {
			try {
				result = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(
						dateString, pos);
			} catch (Exception ex1) {
			}
		}
		if (result == null) {
			try {
				result = new SimpleDateFormat("yyyy-MM-dd").parse(dateString,
						pos);
			} catch (Exception ex2) {
				System.err
						.println(" hat a DC:date that could not be parsed, using current");
				return new Date();
			}
		}
		return result;
	}
}