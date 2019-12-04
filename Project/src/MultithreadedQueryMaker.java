import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * 
 * This is the multithreaded version of the QueryMaker class and all functions
 * are overridden and thread safe
 * 
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2019
 */
public class MultithreadedQueryMaker implements QueryMakerInterface {

	/**
	 * The set that will hold cleaned up queries mapped to their results.
	 */
	private final TreeMap<String, ArrayList<InvertedIndex.SearchResult>> queryMap;

	/**
	 * Number of Threads
	 */
	private final int threads;

	/**
	 * The InvertedIndex we will add to
	 */
	private final MultithreadedInvertedIndex inverted;

	/**
	 * Constructor method for QueryMaker
	 *
	 * @param inverted The inverted index our query search will modify
	 * @param threads  The number of threads we will use
	 */

	public MultithreadedQueryMaker(MultithreadedInvertedIndex inverted, int threads)  { 
		this.inverted = inverted;
		this.threads = threads;
		this.queryMap = new TreeMap<>();
	}

	/**
	 * Overrides function in @class QueryMaker to get the set of queries
	 * 
	 * @return the set of unmodifiable queries
	 */
	@Override
	public Set<String> getQuery() {
		synchronized (queryMap) {
			return Collections.unmodifiableSet(this.queryMap.keySet());
		}
	}

	/**
	 * Overrides function in @class QueryMaker to print the queryMap
	 * 
	 * @return the queryMap
	 */
	@Override
	public String toString() {
		synchronized(queryMap){
			return queryMap.toString();
		}

	}

	/**
	 * Gets a list of outputs based on @param line from the query
	 *
	 * @param line The line we search the query for
	 * @return the list of unmodifiable outputs
	 */
	@Override
	public List<InvertedIndex.SearchResult> getOutput(String line) {
		synchronized (queryMap) {
			if (this.queryMap.get(line) != null) {
				return Collections.unmodifiableList(this.queryMap.get(line));
			} else {
				return Collections.emptyList();
			}
		}
	}

	/**
	 * Calls the Function in SimpleJsonWriter to write the queries
	 * 
	 * @param path
	 * @throws IOException
	 */
	public void queryWriter(Path path) throws IOException {
		synchronized (queryMap) {
			SimpleJsonWriter.asQuery(this.queryMap, path);
		}
	}

	/**
	 * Function that checks if the map is empty.
	 *
	 * @return a boolean if map is empty or not
	 */
	@Override
	public boolean isEmpty() {
		synchronized (queryMap) {
			boolean emptyCheck = this.queryMap.keySet().size() == 0;
			return emptyCheck;
		}

	}

	/**
	 * Parses a Query line made up of words.
	 *
	 * @param line  The line in the query we are parsing through
	 * @param match Return a boolean if we are looking for an exact match or not
	 */
	@Override
	public void queryLineParser(String line, boolean match) {
		TreeSet<String> queries = TextFileStemmer.uniqueStems(line);

		if (queries.isEmpty()) {
			return;
		}

		String joined = String.join(" ", queries);
		synchronized (queryMap) {
			if (queryMap.containsKey(joined)) {
				return;
			}

		}

		ArrayList<InvertedIndex.SearchResult> local = inverted.searchChooser(queries, match);
		synchronized (queryMap) {
			this.queryMap.put(joined, local);
		}

	}

	/**
	 * This function overrides the querymaker function to be thread safe
	 * 
	 * @param path  The path of the query file
	 * @param match Return a boolean if we are looking for an exact match or not
	 */

	@Override
	public void queryParser(Path path, boolean match) throws IOException {
		WorkQueue workQueue = new WorkQueue(this.threads);
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
			String query;
			while ((query = reader.readLine()) != null) {
				workQueue.execute(new ThreadedQueue(query, match));
			}
		}
		try {
			workQueue.finish();
		} catch (Exception e) {
			System.out.println("Error Occured While Trying to Finish");
		}
		workQueue.shutdown();
	}

	/**
	 * 
	 * @author CS 212 Software Development
	 * @author University of San Francisco
	 * @version Fall 2019
	 *
	 */
	private class ThreadedQueue implements Runnable {

		/**
		 * line to put into querylineparser
		 */
		private String line;
		/**
		 * A boolean if it is an exact match or not
		 */
		private boolean match;

		/**
		 * @param line  line to parse @param invertedIndex index to use
		 * @param match
		 */
		public ThreadedQueue(String line, boolean match) {
			this.line = line;
			this.match = match;
		}

		@Override
		public void run() {
			queryLineParser(line, match);
		}
	}

}
