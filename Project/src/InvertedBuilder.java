import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
* Separate Class to Help Build The Inverted Index
*/
public class InvertedBuilder {

	/**
	 * The New Inverted Index we want to add our elements to
	 */
	private final InvertedIndex invertedIndex;

	/**
	 * Default SnowBall Stemmer
	 */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/**
	 * Constructor Method for InvertedBuilder
	 * 
	 * @param invertedIndex Inverted Index that will be built.
	 */
	public InvertedBuilder(InvertedIndex invertedIndex) {
		this.invertedIndex = invertedIndex;
	}

	/**
	 * Directory Traversal Method
	 * 
	 * @param path
	 * @throws IOException
	 */
	public void build(Path path) throws IOException {
		for (Path currentPath : Files.walk(path, FileVisitOption.FOLLOW_LINKS).collect(Collectors.toList())) {
			if (isText(currentPath)) {
				addPath(currentPath);
			}
		}
	}

	/**
	 * 
	 * @param path
	 * @return a boolean, checks if file is a text file
	 */
	public static boolean isText(Path path) {
		String tolowercase = path.toString().toLowerCase();
		return ((tolowercase.endsWith(".txt") || tolowercase.endsWith(".text")) && Files.isRegularFile(path));
	}

	/**
	 * Created the Path, this method is used in the Driver class
	 * 
	 * @param index
	 * @param file
	 * @throws IOException
	 */
	public void addPath(Path file) throws IOException {
		Stemmer stemmer = new SnowballStemmer(DEFAULT);
		System.out.println("this is the path " + file);
		try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);) {
			String line = reader.readLine();
			int i = 0;
			while (line != null) {

				String[] lines = TextParser.parse(line);

				for (String elems : lines) {
					String element = (String) stemmer.stem(elems.toString());
					this.invertedIndex.add(element, file.toString(), ++i);
				}
				line = reader.readLine();
			}

		}

	}
}
