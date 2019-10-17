import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

// TODO Check SimpleJsonWriter!

// TODO No more warnings in code from here on out
/*
 * TODO 
Javadoc: Missing comment for private declaration	InvertedBuilder.java	/Project/src	line 15	Java Problem
Javadoc: Missing comment for public declaration	InvertedBuilder.java	/Project/src	line 13	Java Problem
Javadoc: Missing comment for public declaration	InvertedBuilder.java	/Project/src	line 17	Java Problem
Javadoc: Missing comment for public declaration	InvertedBuilder.java	/Project/src	line 25	Java Problem
Javadoc: Missing comment for public declaration	InvertedBuilder.java	/Project/src	line 34	Java Problem
Javadoc: Missing comment for public declaration	InvertedBuilder.java	/Project/src	line 40	Java Problem
Javadoc: Parameter index is not declared	InvertedBuilder.java	/Project/src	line 48	Java Problem
Javadoc: The method asNestedObject(TreeMap<String,ArrayList<Integer>>, Writer, int) in the type SimpleJsonWriter is not applicable for the arguments (Map, Writer, int)	SimpleJsonWriter.java	/Project/src	line 259	Java Problem
Javadoc: The method asNestedObject(TreeMap<String,ArrayList<Integer>>, Writer, int) in the type SimpleJsonWriter is not applicable for the arguments (Map, Writer, int)	SimpleJsonWriter.java	/Project/src	line 274	Java Problem
 */

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

		if (argumentParser.hasFlag("-path") && argumentParser.getPath("-path") != null) 
		{
			Path path = argumentParser.getPath("-path");
			try {
				builder.build(path);
			} catch (IOException e) {
				System.out.println("Path can not be traversed: " + path.toString());
			}
		}

		if (argumentParser.hasFlag("-index")) {
			Path path = argumentParser.getPath("-index", Path.of("index.json"));
			try {
				invertedIndex.printIndex(path.toString()); // TODO Don't convert the path to a String object
			} catch (IOException e) {
				System.out.println("There was an issue while writing inverted index to file: " + path.toString());
			}
		}

		if(argumentParser.hasFlag("-counts")) {
			Path path = argumentParser.getPath("-counts", Path.of("counts.json"));
			try {
				SimpleJsonWriter.asObject(invertedIndex.getCount(), path);
			} catch (IOException e) {
				System.out.println("There was an issue while writing counts info to file: " + path.toString());
			}
		}

		/* Calculate time elapsed and output */
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}}
