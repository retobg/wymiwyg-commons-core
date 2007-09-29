/*
 * Created on Nov 3, 2003
 * 
 * 
 * ====================================================================
 *
 * The WYMIWYG Software License, Version 1.0
 *
 * Copyright (c) 2002-2003 WYMIWYG  
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by WYMIWYG."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The name "WYMIWYG" or "WYMIWYG.org" must not be used to endorse 
 *    or promote products derived from this software without prior written 
 *    permission. For written permission, please contact wymiwyg@wymiwyg.org.
 *
 * 5. Products derived from this software may not be called  
 *    "WYMIWYG" nor may "WYMIWYG" appear in their names 
 *    without prior written permission of WYMIWYG.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL WYMIWYG OR ITS CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,SPECIAL, EXEMPLARY, 
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY 
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR 
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF 
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of WYMIWYG.  For more
 * information on WYMIWYG, please see http://www.WYMIWYG.org/.
 *
 * This licensed is based on The Apache Software License, Version 1.1,
 * see http://www.apache.org/.
 */

package org.wymiwyg.commons.util.dirbrowser;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.JarURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author reto
 */
public class ZipPathNode implements PathNode {

	private static Log logger = LogFactory.getLog(ZipPathNode.class);

	private String entryName;

	private ZipFile file;

	private static Map listCache = Collections.synchronizedMap(new HashMap());

	/**
	 * @param connection
	 */
	public ZipPathNode(JarURLConnection connection) throws IOException {
		entryName = connection.getJarEntry().getName();
		file = connection.getJarFile();
	}

	public ZipPathNode(ZipFile file, String entryName) {
		this.file = file;
		this.entryName = entryName;
	}

	/**
	 * @see org.wymiwyg.commons.util.dirbrowser.PathNode#getSubPath(java.lang.String)
	 */
	public PathNode getSubPath(String requestPath) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(entryName);
		if (!entryName.equals("") && !entryName.endsWith("/") && !(requestPath.startsWith("/"))) {
			buffer.append('/');
		}
		buffer.append(requestPath);
		
		return new ZipPathNode(file, buffer.toString());
	}

	/**
	 * @see org.wymiwyg.commons.util.dirbrowser.PathNode#isDirectory()
	 */
	public boolean isDirectory() {
		ZipEntry entry = file.getEntry(entryName);
		if ((entry != null) && (entry.isDirectory())) {
			return true;
		}
		entry = file.getEntry(entryName + "/");
		return (entry != null) && (entry.isDirectory());
	}

	/**
	 * @see org.wymiwyg.commons.util.dirbrowser.PathNode#list(org.wymiwyg.commons.util.dirbrowser.PathNameFilter)
	 */
	public String[] list(PathNameFilter filter) {
		String[] fullList = list();
		ArrayList resultList = new ArrayList();
		for (int i = 0; i < fullList.length; i++) {
			if (filter.accept(this, fullList[i])) {
				resultList.add(fullList[i]);
			}
		}

		return (String[]) resultList.toArray(new String[resultList.size()]);
	}

	/**
	 * @return
	 */
	public String[] list() {
		Reference currentRef = (Reference) listCache.get(this);
		if (currentRef != null) {
			String[] values = (String[]) currentRef.get();
			if (values != null) {
				return values;
			}
		}
		Enumeration jarEntries = file.entries();
		String name = entryName;
		if (!name.equals("") && name.charAt(name.length()-1) != '/') {
			name = name +"/";
		}
		Set resultList = new HashSet();
		while (jarEntries.hasMoreElements()) {
			ZipEntry current = (ZipEntry) jarEntries.nextElement();
			String currentName = current.getName();
			if (logger.isDebugEnabled()) {
				logger.debug("evaluating: " + current);
			}
			if ((currentName.length() > name.length())
					&& (currentName.startsWith(name))) {
				String subPath = currentName.substring(name.length());
				if (subPath.length() > 0) {
					int slashPos = subPath.indexOf('/');
					if (slashPos == -1) {
						resultList.add(subPath);
					} else {
						resultList.add(subPath.substring(0, slashPos + 1));
					}

				}
			}

		}
		String[] result = (String[]) resultList.toArray(new String[resultList
				.size()]);
		listCache.put(this, new WeakReference(result));
		return result;
	}

	/**
	 * @see org.wymiwyg.commons.util.dirbrowser.PathNode#getInputStream()
	 */
	public InputStream getInputStream() throws IOException {
		return file.getInputStream(file.getEntry(entryName));
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "ZipPathNode for entry " + entryName + " in file "
				+ file.getName();
	}

	public long getLength() {
		return file.getEntry(entryName).getSize();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		return ((other.getClass().equals(getClass()))
				&& ((ZipPathNode) other).file.equals(file) && ((ZipPathNode) other).entryName
				.equals(entryName));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return file.hashCode() | entryName.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wymiwyg.commons.util.dirbrowser.PathNode#getPath()
	 */
	public String getPath() {
		return entryName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wymiwyg.commons.util.dirbrowser.PathNode#getLastModified()
	 */
	public Date getLastModified() {
		try {
			long time = file.getEntry(entryName).getTime();
			if (time > -1) {
				return new Date(time);
			} else {
				return null;
			}
		} catch (NullPointerException ex) {
			return null;
		}
	}
}
