/*
 * Created on Mar 8, 2004
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
package org.wymiwyg.commons.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author reto
 */
public class HashStore {
	private static HashStore defaultStore;

	private File directory;

	/**
	 *  
	 */
	public HashStore(File directory) {
		directory.mkdirs();
		this.directory = directory;
	}

	public InputStream getContent(URI uri) throws HashStoreException {
		File resultFile = getStoringFile(uri);
		try {
			return new FileInputStream(resultFile);
		} catch (FileNotFoundException e) {
			throw new ContentUnavailableException("No content for urn: " + uri);
		}
	}

	private File getStoringFile(URI uri) throws UnsupportedURIType {
		String schemeSpecific = uri.getSchemeSpecificPart();
		if (!schemeSpecific.startsWith("hash::md5")) {
			throw new UnsupportedURIType("must start with hash::md5");
		}
		String hash = schemeSpecific.substring(10);
		return new File(directory, hash);
	}

	public URI storeContent(byte[] content) throws IOException {
		MD5 md5 = new MD5();
		byte[] hash = new byte[16];
		md5.update(content);
		md5.md5final(hash);
		String imageContentURNString = "urn:hash::md5:" + MD5.dumpBytes(hash);
		File storingFile;
		URI imageContentURN;
		try {
			imageContentURN = new URI(imageContentURNString);
			storingFile = getStoringFile(imageContentURN);
		} catch (URISyntaxException ex) {
			throw new RuntimeException(ex);
		} catch (UnsupportedURIType ex) {
			throw new RuntimeException(ex);
		}
		FileOutputStream out = new FileOutputStream(storingFile);
		out.write(content);
		out.close();
		return imageContentURN;
	}

	/**
	 * @deprecated use storeContent instead
	 * @param uri
	 * @param content
	 * @throws UnsupportedURIType
	 * @throws IOException
	 */
	public void writeContent(URI uri, byte[] content)
			throws UnsupportedURIType, IOException {
		File storingFile = getStoringFile(uri);
		FileOutputStream out = new FileOutputStream(storingFile);
		out.write(content);
		out.close();
	}

	/**
	 * @param object
	 * @return
	 */
	public long getSize(URI uri) throws HashStoreException {
		File resultFile;
		resultFile = getStoringFile(uri);
		return resultFile.length();
	}

	public static HashStore getDefaultStore() {
		if (defaultStore == null) {
			File home = new File(System.getProperty("user.home"));
			File directory = new File(home, ".hashstore");
			directory.mkdir();
			setDefaultStorePath(directory);
		}
		return defaultStore;
	}

	static void setDefaultStorePath(File directory) {
		defaultStore = new HashStore(directory);
	}
}