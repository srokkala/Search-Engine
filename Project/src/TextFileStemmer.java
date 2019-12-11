import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Utility class for parsing and stemming text and text files into sets of
 * stemmed words.
 *
 * @author Steven Rokkala
 * @author University of San Francisco
 * @version Fall 2019
 *
 * @see TextParser
 */
public class TextFileStemmer {

	/** The default stemmer algorithm used by this class. */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/**
	 * Returns a set of unique (no duplicates) cleaned and stemmed words parsed from
	 * the provided line.
	 *
	 * @param line the line of words to clean, split, and stem
	 * @return a sorted set of unique cleaned and stemmed words
	 *
	 * @see SnowballStemmer
	 * @see #DEFAULT
	 * @see #uniqueStems(String, Stemmer)
	 */
	public static TreeSet<String> uniqueStems(String line) {
		// THIS IS PROVIDED FOR YOU; NO NEED TO MODIFY
		return uniqueStems(line, new SnowballStemmer(DEFAULT));
	}

	/**
	 * Returns a set of unique (no duplicates) cleaned and stemmed words parsed from
	 * the provided line.
	 *
	 * @param line    the line of words to clean, split, and stem
	 * @param stemmer the stemmer to use
	 * @return a sorted set of unique cleaned and stemmed words
	 *
	 * @see Stemmer#stem(CharSequence)
	 * @see TextParser#parse(String)
	 */
	public static TreeSet<String> uniqueStems(String line, Stemmer stemmer) {

		TreeSet<String> l = new TreeSet<String>();
		try {
			String[] hello = TextParser.parse(line);
			for (String ss : hello) {
				l.add(stemmer.stem(ss).toString());
			}

		} catch (Exception e) {
			throw new UnsupportedOperationException("Method not yet implemented.");
		}

		return l;
	}

	/**
	 * Reads a file line by line, parses each line into cleaned and stemmed words,
	 * and then adds those words to a set.
	 *
	 * @param inputFile the input file to parse
	 * @return a sorted set of stems from file
	 * @throws IOException if unable to read or parse file
	 *
	 * @see #uniqueStems(String)
	 * @see TextParser#parse(String)
	 */
	public static TreeSet<String> uniqueStems(Path inputFile) throws IOException {

		if (inputFile != null) {
			URI yeet = inputFile.toUri();
			List<String> lines = Files.readAllLines(Paths.get(yeet));
			return uniqueStems(lines.toString());

		}

		else {
			throw new IOException("Method not yet implemented.");

		}

	}

	/**
	 * A simple main method that demonstrates this class.
	 *
	 * @param args unused
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		String text = "practic practical practice practiced practicer practices "
				+ "practicing practis practisants practise practised practiser "
				+ "practisers practises practising practitioner practitioners";

		System.out.println(uniqueStems(text));

		Path inputPath = Paths.get("test", "animals.text");
		// Path inputPath = Paths.get("test", "rfc475.txt");
		Set<String> actual = TextFileStemmer.uniqueStems(inputPath);
		System.out.println(actual);
	}
}
