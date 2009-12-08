/*
 *  Copyright 2009 reto.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.wymiwyg.commons.util.dirbrowser.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import org.wymiwyg.commons.util.dirbrowser.MultiPathNode;
import org.wymiwyg.commons.util.dirbrowser.PathNameFilter;
import org.wymiwyg.commons.util.dirbrowser.PathNode;

/**
 *
 * @author reto
 */
public class MultiPathNodeTest {

	@Test
	public void commonPath() {
		MultiPathNode node = new MultiPathNode(nodeWithPath("foo/bar/a/b/end"),
				nodeWithPath("hello/foo/bar/a/b/end"),
				nodeWithPath("foo/bar/test/a/b/end"));
		Assert.assertEquals("a/b/end", node.getPath());
	}

	private PathNode nodeWithPath(final String path) {
		return new PathNode() {

			public PathNode getSubPath(String requestPath) {
				throw new UnsupportedOperationException("Not supported yet.");
			}

			public boolean isDirectory() {
				throw new UnsupportedOperationException("Not supported yet.");
			}

			public String[] list(PathNameFilter filter) {
				throw new UnsupportedOperationException("Not supported yet.");
			}

			public String[] list() {
				throw new UnsupportedOperationException("Not supported yet.");
			}

			public InputStream getInputStream() throws IOException {
				throw new UnsupportedOperationException("Not supported yet.");
			}

			public long getLength() {
				throw new UnsupportedOperationException("Not supported yet.");
			}

			public String getPath() {
				return path;
			}

			public Date getLastModified() {
				throw new UnsupportedOperationException("Not supported yet.");
			}

			public boolean exists() {
				throw new UnsupportedOperationException("Not supported yet.");
			}
		};
	}
}
