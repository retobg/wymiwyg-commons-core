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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author reto
 * 
 */
public class GreedyPreReadingInputStream extends InputStream {

	/**
	 * @author reto
	 * 
	 */
	public class StreamFinishedException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = -932899244052380362L;

		/**
		 * 
		 */
		public StreamFinishedException() {
			super();
		}

	}

	List byteArrayList = new ArrayList();

	int pos = 0;

	boolean readingFinished = false;

	InputStream currentReader = null;

	/**
	 * 
	 */
	public GreedyPreReadingInputStream(final InputStream base) {
		Thread preReader = new Thread() {

			public void run() {
				byte[] buffer = new byte[255];
				int count;
				try {
					while ((count = base.read(buffer)) != -1) {
						byte[] dataChunk = new byte[count];
						System.arraycopy(buffer, 0, dataChunk, 0, count);
						synchronized (GreedyPreReadingInputStream.this) {
							byteArrayList.add(dataChunk);
							GreedyPreReadingInputStream.this.notify();
						}
						
					}
					synchronized (GreedyPreReadingInputStream.this) {
						readingFinished = true;
						GreedyPreReadingInputStream.this.notify();
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

		};
		preReader.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#read()
	 */
	public int read() throws IOException {
		if (currentReader == null) {
			try {
				getNextReader();
			} catch (StreamFinishedException e) {
				return -1;
			}
		}
		int result = currentReader.read();
		while (result == -1) {
			try {
				getNextReader();
			} catch (StreamFinishedException e) {
				return -1;
			}
			result = currentReader.read();
		}
		return result;
	}

	/**
	 * @return
	 * @throws StreamFinishedException
	 */
	private void getNextReader() throws StreamFinishedException {
		while (pos >= byteArrayList.size()) {
			synchronized (this) {
				if (pos < byteArrayList.size()) {
					//not the same as before synchronized part
					continue;
				}
				if (readingFinished) {
					if (pos < byteArrayList.size()) {
						break;
					}
					throw new StreamFinishedException();
				}
				try {
					wait();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}

		}
		synchronized (this) {
			currentReader = new ByteArrayInputStream((byte[]) byteArrayList
					.get(pos));
			pos++;
		}
		
	}

}
