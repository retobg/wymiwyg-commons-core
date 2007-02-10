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
package org.wymiwyg.commons.util.dirbrowser.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.wymiwyg.commons.util.dirbrowser.PathNode;
import org.wymiwyg.commons.util.dirbrowser.ZipPathNode;

import junit.framework.TestCase;

/**
 * @author reto
 *
 */
public class ZipPathNodeTest extends TestCase {

	public void testList() throws Exception {
		ZipPathNode pathNode = getExample();
		PathNode subPathNode = pathNode.getSubPath("test");
		listRecursively(pathNode);
		assertEquals(3, subPathNode.list().length);	
	}
	
	public void testChildContent() throws Exception {
		ZipPathNode pathNode = getExample();
		PathNode subPathNode = pathNode.getSubPath("test");
		PathNode contentPathNode = subPathNode.getSubPath("foo");
		InputStream in = contentPathNode.getInputStream();
		StringWriter out = new StringWriter();
		for (int ch = in.read(); ch != -1; ch = in.read()) {
			out.write(ch);
		}
		assertEquals("Hello", out.toString());
	}

	/**
	 * @param pathNode
	 */
	private void listRecursively(PathNode pathNode) {
		String[] children = pathNode.list();
		System.out.println("children of "+pathNode);
		for (int i = 0; i < children.length; i++) {
			System.out.println(children[i]);
			PathNode childPathNode = pathNode.getSubPath(children[i]);
			listRecursively(childPathNode);
		}
		
	}

	/**
	 * @return
	 * @throws IOException 
	 */
	private ZipPathNode getExample() throws IOException {
		File zipFile = File.createTempFile("example", ".zip");
		OutputStream outputStream = new FileOutputStream(zipFile);
		ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
		ZipEntry entry = new ZipEntry("test/foo");
		zipOutputStream.putNextEntry(entry);
		zipOutputStream.write("Hello".getBytes());
		ZipEntry entry3 = new ZipEntry("test/foo2");
		zipOutputStream.putNextEntry(entry3);
		zipOutputStream.write("Hello".getBytes());
		ZipEntry entry2 = new ZipEntry("test/subdir/bar");
		zipOutputStream.putNextEntry(entry2);
		zipOutputStream.write("Hello again".getBytes());
		zipOutputStream.close();
		return new ZipPathNode(new ZipFile(zipFile), "");
	}
}
