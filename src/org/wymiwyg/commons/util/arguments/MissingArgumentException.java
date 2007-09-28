/*
 * Copyright  2002-2005 WYMIWYG (http://wymiwyg.org)
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
package org.wymiwyg.commons.util.arguments;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Set;


/**
 * @author reto
 *
 */
public class MissingArgumentException extends InvalidArgumentsException {


	/**
	 * 
	 */
	private static final long serialVersionUID = -2086386201152724340L;

	/**
	 * @param missingArguments
	 */
	public MissingArgumentException(Set<CommandLine> missingArguments) {	
		super(getMessageFromMissing(missingArguments));
	}

	/**
	 * @param missingArguments
	 * @return
	 */
	private static String getMessageFromMissing(Set<CommandLine> missingArguments) {
		StringWriter messageWriter = new StringWriter();
		messageWriter.write("Missing required argument");
		if (missingArguments.size() > 1) {
			messageWriter.write('s');
		}
		messageWriter.write(": ");
		boolean first = true;
		for (Iterator<CommandLine> iter = missingArguments.iterator(); iter.hasNext();) {
			CommandLine current = iter.next();
			if (!first) {
				messageWriter.write(", ");
			} else {
				first = false;
			}
			try {
				printArgument(current, messageWriter);
			} catch (IOException e) {
				throw new RuntimeException("never happens");
			}	
		}
		return messageWriter.toString();
	}

	/**
	 * @param argument
	 * @param messageWriter
	 * @throws IOException 
	 */
	private static void printArgument(CommandLine argument, Writer messageWriter) throws IOException {
		boolean first = true;
		for (int i = 0; i < argument.shortName().length; i++) {
			String name = argument.shortName()[i];
			if (!first) {
				messageWriter.write('|');
			} else {
				first = false;
			}
			messageWriter.write("-");
			messageWriter.write(name);
		}
		for (int i = 0; i < argument.longName().length; i++) {
			String name = argument.longName()[i];
			if (!first) {
				messageWriter.write('|');
			} else {
				first = false;
			}
			messageWriter.write("--");
			messageWriter.write(name);
		}
		
	}

}
