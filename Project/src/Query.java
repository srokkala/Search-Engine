import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class Query implements Comparable<Query>{

	/**
	 * All words in the Query 
	 */
	private TreeSet<String> words;

	/**
	 * Constructor method for the Query class
	 */
	public Query() {
		this.words = new TreeSet<>();
	}
	
	/**
	 * A setter method for the query words
	 * @param words
	 */
	
	public void setWords(TreeSet<String> words) {
		this.words = words;
	}

	/**
	 * A getter method for the query words
	 *
	 * @return An unmodifiable set query words
	 */
	public Set<String> getWords() {
		return Collections.unmodifiableSet(this.words);
	}


	/**
	 * Method to find the size of the query based on word count of TreeSet words
	 *
	 * @return number of words.
	 */
	public int sizeofQuery() {
		return this.words.size();
	}

	/**
	 * Method to add words to the query
	 *
	 * @param word word that is tried to be added
	 * @return boolean if word was added or not
	 */
	public boolean add(String word) {
		return this.words.add(word);
	}


	//Overridden methods from the Comparable Interface
	
	/**
	 * Adds a space before concatenating words
	 */
	@Override
	public String toString() {
		return String.join(" ", this.words);
	}


	/**
	 * compares the result of the toString method to the query
	 */
	@Override
	public int compareTo(Query query) {
		return this.toString().compareTo(query.toString());
	}

}
