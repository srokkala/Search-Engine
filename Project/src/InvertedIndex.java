import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.Collection;

/**
 * The invertedIndex class adds an element to the inverted index
 * 
 * @author Steven Rokkala
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
	 * @return boolean if the element was added or not
	 */
	public boolean add(String element, String file, int position) {
		boolean inserted;
		index.putIfAbsent(element, new TreeMap<>());
		index.get(element).putIfAbsent(file, new TreeSet<>());
		inserted = index.get(element).get(file).add(position);
		counts.putIfAbsent(file, position);

		if (position > counts.get(file)) {
			counts.put(file, position);
		}

		return inserted;
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
	 * A helper method called by the two search methods.
	 *
	 * @param outputs An arraylist of outputs we add to in this function
	 * @param search  The map that updates the count
	 * @param word    The word we are searching for
	 */
	private void searchHelper(ArrayList<SearchResult> outputs, Map<String, SearchResult> search, String word) {
		for (String location : this.index.get(word).keySet()) {
			if (!search.containsKey(location)) {
				SearchResult output = new SearchResult(location);
				search.put(location, output);
				outputs.add(output);
			}

			search.get(location).updateCount(word);
		}
	}

	/**
	 * This functions checks for an exact match with input query
	 *
	 * @param queries The queries used to check for a match
	 * @return an arraylist of outputs given the input query
	 */
	public ArrayList<SearchResult> queryMatchChecker(Collection<String> queries) {
		ArrayList<SearchResult> output = new ArrayList<>();
		HashMap<String, SearchResult> search = new HashMap<>();

		for (String queryKey : queries) {
			if (index.containsKey(queryKey)) {
				searchHelper(output, search, queryKey);
			}
		}

		Collections.sort(output);
		return output;
	}

	/**
	 * This functions checks for an partial match with input query
	 *
	 * @param queries The queries used to check for a partial match
	 * @return an arraylist of outputs given the input query
	 */
	public ArrayList<SearchResult> partialQueryMatchChecker(Collection<String> queries) {
		ArrayList<SearchResult> outputs = new ArrayList<>();
		HashMap<String, SearchResult> search = new HashMap<>();

		for (String query : queries) {
			for (String word : this.index.tailMap(query).keySet()) {
				if (word.startsWith(query)) {
					searchHelper(outputs, search, word);
				} else {
					break;
				}
			}
		}

		Collections.sort(outputs);
		return outputs;
	}

	/**
	 * Goes through our local inverted index and adds to it
	 * 
	 * @param local Our local inverted index we use to optimize speed
	 */

	public void addAll(InvertedIndex local) {
		for (String localWord : local.index.keySet()) {
			if (this.index.containsKey(localWord) == false) {
				this.index.put(localWord, local.index.get(localWord));
			} else {
				for (String localLocations : local.index.get(localWord).keySet()) {
					if (this.index.get(localWord).containsKey(localLocations) == false) {
						this.index.get(localWord).put(localLocations, local.index.get(localWord).get(localLocations));
					} else {
						this.index.get(localWord).get(localLocations)
								.addAll(local.index.get(localWord).get(localLocations));
					}
				}
			}
		}
		for (String localLocations : local.counts.keySet()) {
			if (this.counts.containsKey(localLocations)) {
				this.counts.put(localLocations,
						Math.max(this.counts.get(localLocations), local.counts.get(localLocations)));
			} else {
				this.counts.put(localLocations, local.counts.get(localLocations));
			}
		}
	}

	/**
	 * If the @param match is true we are return the output from matcher checker,
	 * otherwise we return output from the partial match checker
	 *
	 * @param queries The queries we pass through the conditional
	 * @param match   A boolean that cause the conditional to point to the right
	 *                function to execute
	 * @return an arraylist of outputs depending on the function called
	 */
	public ArrayList<SearchResult> searchChooser(Collection<String> queries, boolean match) {
		if (match == true) {
			return queryMatchChecker(queries);
		} else {
			return partialQueryMatchChecker(queries);
		}
	}

	/**
	 * returns a Map with filename and word count of each
	 * 
	 * @return Map of unmodifiable counts
	 */
	public Map<String, Integer> getCount() {
		return Collections.unmodifiableMap(counts);
	}

	/**
	 * Getter function for words
	 * 
	 * @return a unmodifiable Set
	 */
	public Set<String> getWords() {
		return Collections.unmodifiableSet(this.index.keySet());
	}

	/**
	 * Gets an unmodifiable set of locations
	 * 
	 * @param element key value for locations
	 * @return an unmodifiable set of locations
	 */
	public Set<String> getLocations(String element) {

		if (contains(element)) {
			return Collections.unmodifiableSet(index.get(element).keySet());
		}

		return Collections.emptySet();

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
	 * Prints the inverted index
	 */
	@Override
	public String toString() {
		return index.toString();
	}

	/**
	 * The class that keeps track of the output of a search
	 * 
	 * @author Steven Rokkala
	 * @author University of San Francisco
	 * @version Fall 2019
	 */
	public class SearchResult implements Comparable<SearchResult> {

		/**
		 * This will hold the location of the search result.
		 */
		private String place;
		/**
		 * This will hold the count of matches.
		 */
		private int number;
		/**
		 * This will hold the score of the search result.
		 */
		private double score;

		/**
		 * Constructor for SearchResult class.
		 * 
		 * @param place Create the SearchResult using this parameter
		 *
		 */
		public SearchResult(String place) {
			this.place = place;
			this.number = 0;
			this.score = 0;
		}

		/**
		 * Getter for the Place variable
		 * 
		 * @return place
		 */

		public String getPlace() {
			return place;
		}

		/**
		 * Getter for the count data member.
		 *
		 * @return the count data member
		 */
		public int getNumber() {
			return this.number;
		}

		/**
		 * Getter for the score data member.
		 *
		 * @return the score
		 */
		public double getTotals() {
			return this.score;
		}

		/**
		 * Updates the count and score associated to a word.
		 *
		 * @param word The word to be updated.
		 */
		public void updateCount(String word) {
			this.number += index.get(word).get(this.place).size();
			this.score = (double) this.number / counts.get(this.place);
		}

		/**
		 * @return A formatted string ready to write.
		 */
		public String placeOfString() {
			return ("\"where\": " + "\"" + this.place + "\",");
		}

		/**
		 * @return A formatted string ready to write.
		 */
		public String countOfString() {
			return ("\"count\": " + this.number + ",");
		}

		/**
		 * @return A formatted string ready to write.
		 */
		public String totalsOfString() {
			return ("\"score\": " + String.format("%.8f", this.score));
		}

		/**
		 * Checks if another output's place is the same as this ones.
		 *
		 * @param otherPlace
		 * @return true if same;
		 */
		public boolean samePlace(SearchResult otherPlace) {
			return this.place.compareTo(otherPlace.place) == 0;
		}

		@Override
		public int compareTo(SearchResult output) {
			double totalDiff = this.score - output.score;

			if (totalDiff != 0) {
				return totalDiff > 0 ? -1 : 1;
			} else {
				int numberDiff = this.number - output.number;

				if (numberDiff != 0) {
					return numberDiff > 0 ? -1 : 1;
				} else {
					return (this.place.toLowerCase().compareTo(output.place.toLowerCase()));
				}
			}
		}
	}

}