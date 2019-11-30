
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * This class build queries using the inverted index passed in through the
 * constructor
 * 
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2019
 */

public class QueryMaker implements QueryMakerInterface {

	/**
	 * The inverted index that the query search will go through
	 */
	private final InvertedIndex inverted;

	/**
	 * A map for all the clean queries
	 */
	private final TreeMap<String, ArrayList<InvertedIndex.SearchResult>> queryMap;

	/**
	 * Constructor method for QueryMaker
	 *
	 * @param inverted The inverted index our query search will modify
	 */
	public QueryMaker(InvertedIndex inverted) {
		this.inverted = inverted;
		this.queryMap = new TreeMap<>();
	}

	/**
	 * 
	 * Getter for the queryMap
	 * 
	 * @return the set of unmodifiable queries
	 */
	public Set<String> getQuery() {
		return Collections.unmodifiableSet(this.queryMap.keySet());
	}

	/**
	 * Gets a list of outputs based on @param line from the query
	 *
	 * @param line The line we search the query for
	 * @return the list of unmodifiable outputs
	 */
	public List<InvertedIndex.SearchResult> getOutput(String line) {
		if (this.queryMap.get(line) == null) {
			return Collections.unmodifiableList(this.queryMap.get(line));
		} else
			return Collections.emptyList();
	}

	/**
	 * Method that checks if the map is empty.
	 *
	 * @return return boolean if map is empty or not
	 */
	public boolean isEmpty() {
		return this.queryMap.keySet().size() == 0;
	}

	/**
	 * Parses a Query line made up of words.
	 *
	 * @param line  The line in the query we are parsing through
	 * @param match Return a boolean if we are looking for an exact match or not
	 */

	public void queryLineParser(String line, boolean match) {
		TreeSet<String> queries = TextFileStemmer.uniqueStems(line);

		if (queries.isEmpty()) {
			return;
		}

		String joined = String.join(" ", queries);

		if (queryMap.containsKey(joined)) {
			return;
		}

		ArrayList<InvertedIndex.SearchResult> local = inverted.searchChooser(queries, match);
		this.queryMap.put(joined, local);
	}

	/**
	 * Calls the Function in SimpleJsonWriter to write the queries
	 * 
	 * @param path
	 * @throws IOException
	 */
	public void queryWriter(Path path) throws IOException {
		SimpleJsonWriter.asQuery(this.queryMap, path);
	}

	/**
	 * Print out the map of queries
	 */
	public String toString() {
		return queryMap.toString();
	}

}
