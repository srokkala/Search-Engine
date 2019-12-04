import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * This class helps create a new inverted index with a given path
 * 
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2019
 */
public class InvertedBuilder {

	/**
	 * The InvertedIndex we will fill in
	 */
	private final InvertedIndex invertedIndex;

	/**
	 * The default stemmer algorithm used by this class
	 */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/**
	 * @param invertedIndex The inverted index we will add to
	 */
	public InvertedBuilder(InvertedIndex invertedIndex) {
		this.invertedIndex = invertedIndex;
	}

	/**
	 * This method creates paths during directory traversal
	 * 
	 * @param path The initial traversal point
	 * @throws IOException
	 */
	public void build(Path path) throws IOException {
		for (Path currentPath : getTextFiles(path)) {
				addPath(currentPath);
			}
		}

	/**
	 * This method gets a list of all subfiles from a file
	 * 
	 * @param path The initial traversal point
	 * @return The list of subfiles
	 * @throws IOException
	 */
	public static List<Path> getTextFiles(Path path) throws IOException {
		List<Path> list = Files.walk(path, FileVisitOption.FOLLOW_LINKS).filter(InvertedBuilder::isText).collect(Collectors.toList());
		return list;
	}

	/**
	 * This method checks if the path is a text file or not
	 * 
	 * @param path The initial traversal point
	 * @return Returns a boolean depending on if the path is a text file or not
	 */
	public static boolean isText(Path path) {
		String tolowercase = path.toString().toLowerCase();
		return ((tolowercase.endsWith(".txt") || tolowercase.endsWith(".text")) && Files.isRegularFile(path));
	}

	/**
	 * Adds Path from the @Build function
	 * 
	 * @param file The file whose path we are adding
	 * @throws IOException
	 */
	public void addPath(Path file) throws IOException {
		addPath(file, this.invertedIndex);
	}

	/**
	 * Created the Path, this method is used in the Driver class
	 * 
	 * @param file  The file added to inverted index
	 * @param index The inverted index we are adding to
	 * @throws IOException
	 */
	public static void addPath(Path file, InvertedIndex index) throws IOException {
		Stemmer stemmer = new SnowballStemmer(DEFAULT);
		try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);) {
			String line = reader.readLine();
			int i = 0;
			String location = file.toString();
			while (line != null) {

				String[] lines = TextParser.parse(line);

				for (String elements : lines) {
					String element = stemmer.stem(elements).toString();
					index.add(element, location, ++i);
				}
				line = reader.readLine();
			}

		}
	}
}