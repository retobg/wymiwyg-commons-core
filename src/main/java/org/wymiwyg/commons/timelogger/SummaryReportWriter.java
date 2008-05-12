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
package org.wymiwyg.commons.timelogger;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;

/**
 * @author reto
 * 
 */
public class SummaryReportWriter implements ReportWriter {

	/**
	 * @author reto
	 * 
	 */
	public static class PathComparator implements Comparator<List<Object>> {

		public int compare(List<Object> o1, List<Object> o2) {
			if (o1.size() == 0) {
				if (o2.size() == 0) {
					return 0;
				} else {
					return -1;
				}
			} else {
				if (o2.size() == 0) {
					return 1;
				} else {
					int stringComparison = o1.get(0).toString().compareTo(
							o2.get(0).toString());
					if (stringComparison == 0) {	
						return compare(tail(o1), tail(o2));
					} else {
						return stringComparison;
					}
				}
			}
		}

		/**
		 * @param o2
		 * @return
		 */
		private <T >List<T> tail(List<T> list) {
			List<T> result = new ArrayList<T>();
			boolean first = true;
			for (T t : list) {
				if (first) {
					first = false;
					continue;
				}
				result.add(t);
			}
			return result;
		}

	}

	class Summary {
		long totalTimeInNanos = 0;
		int count = 0;
	}

	public void write(Iterable<Section> sections, Writer writer)
			throws IOException {
		Map<List<Object>, Summary> paths = new HashMap<List<Object>, Summary>();
		List<Object> currentPath = new ArrayList<Object>();
		handleSections(paths, currentPath, sections);
		SortedSet<List<Object>> sortedPaths = new TreeSet<List<Object>>(
				new PathComparator());
		sortedPaths.addAll(paths.keySet());
		for (List<Object> key : sortedPaths) {
			printPath(key, writer);
			Summary value = paths.get(key);
			writer.write(": " + (value.totalTimeInNanos / (1000000)) + "/"
					+ value.count);
			writer.write('\n');
		}
		writer.flush();
	}

	/**
	 * @param key
	 * @param writer
	 * @throws IOException
	 */
	private void printPath(List<Object> key, Writer writer) throws IOException {
		boolean first = true;
		for (Object identifier : key) {
			if (!first) {
				writer.write(", ");
			} else {
				first = false;
			}
			writer.write(identifier.toString());
		}

	}

	private void handleSections(Map<List<Object>, Summary> paths,
			List<Object> currentPath, Iterable<Section> sections) {
		for (Section section : sections) {
			handleSection(paths, currentPath, section);
		}

	}

	private void handleSection(Map<List<Object>, Summary> paths,
			List<Object> currentPath, Section section) {
		List<Object> subPath = new ArrayList<Object>(currentPath);
		subPath.add(section.getIdentifier());
		Summary summary;
		if (paths.containsKey(subPath)) {
			summary = paths.get(subPath);
		} else {
			summary = new Summary();
			paths.put(subPath, summary);
		}
		summary.count++;
		summary.totalTimeInNanos += section.getTimeElapsedInNanos();
		handleSections(paths, subPath, section.subSections());
	}

}
