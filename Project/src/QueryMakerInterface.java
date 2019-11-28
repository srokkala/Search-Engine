import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

/**
 * Interface used by @class MultithreadedQueryMaker and @class QueryMaker
 * 
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2019
 */
public interface QueryMakerInterface {

	/**
	 * Getter for the queryMap
	 * 
	 * @return the set of unmodifiable queries
	 */
	public Set<String> getQuery();

	/**
	 * Gets a list of outputs based on @param line from the query
	 * 
	 * @param line line The line we search the query for
	 * @return the list of unmodifiable outputs
	 */
	public List<InvertedIndex.SearchResult> getOutput(String line);

	/**
	 * Calls the Function in SimpleJsonWriter to write the queries
	 * 
	 * @param path
	 * @throws IOException
	 */
	public void queryWriter(Path path) throws IOException;

	/**
	 * Method that checks if the map is empty.
	 *
	 * @return return boolean if map is empty or not
	 */
	public boolean isEmpty();

	/**
	 * Parses a Query line made up of words.
	 *
	 * @param line  The line in the query we are parsing through
	 * @param match Return a boolean if we are looking for an exact match or not
	 */
	public void queryLineParser(String line, boolean match);

	/**
	 * This function is called in driver and parses through the query files
	 *
	 * @param path  The path of the query file
	 * @param match Return a boolean if we are looking for an exact match or not
	 * @throws IOException
	 */
	public default void queryParser(Path path, boolean match) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
			String query;
			while ((query = reader.readLine()) != null) {
				queryLineParser(query, match);
			}
		}
	}

}
