import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * This is the multithreaded version of the invertedindex class All functions
 * are overridden and thread safe
 * 
 * @author Steven Rokkala
 * @author University of San Francisco
 * @version Fall 2019
 */
public class MultithreadedInvertedIndex extends InvertedIndex {

	/**
	 * Creating an object from the class SimpleReadWriteLock
	 */
	private final SimpleReadWriteLock lock;

	/**
	 * Constructor
	 */
	public MultithreadedInvertedIndex() {
		super();
		lock = new SimpleReadWriteLock();
	}

	/**
	 * 
	 * Overrides the function in @class InvertedIndex This function will add the
	 * parameter element to the inverted index
	 * 
	 * @param element  the element being added to the inverted index
	 * @param file     the file that we add the new elements to
	 * @param position the position it is being added at
	 * @return boolean if the element was added or not
	 */
	@Override
	public boolean add(String element, String file, int position) {
		lock.writeLock().lock();
		try {
			return super.add(element, file, position);
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * Overrides the function in @class InvertedIndex Creates the file to be
	 * outputted
	 * 
	 * @param path path we are creating file to
	 * @throws IOException
	 */
	@Override
	public void printIndex(Path path) throws IOException {
		lock.readLock().lock();
		try {
			super.printIndex(path);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Overrides the function in @class InvertedIndex This function checks if
	 * the @param word exist in the index
	 * 
	 * @param word The word that is being checked for
	 * @return returns a boolean true or false if word is found
	 */
	@Override
	public boolean contains(String word) {
		lock.readLock().lock();
		try {
			return super.contains(word);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Overrides the function in @class InvertedIndex This function checks if
	 * the @param word contains the @param place
	 *
	 * @param word  The word that is being checked for
	 * @param place The place we are checking for
	 * @return returns a boolean if the word exists
	 */
	@Override
	public boolean contains(String word, String place) {
		lock.readLock().lock();
		try {
			return super.contains(word, place);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Overrides the function in @class InvertedIndex checks if the map contains the
	 * specific word, path and index.
	 * 
	 * @param word     word we are checking
	 * @param path     path we are checking
	 * @param position position we are checking
	 * @return boolean if word and path at posiStion
	 */
	@Override
	public boolean contains(String word, String path, int position) {
		lock.readLock().lock();
		try {
			return super.contains(word, path, position);
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * Overrides the function in @class InvertedIndex This functions checks for an
	 * exact match with input query
	 *
	 * @param queries The queries used to check for a match
	 * @return an arraylist of outputs given the input query
	 */
	@Override
	public ArrayList<SearchResult> queryMatchChecker(Collection<String> queries) {
		lock.readLock().lock();
		try {
			return super.queryMatchChecker(queries);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Overrides the function in @class InvertedIndex This functions checks for an
	 * partial match with input query
	 *
	 * @param queries The queries used to check for a partial match
	 * @return an arraylist of outputs given the input query
	 */
	@Override
	public ArrayList<SearchResult> partialQueryMatchChecker(Collection<String> queries) {
		lock.readLock().lock();
		try {
			return super.partialQueryMatchChecker(queries);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Overrides the function in @class InvertedIndex 
	 * 
	 * @param local a local Inverted Index we create for speed
	 */
	@Override
	public void addAll(InvertedIndex local) {
		lock.writeLock().lock();
		try {
			super.addAll(local);
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * Overrides the function in @class InvertedIndex returns a Map with filename
	 * and word count of each
	 * 
	 * @return Map of unmodifiable counts
	 */
	@Override
	public Map<String, Integer> getCount() {
		lock.readLock().lock();
		try {
			return super.getCount();
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Overrides the function in @class InvertedIndex Getter function for words
	 * 
	 * @return a unmodifiable Set
	 */
	@Override
	public Set<String> getWords() {
		lock.readLock().lock();
		try {
			return super.getWords();
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Override the function in @class InvertedIndex Getter function for position of
	 * a word
	 * 
	 * @param word     word we are checking
	 * @param position position we are checking
	 * @return a unmodifiable set of positions
	 */
	@Override
	public Set<Integer> getPositions(String word, String position) {
		lock.readLock().lock();
		try {
			return super.getPositions(word, position);
		} finally {
			lock.readLock().unlock();
		}

	}

	/**
	 * Overrides the function in @class InvertedIndex
	 * 
	 * Prints the inverted index
	 */
	@Override
	public String toString() {
		lock.readLock().lock();
		try {
			return super.toString();
		} finally {
			lock.readLock().unlock();
		}
	}
}
