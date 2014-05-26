/*
 * Created on Apr 23, 2004
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

package org.wymiwyg.commons.util.arguments;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author reto
 */
public class ArgumentHandler {

	List argumentList = new ArrayList();

	/**
	 * 
	 */
	public ArgumentHandler(String[] arguments) {
		argumentList.addAll(Arrays.asList(arguments)); // ArrayList is
		// guaranteed to support
		// Remove
	}

	public void processArguments(ArgumentProcessor processor)
			throws InvalidArgumentsException {
		processor.process(argumentList);
	}

	/**
	 * This methods uses <code>AnnotatedInterfaceArguments</code> to return an
	 * instance of the specified class with methods returning values from the
	 * arguments of this Handler.
	 * 
	 * Note that the interface must be annotated using <code>CommandLine</code>.
	 * 
	 * @param <I> the type of the interface
	 * @param interfaceClass a class instance for I, i.e. <em>I</em>.class 
	 * @return an object of type I 
	 * @throws InvalidArgumentsException thrown when the argument-list doesn't match the definition in the interface annotations
	 */
	public <I> I getInstance(Class<I> interfaceClass)
			throws InvalidArgumentsException {
		I instance;
		instance = AnnotatedInterfaceArguments
				.getInstance(interfaceClass, this).getValueObject();

		return instance;
	}
    
    /**
     * Returns an instance of A if the arguments could be correctly parsed 
     * and A was not a subclass of ArgumentsWithHelp or no help was requested, null
     * otherwise.
     * @param <A>
     * @param interfaceClass
     * @param args
     * @return 
     */
    public static <A> A readArguments(Class<A> interfaceClass, String[] args) {
        A result = null;
        try {
            final ArgumentHandler argumentHandler = new ArgumentHandler(args);
            result = argumentHandler.getInstance(interfaceClass);
            argumentHandler.processArguments(new ArgumentProcessor() {

                @Override
                public void process(List<String> remaining) throws InvalidArgumentsException {
                    if (remaining.size() > 0) {
                        throw new InvalidArgumentsException("The following arguments could not be understood: " + remaining);
                    }
                }
            });
        } catch (InvalidArgumentsException e) {
            System.out.println(e.getMessage());
            showUsage(interfaceClass);
            result = null;
        }
        if (result instanceof ArgumentsWithHelp) {
            if (((ArgumentsWithHelp)result).getHelp()) {
                showUsage(interfaceClass);
                result = null;
            }
        }
        return result;
    }
    
    
    private static <I> void showUsage(Class<I> interfaceClass) {
        System.out.print("This command has the following arguments: ");
        System.out.println(AnnotatedInterfaceArguments.getArgumentsSyntax(interfaceClass));
        PrintWriter out = new PrintWriter(System.out, true);
        AnnotatedInterfaceArguments.printArgumentDescriptions(
                interfaceClass, out);
        out.flush();
    }
    
}
