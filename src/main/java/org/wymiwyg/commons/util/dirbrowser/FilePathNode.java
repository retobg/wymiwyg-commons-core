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

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * @author reto
 */
public class FilePathNode implements PathNode {

	private File file;
	/**
	 * @param string
	 */
	public FilePathNode(String path) {
		file = new File(path);
	}

	public FilePathNode(File file) {
		this.file = file;
	}

	/**
	 * @see org.wymiwyg.rwcf.tools.fileserver.PathNode#getSubPath(java.lang.String)
	 */
	public PathNode getSubPath(String requestPath) {
		return new FilePathNode(new File(file, requestPath));
	}

	/**
	 * @see org.wymiwyg.rwcf.tools.fileserver.PathNode#isDirectory()
	 */
	public boolean isDirectory() {
		return file.isDirectory();
	}

	/**
	 * @see org.wymiwyg.rwcf.tools.fileserver.PathNode#list(org.wymiwyg.rwcf.tools.fileserver.PathNameFilter)
	 */
	public String[] list(final PathNameFilter filter) {
		return file.list(new FilenameFilter() {

			public boolean accept(File dir, String name) {
				return filter.accept(FilePathNode.this, name);
			}
		});
	}
	
	/**
	 * @see org.wymiwyg.rwcf.tools.fileserver.PathNode#list()
	 */
	public String[] list() {
		return file.list();
	}

	/**
	 * @see org.wymiwyg.rwcf.tools.fileserver.PathNode#getInputStream()
	 */
	public InputStream getInputStream() throws IOException {
		return new FileInputStream(file);
	}
	
	public long getLength() {
		return file.length();
	}

	/* (non-Javadoc)
	 * @see org.wymiwyg.commons.util.dirbrowser.PathNode#getPath()
	 */
	public String getPath() {
		return file.getPath();
	}

	/* (non-Javadoc)
	 * @see org.wymiwyg.commons.util.dirbrowser.PathNode#getLastModified()
	 */
	public Date getLastModified() {
		// TODO Auto-generated method stub
		return new Date(file.lastModified());
	}

	public boolean exists() {
		return file.exists();
	}

}
