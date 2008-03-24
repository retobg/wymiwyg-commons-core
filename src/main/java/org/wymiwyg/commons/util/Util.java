/*
 * Created on 5-mar-03
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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.wymiwyg.commons.util.text.W3CDateFormat;

/**
 * @author reto
 */
public class Util {

	/**
	 * @param resource
	 * @param model
	 */
	/*
	 * public static void adaptRating( Resource resource, float ratingFactor,
	 * int defaultRating) throws RDFException { int rating; try { Statement
	 * ratingStmt = resource.getProperty(MIES.rating); int currentRating =
	 * ratingStmt.getInt(); resource.getModel().remove(ratingStmt); rating =
	 * (int) (currentRating * ratingFactor); } catch (RDFException e) { //get
	 * defaults rating = defaultRating; } resource.getModel().add(resource,
	 * MIES.rating, rating); }
	 */
	private static char createRandomChar() {
		return (char) ('a' + (Math.random() * ('z' - 'a')));
	}

	public static String createRandomString(int length) {
		char[] chars = new char[length];
		for (int i = 0; i < length; i++) {
			chars[i] = createRandomChar();
		}
		return new String(chars);
	}

	public static String createURN5() {
		StringBuffer result = new StringBuffer("urn:urn-5:");
		result.append(replaceSlashWithHyphen(new String(Base64
				.encode(createRandomBytes(20)))));
		return result.toString();
	}

	/**
	 * replaces slashes with hyphens and removes padding=
	 * 
	 * @param origin
	 * @return
	 */
	public static String replaceSlashWithHyphen(String origin) {
		char[] resulltChars = origin.toCharArray();
		for (int i = 0; i < resulltChars.length - 1; i++) {
			if (resulltChars[i] == '/') {
				resulltChars[i] = '-';
			}
		}
		return new String(resulltChars, 0, resulltChars.length - 1);
	}

	public static byte[] createRandomBytes(int length) {
		byte[] result = new byte[length];
		for (int i = 0; i < length; i++) {
			result[i] = createRandomByte();
		}
		return result;
	}

	/**
	 * @param resource
	 * @param string
	 */
	/*
	 * public static void addOrigin(Resource resource, String serverBaseURI)
	 * throws RDFException { Resource annotationSource =
	 * createAnnotationSource(resource, serverBaseURI); StmtIterator
	 * currentOrigins = resource.listProperties(MIES.origin); if
	 * (!currentOrigins.hasNext()) { resource.addProperty(MIES.origin,
	 * annotationSource); } }
	 */

	/**
	 * get a resource from model with the same origin as resource
	 * 
	 * @param resource
	 * @param model
	 * @return
	 */
	/*
	 * public static Resource getExistingResource( Resource resource, Model
	 * model) { try { Resource origin = (Resource)
	 * (resource.getProperty(MIES.origin).getObject()); Resource[] originInModel =
	 * getEquivalentResources(origin, model); if (originInModel == null) {
	 * logger.debug("No such origin"); return null; } Resource result = null;
	 * for (int i = 0; i < originInModel.length; i++) { StmtIterator resultIter =
	 * model.listStatements( new SelectorImpl(null, MIES.origin,
	 * originInModel[i])); if (resultIter.hasNext()) { result =
	 * resultIter.next().getSubject(); break; } } return result; } catch
	 * (RDFException ex) { return null; } }
	 */

	/**
	 * @return
	 */
	private static byte createRandomByte() {
		return (byte) (Math.random() * 255);
	}

	/**
	 * @param date
	 * @return
	 */
	public static String formatDate(Date date) {
		// return a date compliant with http://www.w3.org/TR/NOTE-datetime
		return new W3CDateFormat().format(date);
	}

	/**
	 * @param password
	 * @return
	 */
	public static String sha1(String s) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("sha1 not supported by plattform");
		}
		try {
			byte[] result = md.digest(s.getBytes("UTF-8"));
			return bytes2HexString(result);
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException("utf-8 not supported by plattform");
		}

	}

	/**
	 * Converts an array of bytes to a string of two digits hex-representations
	 * 
	 * @param bytes
	 * @return
	 */
	public static String bytes2HexString(byte[] bytes) {
		StringBuffer resultBuffer = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			resultBuffer.append(byte2Hex(bytes[i]));
		}
		return resultBuffer.toString();
	}

	/**
	 * @param b
	 * @return
	 */
	private static String byte2Hex(byte b) {
		int i = unsignedByteToInt(b);
		String hexChars = "0123456789abcdef";
		byte low = (byte) (i % 16);
		byte high = (byte) (i / 16);
		return "" + hexChars.charAt(high) + hexChars.charAt(low);
	}

	private static int unsignedByteToInt(byte b) {
		return (int) b & 0xFF;
	}

}
