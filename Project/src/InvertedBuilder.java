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

public class InvertedBuilder {
	
	private final InvertedIndex invertedIndex;

	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;
	/**
	 * @param invertedIndex Inverted Index structure that will be built.
	 */
	public InvertedBuilder(InvertedIndex invertedIndex) {
		this.invertedIndex = invertedIndex;
	}

	public void build(Path path) throws IOException {
		for (Path currentPath : getTextFiles(path)) {
			if (isText(currentPath)) {
				addPath(currentPath);
			}
		}
	}
	
	
	public static List<Path> getTextFiles(Path path) throws IOException {
		List<Path> list = Files.walk(path, FileVisitOption.FOLLOW_LINKS).collect(Collectors.toList());
		return list;
	}


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
	public  void addPath(Path file) throws IOException {
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

		catch (Exception e) {
			System.out.println("Something went wrong");
		}

	}
}
