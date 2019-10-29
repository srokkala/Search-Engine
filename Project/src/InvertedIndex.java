
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * The invertedIndex class adds an element to the inverted index
 * 
 */

public class InvertedIndex {

	/**
	 * initializing a TreeMap in which we place our elements into
	 */

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
	 * @param element  the element being added to the inverted index
	 * @param file     the file that we add the new elements to
	 * @param position the position it is being added at
	 */

	public void add(String element, String file, int position) {
		index.putIfAbsent(element, new TreeMap<String, ArrayList<Integer>>());
		index.get(element).putIfAbsent(file, new ArrayList<Integer>());
		index.get(element).get(file).add(position);
		counts.putIfAbsent(file, position);
		if (position > counts.get(file)) {
			counts.put(file, position);
		}
	}

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
	 * This function checks if the @param word exist in the index
	 * 
	 * @param word The word that is being checked for
	 * @return returns a boolean true or false if word is found
	 */

	public boolean hasWord(String word) {
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
		return contains(word, path) ? index.get(word).get(path).contains(position) : false;
	}

	/**
	 * Gets an unmodifiable list of positions
	 * 
	 * @param word     word we are checking
	 * @param position position we are checking
	 * @return a unmodifiable set of positions
	 */

	public List<Integer> getPositions(String word, String position) {
		return Collections.unmodifiableList(index.get(word).get(position));
	}

	/**
	 * Gets an unmodifiable set of locations
	 * 
	 * @param word key value for locations
	 * @return an unmodifiable set of locations
	 */
	public Set<String> getLocations(String word) {
		return Collections.unmodifiableSet(index.get(word).keySet());
	}
}
