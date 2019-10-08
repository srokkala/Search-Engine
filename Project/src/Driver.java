import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;


// TODO Need to address the Javadoc warnings

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2019
 */
public class Driver {

	/*
	 * TODO What to include in Driver:
	 * It is the programmer-specific class that tends not to be shared with other
	 * developers. In Driver is where we see specific flag/value pairs, but anything
	 * that is considered generally useful needs to be outside of Driver so other
	 * developers can take advantange of the code.
	 */
	
	/*
	 * TODO Exception handling
	 * 
	 * Generally, to make code general, you throw exceptions. The only place you dont
	 * is where you interact with the user. (In this case, is Driver.main)
	 *
	 * All output to the user must be both user-friendly (stack trace) and
	 * informative (no messages like "Unknown error occurred.") Need to give the 
	 * user enough information to re-run your code without the same issue.
	 */
	
	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 *
	 * @param args flag/value pairs used to start this program
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		

		InvertedIndex index = new InvertedIndex();
		ArgumentParser parser = new ArgumentParser(args);
		Instant start = Instant.now();
		
		InvertedBuilder builder = new InvertedBuilder(index);
		
		
		if(parser.hasFlag("-path") && parser.getString("-path") != null)
		{
			Path path = parser.getPath("-path");
			try {
				builder.build(path);
				index.printIndex(parser.getString("-index"));
			} catch (IOException e) {
				System.out.println("Warning: Unable to output your inverted index as JSON");
			}
		}
		
		if(parser.hasFlag("-index"))
		{
			Path path = parser.getPath("-index",Path.of("index.json"));
			try {
				index.printIndex(path.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if(parser.hasFlag("-counts"))
		{
			Path path = parser.getPath("-counts",Path.of("counts.json"));
			try {
				SimpleJsonWriter.asObject(index.getNumbers(),Path.of("actual/counts.json"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		InvertedBuilder.builder(index, parser);
		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}

	
}
