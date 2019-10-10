
import java.io.IOException;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * The invertedIndex class adds an element to the inverted index
 * 
 */
public class InvertedIndex {
	/** initializing a TreeMap in which we place our elements into */
	private final TreeMap<String, TreeMap<String, ArrayList<Integer>>> index;

	/**
	 * New TreeMap in which we will count the words per file
	 */
	private final TreeMap<String, Integer> counts;

	/**
	 * Constructor for Inverted Index initializing the TreeMaps we are using
	 */
	public InvertedIndex() {
		index = new TreeMap<>();
		counts = new TreeMap<>();
	}

	/**
	 * This function will add the parameter element to the inverted index
	 * 
	 * @param element
	 * @param file
	 * @param position
	 */
	public void add(String element, String file, int position) {
		index.putIfAbsent(element, new TreeMap<String, ArrayList<Integer>>());
		index.get(element).putIfAbsent(file, new ArrayList<Integer>());
		index.get(element).get(file).add(position);
		counts.putIfAbsent(file, 0);
		counts.put(file, counts.get(file) + 1);
	}

	/**
	 * Creates the file to be outputted
	 * 
	 * @param outputFile
	 * @throws IOException
	 */

	public void printIndex(String outputFile) throws IOException {

		SimpleJsonWriter.asDoubleNestedObject(index, Path.of(outputFile));

	}

	/**
	 * returns a Map with filename and word count of each
	 * 
	 * @return Map
	 */
	public Map<String, Integer> getCount() {
		return Collections.unmodifiableMap(counts);
	}

}
