/*
 * Copyright  2002-2006 WYMIWYG (http://wymiwyg.org)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.wymiwyg.commons.util.text.test;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.wymiwyg.commons.util.text.W3CDateFormat;

import junit.framework.TestCase;

/** 
 * @author reto
 * 
 */
public class W3CDateFormatTest extends TestCase {
	
	public void testParse() throws ParseException {
		System.out.println(new Date());
		//using default tz
		Calendar calendar = new GregorianCalendar(2008,10,18);
		assertEquals(calendar.getTime(), new W3CDateFormat().parse("2008-11-18"));
		calendar = new GregorianCalendar(2008,10,18, 23,59,32);
		calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
		assertEquals(calendar.getTime(), new W3CDateFormat().parse("2008-11-18T23.59.32Z"));
		
	}

}

