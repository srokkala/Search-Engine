
import java.io.IOException;
import java.nio.file.Path;
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
	 * @param element  the element being added to the inverted index
	 * @param file  the file that we add the new elements to
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
	 * Creates the file to be outputted
	 * 
	 * @param path
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
	 * @return boolean true or false if word is found
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
	public boolean hasWord(String word, String place) {
		if (this.index.containsKey(word)) {
			return this.index.get(word).containsKey(place);
		}
		return false;
	}

	/*
	 * TODO hasWord works as a method name only for the first method.
	 * The other ones are checking for more than just the word. I suggest
	 * refactoring all of them to just "contains" 
	 */
	
	/**
	 * checks if the map contains the specific word, path and index.
	 * 
	 * @param word word we are checking
	 * @param path path we are checking
	 * @param position position we are checking
	 * @return boolean if word and path at posiStion
	 */
	public boolean hasWord(String word, String path, int position) {
		return hasWord(word, path) ? index.get(word).get(path).contains(position) : false;
	}

	/**
	 * getter for set of positions
	 * 
	 * @param word
	 * @param position
	 * @return an unmodifiable set of Positions
	 */
	public Set<Integer> getPositions(String word, String position) {
		// TODO What happens if get(word) is null?
		return Collections.unmodifiableSet(index.get(word).get(position));
	}
	
	// TODO Also need getLocations(String word) method
	
	// TODO Add a toString method
}
