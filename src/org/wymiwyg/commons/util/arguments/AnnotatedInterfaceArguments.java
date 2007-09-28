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
package org.wymiwyg.commons.util.arguments;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author reto
 * 
 */
public class AnnotatedInterfaceArguments<T> implements ArgumentProcessor {

	private final class MySingleNamedAttributeProcessor extends
			SingleNamedAttributeProcessor {
		private final CommandLine cl;

		private final Method m;

		private final String name;

		private MySingleNamedAttributeProcessor(String name, CommandLine cl,
				Method m) {
			super(name);
			this.cl = cl;
			this.m = m;
			this.name = name;
		}

		@Override
		public void process(PushBackIterator nextArguments)
				throws InvalidArgumentsException {
			Class<?> returnType = m.getReturnType();
			if (returnType.isArray()) {
				List<String> values = new ArrayList<String>();
				if (!nextArguments.hasNext()) {
					throw new InvalidArgumentsException(name
							+ "  must have at least one value");
				}
				values.add((String) nextArguments.next());
				while (nextArguments.hasNext()) {
					String nextValue = (String) nextArguments.next();
					if (nextValue.startsWith("-")) {
						nextArguments.pushBack(nextValue);
						break;
					}
				}
				valueMap.put(m, values.toArray(new String[values.size()]));
			} else {
				if (returnType.equals(Boolean.TYPE)) {
					if (cl.isSwitch()) {
						valueMap.put(m, Boolean.TRUE);
					} else {
						if (!nextArguments.hasNext()) {
							throw new InvalidArgumentsException(name
									+ "  must have value true|yes or false|no");
						}
						String value = (String) nextArguments.next();
						boolean booleanValue;
						if (value.equals("true") || value.equals("yes")) {
							booleanValue = true;
						} else {
							if (value.equals("false") || value.equals("no")) {
								booleanValue = false;
							} else {
								throw new InvalidArgumentsException(
										name
												+ "  must have value true|yes or false|no");
							}
						}
						valueMap.put(m, booleanValue);
					}
				} else {
					// store string value
					if (!nextArguments.hasNext()) {
						throw new InvalidArgumentsException(name
								+ "  must have a value");
					}
					valueMap.put(m, nextArguments.next());
				}
			}
		}
	}

	/**
	 * @author reto
	 * 
	 */
	public class AnnotatedInterfaceProxy implements InvocationHandler {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
		 *      java.lang.reflect.Method, java.lang.Object[])
		 */
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			Object value = valueMap.get(method);
			Class<?> returnType = method.getReturnType();

			if (value == null) {
				CommandLine annotation = method
						.getAnnotation(CommandLine.class);
				if (returnType.equals(Boolean.TYPE)) {
					if (annotation.isSwitch()) {
						return Boolean.FALSE;
					} else {
						return annotation.defaultBooleanValue();
					}
				} else {
					String[] defaultValues = annotation.defaultValue();
					if (returnType.isArray()) {
						return defaultValues;
					} else {
						if (defaultValues.length > 0) {

							return getObjectFromString(defaultValues[0],
									returnType);

						} else {
							return null;
						}
					}
				}
			}
			if (returnType.isArray()) {
				String[] valueStrings = (String[])value;
				Object[] result = (Object[]) Array.newInstance(returnType.getComponentType(), valueStrings.length);
				//Object[] result = new Object[valueStrings.length];
				for (int i = 0; i < valueStrings.length; i++) {
					result[i] = getObjectFromString(valueStrings[i], returnType.getComponentType());
				}
				return result;
			} else {
				if (value.getClass().equals(String.class)) {
					return getObjectFromString((String) value, returnType);
				} else {
					return value;
				}
			}
		}

		/**
		 * @param string
		 * @param returnType
		 * @return
		 * @throws NoSuchMethodException
		 * @throws SecurityException
		 */
		private Object getObjectFromString(String string, Class<?> type)
				throws Exception {
			if (type.isPrimitive()) {
				type = civilize(type);
			}
			Class[] argTypes = new Class[1];
			argTypes[0] = String.class;
			Constructor constructor = type.getConstructor(argTypes);
			Object[] argValues = new Object[1];
			argValues[0] = string;
			Object result =  constructor.newInstance(argValues);
			return result;
		}

		/**
		 * @param returnType
		 * @return
		 */
		private Class<?> civilize(Class<?> primitive) {
			if (primitive.equals(int.class)) {
				return Integer.class;
			}
			throw new RuntimeException("Civilization failed");
		}

	}

	private Class<T> annotated;

	private Map<Method, Object> valueMap = new HashMap<Method, Object>();

	public static <U> AnnotatedInterfaceArguments<U> getInstance(
			Class<U> annotated, ArgumentHandler argumentHandler)
			throws InvalidArgumentsException {
		return new AnnotatedInterfaceArguments<U>(annotated, argumentHandler);
	}

	public static <U> U getValueObject(Class<U> annotated,
			ArgumentHandler argumentHandler) throws InvalidArgumentsException {
		return new AnnotatedInterfaceArguments<U>(annotated, argumentHandler)
				.getValueObject();
	}

	/**
	 * @throws InvalidArgumentsException
	 * 
	 */
	private AnnotatedInterfaceArguments(Class<T> annotated,
			ArgumentHandler argumentHandler) throws InvalidArgumentsException {
		this.annotated = annotated;
		argumentHandler.processArguments(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wymiwyg.commons.util.arguments.ArgumentProcessor#process(java.util.List)
	 */
	public void process(List argumentList) throws InvalidArgumentsException {
		Set<Method> requiredArguments = new HashSet<Method>();
		NamedAttributeProcessor namedAttributeProcessor = new NamedAttributeProcessor();
		for (final Method m : annotated.getMethods()) {
			if (m.isAnnotationPresent(CommandLine.class)) {
				final CommandLine cl = m.getAnnotation(CommandLine.class);
				if (cl.required()) {
					requiredArguments.add(m);
				}
				for (final String longName : cl.longName()) {
					namedAttributeProcessor
							.addLongNameAttributeProcessor(new MySingleNamedAttributeProcessor(
									longName, cl, m));
				}
				for (final String shortName : cl.shortName()) {
					namedAttributeProcessor
							.addNamedAttributeProcessor(new MySingleNamedAttributeProcessor(
									shortName, cl, m));
				}
			}
		}
		Set<CommandLine> missingArguments = new HashSet<CommandLine>();
		namedAttributeProcessor.process(argumentList);
		for (Iterator<Method> iter = requiredArguments.iterator(); iter
				.hasNext();) {
			Method current = iter.next();
			if (!valueMap.containsKey(current)) {
				missingArguments.add(current.getAnnotation(CommandLine.class));
			}
		}
		if (missingArguments.size() > 0) {
			throw new MissingArgumentException(missingArguments);
		}
	}

	@SuppressWarnings("unchecked")
	public T getValueObject() {
		Class[] interfaces = new Class[1];
		interfaces[0] = annotated;
		T result = (T) Proxy.newProxyInstance(this.getClass().getClassLoader(),
				interfaces, new AnnotatedInterfaceProxy());
		return result;
	}

	/**
	 * @param name
	 * @return something like "-model1 string -model2 string
	 *         [-useDefaultOntology {true|false}]"
	 */
	public static String getArgumentsSyntax(Class annotated) {
		StringWriter writer = new StringWriter();
		for (final Method m : annotated.getMethods()) {
			if (m.isAnnotationPresent(CommandLine.class)) {
				try {
					printArgument(m, writer);
				} catch (IOException e) {
					throw new RuntimeException("never happens");
				}
			}
		}
		return writer.toString();
	}

	/**
	 * @param argument
	 * @param messageWriter
	 * @throws IOException
	 */
	private static void printArgument(Method method, Writer writer)
			throws IOException {
		final CommandLine cl = method.getAnnotation(CommandLine.class);
		if (!cl.required()) {
			writer.write('[');
		}
		printArgumentSyntax(cl, method, writer);

		if (!cl.required()) {
			writer.write(']');
		}
		writer.write(' ');
	}

	/**
	 * @param cl
	 * @param method
	 * @param writer
	 * @throws IOException
	 */
	private static void printValueRange(CommandLine cl, Method method,
			Writer writer) throws IOException {
		Class<?> returnType = method.getReturnType();
		if (returnType.equals(Boolean.TYPE)) {
			if (!cl.isSwitch()) {
				writer.write(" {true|false}");
			}
		} else {
			if (returnType.equals(File.class)) {
				writer.write(" filename");
			} else {
				writer.write(" string");
			}
		}
	}

	/**
	 * @param cl
	 * @param writer
	 * @throws IOException
	 */
	private static void printNames(CommandLine cl, Writer writer)
			throws IOException {
		boolean first = true;
		for (int i = 0; i < cl.shortName().length; i++) {
			String name = cl.shortName()[i];
			if (!first) {
				writer.write('|');
			} else {
				first = false;
			}
			writer.write("-");
			writer.write(name);
		}
		for (int i = 0; i < cl.longName().length; i++) {
			String name = cl.longName()[i];
			if (!first) {
				writer.write('|');
			} else {
				first = false;
			}
			writer.write("--");
			writer.write(name);
		}

	}

	/**
	 * @param name
	 * @param writer
	 */
	public static void printArgumentDescriptions(Class annotated,
			PrintWriter writer) {
		int maxLength = 0;
		for (final Method m : annotated.getMethods()) {
			if (m.isAnnotationPresent(CommandLine.class)) {
				int currentLength = getArgumentSyntaxLength(m);
				maxLength = maxLength < currentLength ? currentLength
						: maxLength;
			}
		}

		for (final Method m : annotated.getMethods()) {
			if (m.isAnnotationPresent(CommandLine.class)) {
				try {
					printArgumentDescription(m, writer, maxLength);
				} catch (IOException e) {
					throw new RuntimeException("never happens");
				}
			}
		}

	}

	/**
	 * @param m
	 * @return
	 */
	private static int getArgumentSyntaxLength(Method m) {

		final CommandLine cl = m.getAnnotation(CommandLine.class);
		return getArgumentSyntax(cl, m).length();

	}

	/**
	 * @param cl
	 * @param m
	 * @return
	 */
	private static String getArgumentSyntax(CommandLine cl, Method m) {
		StringWriter w = new StringWriter();
		try {
			printArgumentSyntax(cl, m, w);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return w.toString();
	}

	/**
	 * @param m
	 * @param writer
	 * @param maxLength
	 * @throws IOException
	 */
	private static void printArgumentDescription(Method method,
			PrintWriter writer, int maxLength) throws IOException {
		final CommandLine cl = method.getAnnotation(CommandLine.class);
		writer.print("  ");
		String argumentSyntax = getArgumentSyntax(cl, method);
		writer.print(argumentSyntax);
		for (int i = argumentSyntax.length(); i < (maxLength + 3); i++) {
			writer.print(' ');
		}
		writer.println(cl.description());

	}

	/**
	 * @param cl
	 * @param method
	 * @param writer
	 * @throws IOException
	 */
	private static void printArgumentSyntax(CommandLine cl, Method method,
			Writer writer) throws IOException {
		printNames(cl, writer);
		printValueRange(cl, method, writer);

	}

	/**
	 * @param name
	 */
	public static void showHelp(Class<?> type) {

		System.out.println("Usage: ");
		System.out.print("SmartHello ");
		System.out
				.println(AnnotatedInterfaceArguments.getArgumentsSyntax(type));
		PrintWriter out = new PrintWriter(System.out, true);
		AnnotatedInterfaceArguments.printArgumentDescriptions(type, out);
		out.flush();

	}
}
