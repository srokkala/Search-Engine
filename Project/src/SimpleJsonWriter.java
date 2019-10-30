import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Outputs several simple data structures in "pretty" JSON format where newlines
 * are used to separate elements and nested elements are indented.
 *
 * Warning: This class is not thread-safe. If multiple threads access this class
 * concurrently, access must be synchronized externally.
 * 
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2019
 */

public class SimpleJsonWriter {

	/**
	 * Writes the elements as a pretty JSON array.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException
	 */

	public static void asArray(Collection<Integer> elements, Writer writer, int level) throws IOException {
		writer.write("[\n");
		/*
		 * TODO This iterator isn't an integer list. Rename to just iterator? 
		 * (Fix here and everywhere)
		 * 
		 * https://github.com/usf-cs212-fall2019/project-srokkala/blob/d721333ac342839e361d8d827ea908d9d0efeb89/Project/src/SimpleJsonWriter.java#L109
		 */
		Iterator<Integer> integerlist = elements.iterator();

		if (integerlist.hasNext()) {
			indent(writer, level++);
			writer.write(integerlist.next().toString());
		}
		while (integerlist.hasNext()) { // TODO I would expect a blank line above this, but not below it based on your other code.

			writer.write(",\n");
			indent(writer, level + 1);
			writer.write(integerlist.next().toString());
		}

		writer.write("\n");
		indent(writer, level);
		writer.write("]");

	}

	/**
	 * Writes the elements as a pretty JSON array to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException
	 *
	 * @see #asArray(Collection, Writer, int)
	 */

	public static void asArray(Collection<Integer> elements, Path path) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asArray(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON array.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing thments in pretty JSON format
	 *
	 * @see #asArray(Collection, Writer, int)
	 */

	public static String asArray(Collection<Integer> elements) {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try {
			StringWriter writer = new StringWriter();
			asArray(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a pretty JSON object.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException
	 */

	public static void asObject(Map<String, Integer> elements, Writer writer, int level) throws IOException {
		Iterator<String> items = elements.keySet().iterator();
		writer.write("{");

		if (items.hasNext()) { // TODO Need to decide on a strategy for using blank lines and stick to it.

			String line = items.next();
			writer.write("\n");
			quote(line.toString(), writer, level + 1);
			writer.write(": " + elements.get(line));
		}
		while (items.hasNext()) {
			String line = items.next();
			writer.write(",\n");
			quote(line.toString(), writer, level + 1);
			writer.write(": " + elements.get(line));
		}
		writer.write("\n");
		indent("}", writer, level);
	}

	/**
	 * Writes the elements as a pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException
	 *
	 * @see #asObject(Map, Writer, int)
	 */

	public static void asObject(Map<String, Integer> elements, Path path) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asObject(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asObject(Map, Writer, int)
	 */

	public static String asObject(Map<String, Integer> elements) {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try {
			StringWriter writer = new StringWriter();
			asObject(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a nested pretty JSON object. The generic notation used
	 * allows this method to be used for any type of map with any type of nested
	 * collection of integer objects.
	 *
	 * @param treeMap the elements to write
	 * @param writer  the writer to use
	 * @param level   the initial indent level
	 * @throws IOException
	 */

	public static void asNestedObject(Map<String, ? extends Collection<Integer>> treeMap, Writer writer, int level) // TODO Rename treeMap parameter to something more appropriate
			throws IOException {
		Iterator<String> elementlist = treeMap.keySet().iterator();
		writer.write("{\n");

		if (elementlist.hasNext()) {
			String nextelement = elementlist.next();
			indent(writer, level++);
			quote(nextelement, writer);
			writer.write(": ");
			asArray(treeMap.get(nextelement), writer, level++);
			while (elementlist.hasNext()) {
				nextelement = elementlist.next();
				writer.write(",\n");
				indent(writer, level + 1);
				quote(nextelement, writer);
				writer.write(": ");
				asArray(treeMap.get(nextelement), writer, level++);
			}
		}

		writer.write("\n}");

	}

	/**
	 * Writes the elements as a nested pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException
	 */

	public static void asNestedObject(Map<String, ? extends Collection<Integer>> elements, Path path)
			throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asNestedObject(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a nested pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 */

	public static String asNestedObject(Map<String, ? extends Collection<Integer>> elements) {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try {
			StringWriter writer = new StringWriter();
			asNestedObject(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the {@code \t} tab symbol by the number of times specified.
	 *
	 * @param writer the writer to use
	 * @param times  the number of times to write a tab symbol
	 * @throws IOException
	 */

	public static void indent(Writer writer, int times) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		for (int i = 0; i < times; i++) {
			writer.write('\t');
		}
	}

	/**
	 * Indents and then writes the element.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException
	 *
	 * @see #indent(String, Writer, int)
	 * @see #indent(Writer, int)
	 */

	public static void indent(Integer element, Writer writer, int times) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		indent(element.toString(), writer, times);
	}

	/**
	 * Indents and then writes the element.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException
	 *
	 * @see #indent(Writer, int)
	 */

	public static void indent(String element, Writer writer, int times) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		indent(writer, times);
		writer.write(element);
	}

	/**
	 * Writes the element surrounded by {@code " "} quotation marks.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @throws IOException
	 */

	public static void quote(String element, Writer writer) throws IOException { // TODO Why a blank line above?
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}
	
	/*
	 * TODO Do not have a blank line between the Javadoc and the method it is documenting!
	 * Fix here and everywhere
	 */

	/**
	 * Indents and then writes the element surrounded by {@code " "} quotation
	 * marks.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException
	 *
	 * @see #indent(Writer, int)
	 * @see #quote(String, Writer)
	 */

	public static void quote(String element, Writer writer, int times) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		indent(writer, times);
		quote(element, writer);
	}

	/**
	 * Writes the elements as a pretty JSON object to file.
	 *
	 * @param index the elements to write
	 * @param path  the file path to use
	 * @throws IOException
	 *
	 */

	public static void asDoubleNestedObject(TreeMap<String, TreeMap<String, TreeSet<Integer>>> index, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asDoubleNestedObject(index, writer, 0);
		}
	}

	/**
	 * Writes the elements as a nested pretty JSON object to file.
	 * 
	 * @param newindex the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException
	 */

	public static void asDoubleNestedObject(TreeMap<String, TreeMap<String, TreeSet<Integer>>> newindex, Writer writer,
			Integer level) throws IOException {
		var iterator = newindex.keySet().iterator();
		writer.write("{");
		if (iterator.hasNext()) {
			String word = iterator.next();
			writer.write("\n\t");
			quote(word, writer, level);
			writer.write(": ");
			asNestedObject(newindex.get(word), writer, level + 1);
		}
		while (iterator.hasNext()) {
			String word = iterator.next();
			writer.write(",\n\t");
			quote(word, writer, level);
			writer.write(": ");
			asNestedObject(newindex.get(word), writer, level + 1);
		}
		writer.write("\n");
		indent("}", writer, level - 1);

	}
	
	/* TODO
	 
	As suspected (I mentioned it in a previous review), your asArray implementation
	does not work. I suspect any of the implementations using level++ do not work.
	Are you re-running the SimpleJsonWriter unit tests? 
	
	You are going to need to fix these and resubmit unfortunately. Try to get them
	fixed before the Thursday cutoff. 
	
	See: https://github.com/usf-cs212-fall2019/project-srokkala/blob/d721333ac342839e361d8d827ea908d9d0efeb89/Project/src/SimpleJsonWriter.java#L53
	
	public static void main(String[] args) {
		ArrayList<Integer> elements = new ArrayList<>();
		System.out.println(asArray(elements));
		
		elements.add(42);
		System.out.println(asArray(elements));
		
		elements.add(-13);
		System.out.println(asArray(elements));
	}
	*/
}
