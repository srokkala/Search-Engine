import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.nio.charset.StandardCharsets;

/**
 * This class build queries using the inverted index passed in through the
 * constructor
 * 
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2019
 */

public class QueryMaker {

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
	 * This function is called in driver and parses through the query files
	 *
	 * @param path    The path of the query file
	 * @param threads Passing through the number of threads
	 * @param match   Return a boolean if we are looking for an exact match or not
	 * @throws IOException
	 */
	public void queryParser(Path path, int threads, boolean match) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				queryLineParser(line, match);
			}
		}
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

		this.queryMap.put(joined, inverted.searchChooser(queries, match));
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
