
import java.io.IOException;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * The invertedIndex class adds an element to the inverted index
 * 
 */
public class InvertedIndex {
	/** initializing a TreeMap in which we place our elements into */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;

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
	public void add(String word, String filename, int position) {
		index.putIfAbsent(word, new TreeMap<String, TreeSet<Integer>>());
		index.get(word).putIfAbsent(filename, new TreeSet<Integer>());
		index.get(word).get(filename).add(position);
		counts.putIfAbsent(filename, 0);
		counts.put(filename, counts.get(filename) + 1);
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
	 *
	 * @param word
	 * @return returns an ArrayList of indexes where this param word was found
	 */
	public ArrayList<Output> invertedOutput(String word) {
		ArrayList<Output> indexes = new ArrayList<>();
		if (this.index.containsKey(word)) {
			var keys = this.index.get(word).keySet();
			for (String results : keys) {
				Output result = new Output();
				result.setPlace(results);
				result.setNumber(this.index.get(word).get(results).size());
				result.setTotals((double) result.getNumber() / counts.get(results));
				indexes.add(result);
			}
		}
		return indexes;
	}

	/**
	 * Merges Duplicates based on @param ArrayList
	 * 
	 * @param initial
	 * @return an ArrayList of Results.
	 */
	public static ArrayList<Output> mergeDuplicates(ArrayList<Output> initial) {
		ArrayList<Output> merged = new ArrayList<>();
		for (Output result : initial) {
			boolean merge = false;
			for (Output mergedOutput : merged) {
				if (mergedOutput.samePlace(result)) {
					mergedOutput.setNumber(mergedOutput.getNumber() + result.getNumber());
					mergedOutput.setTotals(mergedOutput.getTotals() + result.getTotals());
					merge = true;
				}
			}
			if (!merge) {
				merged.add(result);
			}
		}
		return merged;
	}

	/**
	 * Returns an arrayList of outputs based on the parameter query
	 *
	 * @param query the query we are working on
	 * @return An ArrayList of outputs based on the @param query
	 */
	public ArrayList<Output> getOutput(Query query) {
		ArrayList<Output> output = new ArrayList<>();

		// Get words from query and place them into a string
		for (String words : query.getWords()) {
			ArrayList<Output> array = invertedOutput(words);
			for (Output querys : array) {
				output.add(querys);
			}
		}
		// use helper function to merge duplicates
		output = mergeDuplicates(output);
		Collections.sort(output);
		return output;
	}

	/**
	 * returns a Map with filename and word count of each
	 * 
	 * @return Map
	 */
	public Map<String, Integer> getCount() {
		return Collections.unmodifiableMap(counts);
	}

	/**
	 * returns a unmodifiable Set
	 * 
	 * @return Set
	 */
	public Set<String> getWords() {
		return Collections.unmodifiableSet(this.index.keySet());
	}

	/**
	 * This function checks if the @param word contains the @param place
	 *
	 * @param word
	 * @param place
	 * @return returns a boolean if the word exists
	 */
	public boolean hasLocation(String word, String place) {
		if (this.index.containsKey(word)) {
			return this.index.get(word).containsKey(place);
		}
		return false;
	}

}
