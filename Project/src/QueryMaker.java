import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.nio.charset.StandardCharsets;

/**
 * 
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
	private TreeMap<String, ArrayList<Output>> queryMap;

	/**
	 * The path to the file containing the queries printed out in the console
	 */
	private final Path queryPath;

	/**
	 * Constructor method for QueryMaker
	 *
	 * @param inverted  The inverted index our query search will modify
	 * @param queryPath The path
	 * @throws IOException
	 */
	public QueryMaker(InvertedIndex inverted, Path queryPath) throws IOException {
		this.inverted = inverted;
		this.queryMap = new TreeMap<>();
		this.queryPath = queryPath;
	}

	/**
	 * Getter for the queryMap
	 *
	 * @return the map
	 */
	public Map<String, ArrayList<Output>> getQuery() {
		return Collections.unmodifiableMap(this.queryMap);
	}

	/**
	 * Method that checks if the map is empty.
	 *
	 * @return return boolean if map if empty or not
	 */
	public boolean isEmpty() {
		return this.queryMap.keySet().size() == 0;
	}

	/**
	 * This method searches queries for an exact match
	 */
	public void exactSearch() {
		for (String query : this.queryMap.keySet()) {
			this.queryMap.put(query, this.inverted.getOutput(query));
		}
	}

	/**
	 * This method searches through queries based on startswith or if String index
	 * matches String queries, merges duplicates,and places them into the queryMap
	 */
	public void querySearch() {
		for (String query : this.queryMap.keySet()) {
			ArrayList<Output> output = new ArrayList<>();
			for (String queryies : query.split(" ")) {
				for (String indexes : inverted.getWords()) {
					if (indexes.startsWith(queryies) || indexes.equals(queryies)) {
						output.addAll(this.inverted.invertedOutput(indexes));
					}
				}
			}
			output = InvertedIndex.mergeDuplicates(output);
			Collections.sort(output);
			this.queryMap.put(query, output);
		}

	}

	/**
	 * This method opens the query file, cleans and stems the queries, and puts the
	 * result into a Map
	 * 
	 * @throws IOException
	 */
	public void queryGenerator() throws IOException {
		String query;
		try (BufferedReader reader = Files.newBufferedReader(this.queryPath, StandardCharsets.UTF_8);) {
			while ((query = reader.readLine()) != null) {
				TreeSet<String> queries = TextFileStemmer.uniqueStems(query);
				String append = String.join(" ", queries);
				if (queries.size() != 0 && !this.queryMap.containsKey(append)) {
					this.queryMap.put(append, null);
				}
			}
		}
	}

}
