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
package org.wymiwyg.commons.util.io;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * @author reto
 *
 */
public class IndentWriter extends Writer {

	private Writer base;
	private char[] separatorChars;
	boolean identBeforeNextChar = true;

	/**
	 * @param base
	 */
	public IndentWriter(Writer base) {
		this.base = base;
		String lineSeparator = System.getProperty("line.separator");
		this.separatorChars = lineSeparator.toCharArray();
	}

	/* (non-Javadoc)
	 * @see java.io.Writer#close()
	 */
	public void close() throws IOException {
		base.close();
		
	}

	/* (non-Javadoc)
	 * @see java.io.Writer#flush()
	 */
	public void flush() throws IOException {
		base.flush();
		
	}

	/* (non-Javadoc)
	 * @see java.io.Writer#write(char[], int, int)
	 */
	public void write(char[] cbuf, int off, int len) throws IOException {
		int posInSeparatorChars = 0;
		for (int i = 0; i < len; i++) {
			if (identBeforeNextChar) {
				base.write('\t');
				identBeforeNextChar = false;
			}
			base.write(cbuf[i]);
			if (cbuf[i] == separatorChars[posInSeparatorChars]) {
				posInSeparatorChars++;
				if (posInSeparatorChars == separatorChars.length) {
					identBeforeNextChar = true;
					posInSeparatorChars = 0;
				}
			} else {
				posInSeparatorChars = 0;
			}
		}
		
	}


}
