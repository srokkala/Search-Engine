import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.ArrayList;

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
	 * @param element the elements to write
	 * @param writer  the writer to use
	 * @param level   the initial indent level
	 * @throws IOException
	 */
	public static void asArray(Collection<Integer> element, Writer writer, int level) throws IOException {
		Iterator<Integer> iterator = element.iterator();
		writer.write("[");
		if (iterator.hasNext()) {
			writer.write("\n");
			indent(iterator.next().toString(), writer, level + 1);
		}
		while (iterator.hasNext()) {

			writer.write(",\n");
			indent(iterator.next().toString(), writer, level + 1);
		}
		writer.write("\n");
		indent("]", writer, level);
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
	 * @return a {@link String} containing the in pretty JSON format
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

		if (items.hasNext()) {
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
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException
	 */
	public static void asNestedObject(Map<String, ? extends Collection<Integer>> elements, Writer writer, int level)
			throws IOException {
		Iterator<String> iterator = elements.keySet().iterator();
		writer.write("{\n");

		if (iterator.hasNext()) {
			String nextelement = iterator.next();
			indent(writer, level + 1);
			quote(nextelement, writer);
			writer.write(": ");
			asArray(elements.get(nextelement), writer, level + 1);
		}

		while (iterator.hasNext()) {
			String nextelement = iterator.next();
			writer.write(",\n");
			indent(writer, level + 1);
			quote(nextelement, writer);
			writer.write(": ");
			asArray(elements.get(nextelement), writer, level + 1);
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
	public static void quote(String element, Writer writer) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}

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

	/**
	 * Overload Function for asQuery
	 *
	 * @param querySet
	 * @param path
	 * @throws IOException
	 */
	public static void asQuery(Map<String, ArrayList<InvertedIndex.SearchResult>> querySet, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asQuery(querySet, path, writer, 0);
		}
	}

	/**
	 * Helper function that return the map for asQuery
	 * 
	 * @param queryMap
	 * @return Map
	 */
	public static Map<String, ArrayList<InvertedIndex.SearchResult>> asQueryHelper(
			Map<String, ArrayList<InvertedIndex.SearchResult>> queryMap) {
		Map<String, ArrayList<InvertedIndex.SearchResult>> initialMap = new TreeMap<>();

		for (String query : queryMap.keySet()) {
			ArrayList<InvertedIndex.SearchResult> innerTemp = new ArrayList<>();
			innerTemp.addAll(queryMap.get(query));
			initialMap.put(query, innerTemp);
		}

		queryMap = initialMap;
		return queryMap;
	}

	/**
	 * Writes Queries to a File
	 *
	 * @param queryMap Queries will be written to a Map
	 * @param path     The path to write
	 * @param writer   The writer to use.
	 * @param level    the Initial Indent Level
	 * @throws IOException
	 */
	public static void asQuery(Map<String, ArrayList<InvertedIndex.SearchResult>> queryMap, Path path, Writer writer,
			int level) throws IOException {

		writer.write("{\n");

		// Outer Iterator for Outer if- while condition
		var outerIterator = asQueryHelper(queryMap).keySet().iterator();

		if (outerIterator.hasNext()) {
			String nextQuery = outerIterator.next();
			indent(writer, level + 1);

			writer.write("\"" + nextQuery.toString() + "\": [");

			indent(writer, level + 1);

			// Inner Iterator for Inner if-while condition
			var innerIterator = queryMap.get(nextQuery).iterator();

			if (innerIterator.hasNext()) {
				writer.write("\n");
				indent(writer, level + 1);
				writer.write("\t{\n");
				indent(writer, level + 3);
				var nexto = innerIterator.next();
				writer.write(nexto.placeOfString() + "\n");
				indent(writer, level + 3);
				writer.write(nexto.countOfString() + "\n");
				indent(writer, level + 3);
				writer.write(nexto.totalsOfString() + "\n");
				indent(writer, level + 2);
				writer.write("}");
				indent(writer, level);

				while (innerIterator.hasNext()) {
					writer.write(",\n");
					indent(writer, level + 2);
					writer.write("{\n");
					indent(writer, level + 3);
					nexto = innerIterator.next();
					writer.write(nexto.placeOfString() + "\n");
					indent(writer, level + 3);
					writer.write(nexto.countOfString() + "\n");
					indent(writer, level + 3);
					writer.write(nexto.totalsOfString() + "\n");
					indent(writer, level + 2);

					writer.write("}");
					indent(writer, level);
				}
			}
			writer.write("\n");
			indent(writer, level + 1);
			writer.write("]");
			indent(writer, level);

		}

		while (outerIterator.hasNext()) {
			String nextQuery = outerIterator.next();
			writer.write(",\n");
			indent(writer, level + 1);
			writer.write("\"" + nextQuery.toString() + "\": [");
			indent(writer, level + 1);
			var innerIterator = queryMap.get(nextQuery).iterator();

			boolean noNext = true;
			if (innerIterator.hasNext()) {
				writer.write("\n");
				noNext = false;
				indent(writer, level + 1);
				writer.write("\t{\n");
				indent(writer, level + 3);
				var nexto = innerIterator.next();
				writer.write(nexto.placeOfString() + "\n");
				indent(writer, level + 3);
				writer.write(nexto.countOfString() + "\n");
				indent(writer, level + 3);
				writer.write(nexto.totalsOfString() + "\n");
				indent(writer, level + 2);

				writer.write("}");
				indent(writer, level);

				while (innerIterator.hasNext()) {
					writer.write(",\n");
					indent(writer, level + 2);
					writer.write("{\n");
					indent(writer, level + 3);
					nexto = innerIterator.next();
					writer.write(nexto.placeOfString() + "\n");
					indent(writer, level + 3);
					writer.write(nexto.countOfString() + "\n");
					indent(writer, level + 3);
					writer.write(nexto.totalsOfString() + "\n");
					indent(writer, level + 2);

					writer.write("}");
					indent(writer, level);
				}
			}

			if (noNext == false) {
				indent(writer, level + 1);
			}
			writer.write("\n\t]");
			indent(writer, level);
		}
		writer.write("\n}");

	}

}
