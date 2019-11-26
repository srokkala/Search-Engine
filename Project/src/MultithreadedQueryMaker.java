import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

/*
 * TODO Multiple threads aren't really able to run at the same time.
 * But, with this extends relationship, there aren't many options for speeding this up.
 * 
 * The only way to speed up is to re-implement and lock around access to the map.
 * 
 * Create a QueryMakerInterface that has the common methods and a default implementation 
 * of the public void queryParser(Path path, int threads, boolean match) throws IOException
 * 
 * Implelment this interface in QueryMaker and MultithreadedQueryMaker. In the multithreaded
 * version make sure to synchronize ONLY around access to the map and not the search.
 */

/**
 * 
 * This is the multithreaded version of the QueryMaker class and all functions
 * are overridden and thread safe
 * 
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2019
 */
public class MultithreadedQueryMaker extends QueryMaker {

	/**
	 * Creating a variable using the class WorkQueue
	 */
	WorkQueue workQueue;

	/**
	 * Constructor method for QueryMaker
	 *
	 * @param inverted The inverted index our query search will modify
	 * @throws IOException
	 */

	public MultithreadedQueryMaker(MultithreadedInvertedIndex inverted) throws IOException {
		super(inverted);
		new TreeMap<>();
	}

	/**
	 * Overrides function in @class QueryMaker to get the set of queries
	 * 
	 * @return the set of unmodifiable queries
	 */
	@Override
	public Set<String> getQuery() {
		return super.getQuery();
	}

	/**
	 * Overrides function in @class QueryMaker to print the queryMap
	 * 
	 * @return the queryMap
	 */
	@Override
	public String toString() {
		return super.toString();
	}

	/**
	 * Gets a list of outputs based on @param line from the query
	 *
	 * @param line The line we search the query for
	 * @return the list of unmodifiable outputs
	 */
	@Override
	public List<InvertedIndex.SearchResult> getOutput(String line) {
		return super.getOutput(line);
	}

	/**
	 * Function that checks if the map is empty.
	 *
	 * @return a boolean if map is empty or not
	 */
	@Override
	public boolean isEmpty() {
		return super.isEmpty();
	}

	/**
	 * Parses a Query line made up of words.
	 *
	 * @param line  The line in the query we are parsing through
	 * @param match Return a boolean if we are looking for an exact match or not
	 */
	@Override
	public void queryLineParser(String line, boolean match) {
		super.queryLineParser(line, match);
	}

	/**
	 * This function overrides the querymaker function to be thread safe
	 * 
	 * @param path    The path of the query file
	 * @param threads Passing through the number of threads
	 * @param match   Return a boolean if we are looking for an exact match or not
	 */

	@Override
	public void queryParser(Path path, int threads, boolean match) throws IOException {
		this.workQueue = new WorkQueue(threads);
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
			String query;
			while ((query = reader.readLine()) != null) {
				workQueue.execute(new ThreadedQueue(query, match));
			}
		}
		try {
			workQueue.finish();
		} catch (Exception e) {

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
			synchronized (workQueue) {
				queryLineParser(line, match);
			}
		}
	}

}
