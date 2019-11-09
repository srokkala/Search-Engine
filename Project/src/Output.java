/*
 * TODO
 * Since all of the data stored here is based on a specific inverted index, this 
 * works well as a public non-static inner class inside of InvertedIndex.
 * 
 * 
 */


/**
 * The class that keeps track of the output of a search
 * 
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2019
 */
public class Output implements Comparable<Output> { // TODO Refactor name to Result or SearchResult

	/**
	 * This will hold the location of the search result.
	 */
	private String place; // TODO final
	/**
	 * This will hold the count of matches.
	 */
	private int number;
	/**
	 * This will hold the score of the search result.
	 */
	private double totals;

	/**
	 * Constructor for Output class.
	 *
	 */
	public Output() { // TODO public Output(String place)
		this.place = "";
		this.number = 0;
		this.totals = 0;
	}

	/**
	 * Sets the string Place
	 *
	 * @param place
	 */
	public void setPlace(String place) { // TODO Remove
		this.place = place;
	}
	
	/*
	 * TODO Remove all of the set methods... replace with instead...
	 * 
	private update(String word) {
		this.number += index.get(word).get(place).size();
		this.totals = (double) this.number / counts.get(place);
	}
	 */

	/**
	 * Sets the Number variable
	 *
	 * @param number
	 */
	public void setNumber(int number) {
		this.number = number;
	}

	/**
	 * Sets the total variable.
	 * 
	 * @param totals
	 *
	 */
	public void setTotals(double totals) {
		this.totals = totals;
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
		return this.totals;
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
		return ("\"score\": " + String.format("%.8f", this.totals));
	}

	/**
	 * Checks if another output's place is the same as this ones.
	 *
	 * @param otherPlace
	 * @return true if same;
	 */
	public boolean samePlace(Output otherPlace) {
		return this.place.compareTo(otherPlace.place) == 0;
	}

	@Override
	public int compareTo(Output output) {
		double totalDiff = this.totals - output.totals;

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
