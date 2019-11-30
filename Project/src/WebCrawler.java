import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2019
 */
public class WebCrawler  {

	/**
	 * Default stemming algorithm.
	 */
	private static SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/**
	 * The ThreadSafe Inverted Index we will add to
	 */
	private final MultithreadedInvertedIndex invertedIndex;

	/**
	 * Number of Threads
	 */
	private final int numThreads;

	/**
	 * Workqueue added to support multithreading
	 */
	private WorkQueue workQueue;

	/**
	 * The number of maximum follows.
	 */
	private int limit;

	/**
	 * The set of links.
	 */
	private Set<URL> links;

	/**
	 * Constructor Method for the webcrawler class
	 *
	 * @param invertedIndex the index we will add to
	 * @param numThreads number of Threads
	 * @param limit maximum depth
	 */
	public WebCrawler (MultithreadedInvertedIndex invertedIndex, int numThreads, int limit){
		this.invertedIndex = invertedIndex;
		this.numThreads = numThreads;
		this.limit = limit;
		this.links = new HashSet<URL>();
	}

	/**
	 * This function stems the content of the cleaned HTML and adds it to the inverted index.
	 *
	 * @param cleaned the cleaned HTML
	 * @param location the location string
	 * @param index the inverted index we add to
	 * @throws IOException 
	 */
	public static void addStemmed(String cleaned, String location, InvertedIndex index) throws IOException {
		int position = 0;
		try (BufferedReader reader = new BufferedReader(new StringReader(cleaned));) {
			String line = null;
			SnowballStemmer stemmer = new SnowballStemmer(DEFAULT);
			while ((line = reader.readLine()) != null) {
				for (String word : TextParser.parse(line)) {
					position++;
					index.add(stemmer.stem(word).toString(),location , position);
				}
			}
		}
	}


	/**
	 * This function traverses through seeds and adds to the links Set
	 * @param seed the seed url
	 * @throws IOException
	 */
	public void build(URL seed) throws IOException {
		workQueue = new WorkQueue(numThreads);
		links.add(seed);
		workQueue.execute(new ThreadSafe(seed));
		try {
			workQueue.finish();
		} catch (Exception e) {
			System.out.println("The work queue encountered an error.");
		}
		workQueue.shutdown();
	}

	/**
	 * This inner class helps with thread safety and is called in the build function 
	 *
	 * @author CS 212 Software Development
	 * @author University of San Francisco
	 * @version Fall 2019
	 */
	private class ThreadSafe implements Runnable {

		/**
		 * A given URL
		 */
		private final URL url;

		/**
		 * Constructor methods for ThreadSafe
		 * @param url the url we will work on
		 */
		public ThreadSafe(URL url) {
			this.url = url;
		}

		/**
		 * Overrides the function in the Runnable class
		 */
		@Override
		public void run() {
			HtmlCleaner htmlCleaner = new HtmlCleaner(this.url, HtmlFetcher.fetch(url, 3));
			try {
				if (HtmlFetcher.fetch(url, 3) == null) {
					return;
				}
				
				InvertedIndex local = new InvertedIndex();		//Using a local inverted index 
				addStemmed(htmlCleaner.getHtml(), url.toString(), local);   
				invertedIndex.addAll(local);

				//Ensuring Thread Safe work
				synchronized(links) {
					for (URL url : htmlCleaner.getUrls()) {
						if (links.size() < limit && links.add(url)) {
							workQueue.execute(new ThreadSafe(url));
						} else if (links.size() == limit) {
							break;
						}
					}
				}


			} catch (Exception e){
				System.out.println("Adding the cleaned HTML to the Inverted Index has failed!");
			}
		}
	}

}

