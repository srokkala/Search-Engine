import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2019
 */
public class Driver {

	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 *
	 * @param args flag/value pairs used to start this program
	 *
	 */
	public static void main(String[] args) {
		/* Store initial start time */
		Instant start = Instant.now();
		InvertedIndex invertedIndex = new InvertedIndex();
		ArgumentParser argumentParser = new ArgumentParser(args);
		InvertedBuilder builder = new InvertedBuilder(invertedIndex);

		try {
			if (argumentParser.hasFlag("-path") && argumentParser.getPath("-path") != null) {
				Path path = argumentParser.getPath("-path");
				try {
					builder.build(path);
				} catch (IOException e) {
					System.out.println("There was an issue during traversal");
				}
			}
		} catch (Exception e) {
			System.out.println("There was an issue with adding the path");
		}

		if (argumentParser.hasFlag("-index")) {
			Path path = argumentParser.getPath("-index", Path.of("index.json"));
			try {
				invertedIndex.printIndex(path.toString());
			} catch (IOException e) {
				System.out.println("There was an issue while writing the index");
			}
		}

		if (argumentParser.hasFlag("-counts")) {
			Path path = argumentParser.getPath("-counts", Path.of("counts.json"));
			try {
				SimpleJsonWriter.asObject(invertedIndex.getCount(), path);
			} catch (IOException e) {
				System.out.println("There was an issue while writing");
			}
		}

		if (argumentParser.hasFlag("-results")) {
			try {
				SimpleJsonWriter.asQuery(Collections.emptyMap(), Path.of("results.json"));
			} catch (Exception e) {
				System.out.println("There was an issue while writing");
			}
		}

		if (argumentParser.hasFlag("-query") && argumentParser.getPath("-query") != null) {
			Path queryPath = argumentParser.getPath("-query");
			try {
				QueryMaker querymaker = new QueryMaker(invertedIndex, queryPath);
				querymaker.queryGenerator();

				if (argumentParser.hasFlag("-exact")) {
					querymaker.exactSearch();
				} else {
					querymaker.querySearch();
				}

				if (argumentParser.hasFlag("-results")) {
					Path path = argumentParser.getPath("-results");

					if (path == null) {
						path = Path.of("results.json");
					}

					SimpleJsonWriter.asQuery(querymaker.getQuery(), path);

				}

			} catch (IOException e) {
				System.out.println("There was an issue while reading");
			} catch (Exception ee) {
				System.out.println("There was an issue while changing the file ");
			}

		}

		/* Calculate time elapsed and output */
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}
}
