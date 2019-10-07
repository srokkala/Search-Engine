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
 * Outputs several simple data structures in "pretty" JSON format where
 * newlines are used to separate elements and nested elements are indented.
 *
 * Warning: This class is not thread-safe. If multiple threads access this class
 * concurrently, access must be synchronized externally.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2019
 */
public class SimpleJsonWriter {

	// TODO Need to fix JSON writing so that it is efficient (no countres, no if blocks inside of loops)
	// TODO See related Piazza post on asObject, then take the same approach for all the methods
	// TODO NO STRING CONCATENATION
	// TODO Better variable names (avoid abbreviations except in special circumstances)
	
	// TODO Properly format all the code before code review always (can use the build in formatter in Eclipse)
	
	/**
	 * Writes the elements as a pretty JSON array.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException
	 */
	public static void asArray(Collection<Integer> elements, Writer writer, int level) throws IOException {
		
		
	String elem = "[\n";
	int count = 0;
	
	for(Integer ss : elements)
	{
		if(elements.size() > 1)
		{
			if((elements.size()-1) == count)
			{
				elem += "\t" + ss + "\n";
			}
			else 
			{
				elem += "\t" + ss + ",\n";
			}
		}
		else 
		{
			elem += "\t" + ss + "\n";
		}
		
		count++;
	}
	
	elem += "]";
	writer.write(elem);
	
	
		
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
		}
		catch (IOException e) {
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
		
		Iterator<String> elems = elements.keySet().iterator();
		 writer.write("{");
		 
		 if(elems.hasNext()) {
			 
			 String line = elems.next();
			 writer.write("\n");
			 quote(line.toString(), writer, level+1);
			 writer.write(": " + elements.get(line));
			 }
		 while(elems.hasNext()) {
			 String line = elems.next();
			 writer.write(",\n");
			 quote(line.toString(), writer, level+1);
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
		}
		catch (IOException e) {
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
	public static void asNestedObject(TreeMap<String, ArrayList<Integer>> elements, Writer writer, int level) throws IOException {
		
		Iterator<String> elems = elements.keySet().iterator();
		 writer.write("{");
		 
		 if(elems.hasNext()) {
			 
			 String line = elems.next();
			 writer.write("\n");
			 quote(line.toString(), writer, level+1);
			 writer.write(": [\n");
			 Collection<Integer> nest = elements.get(line); // TODO Call asArray here
			 Iterator<Integer> nests =  nest.iterator();
			 if(nests.hasNext()) {
				 Integer value = nests.next();
				 indent(value.toString(), writer, level+2);
				 
			 }
			 
			while(nests.hasNext()) {
				 Integer value = nests.next();
				 writer.write(",\n");
				 indent(value.toString(), writer, level+2);

			 } 
			writer.write("\n\t\t]");
			 
		 }
		 while(elems.hasNext()) {
			 String line = elems.next();
			 writer.write(",\n");
			 quote(line.toString(), writer, level+1);
			 writer.write(": [");
			
			 Collection<Integer> nest = elements.get(line);
			 Iterator<Integer> nests =  nest.iterator();
			 if(nests.hasNext()) {
				 Integer value = nests.next();
				 writer.write("\n");
				 indent(value.toString(), writer, level+2);
				 
			 }
			 while(nests.hasNext()) {
				 Integer value = nests.next();
				 writer.write(",\n");
				 indent(value.toString(), writer, level+2);
			 }
			 writer.write("\n\t\t]");
		 }
		 
		 writer.write("\n");
		 indent("}", writer, level);
		/*
		 * The generic notation:
		 *
		 *    Map<String, ? extends Collection<Integer>> elements
		 *
		 * May be confusing. You can mentally replace it with:
		 *
		 *    HashMap<String, HashSet<Integer>> elements
		 */
	}

	/**
	 * Writes the elements as a nested pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException
	 *
	 * @see #asNestedObject(Map, Writer, int)
	 */
	public static void asNestedObject(TreeMap<String, ArrayList<Integer>> elements, Path path) throws IOException {
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
	 *
	 * @see #asNestedObject(Map, Writer, int)
	 */
	public static String asNestedObject(TreeMap<String, ArrayList<Integer>> elements) {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try {
			StringWriter writer = new StringWriter();
			asNestedObject(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
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
	 * A simple main method that demonstrates this class.
	 *
	 * @param args unused
	 */
	public static void main(String[] args) {
		// MODIFY AS NECESSARY TO DEBUG YOUR CODE

		TreeSet<Integer> elements = new TreeSet<>();
		
		System.out.println("Empty:");
		System.out.println(asArray(elements));

		elements.add(65);
		System.out.println("\nSingle:");
		System.out.println(asArray(elements));

		elements.add(66);
		elements.add(67);
		System.out.println("\nSimple:");
		System.out.println(asArray(elements));
	}
	
	
	/**
	 * Writes the elements as a pretty JSON object to file.
	 *
	 * @param newindex the elements to write
	 * @param path the file path to use
	 * @throws IOException
	 *
	 */
	public static void asDoubleNestedObject(TreeMap<String, TreeMap<String, ArrayList<Integer>>> newindex, Path path)  throws IOException{
		try(BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)){
			asDoubleNestedObject(newindex, writer, 0);
		}
	}
	
	/**
	 * Writes the elements as a nested pretty JSON object to file.
	 * @param newindex the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException
	 */
	public static void asDoubleNestedObject(TreeMap<String, TreeMap<String, ArrayList<Integer>>> newindex,Writer writer, Integer level) throws IOException {
		var iterator = newindex.keySet().iterator();
		writer.write("{");
		if(iterator.hasNext()) {
			String word = iterator.next();
			writer.write("\n\t");
			quote(word, writer, level);
			writer.write(": ");
			asNestedObject(newindex.get(word),writer,level+1);
		}
		while(iterator.hasNext()) {
			String word = iterator.next();
			writer.write(",\n\t");
			quote(word, writer, level);
			writer.write(": ");
			asNestedObject(newindex.get(word),writer,level+1);
		}
		writer.write("\n");
		indent("}", writer, level-1);
		
	}
}
