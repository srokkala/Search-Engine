import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.ArrayList;

/**
 * The invertedIndex class adds an element to the inverted index
 * 
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2019
 */
public class InvertedIndex {

	/**
	 * initializing a TreeMap in which we place our elements into
	 */
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
	 * @param element  the element being added to the inverted index
	 * @param file     the file that we add the new elements to
	 * @param position the position it is being added at
	 */
	public void add(String element, String file, int position) {
		index.putIfAbsent(element, new TreeMap<String, TreeSet<Integer>>());
		index.get(element).putIfAbsent(file, new TreeSet<Integer>());
		index.get(element).get(file).add(position);
		counts.putIfAbsent(file, position);
		if (position > counts.get(file)) {
			counts.put(file, position);
		}
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
			for (Output mergedOutput : merged) { // TODO Linear search
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
	public ArrayList<Output> getOutput(String query) {
		ArrayList<Output> output = new ArrayList<>();

		// Get words from query and place them into a string
		for (String words : query.split(" ")) {
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
	
	/* TODO
	public ArrayList<Output> search(Collection<String> queries, boolean exact) {
		return exact ? exactSearch(queries) : partialSearch(queries);
	}
	
	public ArrayList<Output> exactSearch(Collection<String> queries) {
		
	}
	
	public ArrayList<Output> partialSearch(Collection<String> queries) {
		ArrayList<Output> results = 
		Map<String (place), Output> lookup = 
		
		for each query in queries:
			for each word in inverted index keyset
				if the word starts with our query
					for each location of that word in our index
						if (lookup.containsKey(location) {
							lookup.get(location).update(word)
						}
						else {
							Output result = new Output(location);
							result.update(word);
							
							results.add(result);
							lookup.put(location, result);
						}
		
		Collections.sort(results);
		return results;
	}
	*/

	/**
	 * Creates the file to be outputted
	 * 
	 * @param path path we are creating file to
	 * @throws IOException
	 */
	public void printIndex(Path path) throws IOException {

		SimpleJsonWriter.asDoubleNestedObject(index, Path.of(path.toUri()));

	}

	/**
	 * This function checks if the @param word exist in the index
	 * 
	 * @param word The word that is being checked for
	 * @return returns a boolean true or false if word is found
	 */
	public boolean contains(String word) {
		return index.containsKey(word);

	}

	/**
	 * This function checks if the @param word contains the @param place
	 *
	 * @param word  The word that is being checked for
	 * @param place The place we are checking for
	 * @return returns a boolean if the word exists
	 */
	public boolean contains(String word, String place) {
		if (this.index.containsKey(word)) {
			return this.index.get(word).containsKey(place);
		}
		return false;
	}

	/**
	 * checks if the map contains the specific word, path and index.
	 * 
	 * @param word     word we are checking
	 * @param path     path we are checking
	 * @param position position we are checking
	 * @return boolean if word and path at posiStion
	 */
	public boolean contains(String word, String path, int position) {
		return contains(word, path) && index.get(word).get(path).contains(position);

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
	 * Gets an unmodifiable set of positions
	 * 
	 * @param word     word we are checking
	 * @param position position we are checking
	 * @return a unmodifiable set of positions
	 */
	public Set<Integer> getPositions(String word, String position) {
		if (contains(word, position)) {
			return Collections.unmodifiableSet(index.get(word).get(position));
		}
		return Collections.emptySet();

	}

	/**
	 * Gets an unmodifiable set of locations
	 * 
	 * @param word key value for locations
	 * @return an unmodifiable set of locations
	 */
	public Set<String> getLocations(String word) {
		if (contains(word)) {
			return Collections.unmodifiableSet(index.get(word).keySet());
		}
		return Collections.emptySet();

	}

	/**
	 * Prints the inverted index
	 */
	@Override
	public String toString() {
		return index.toString();
	}
}
