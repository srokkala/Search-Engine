import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.stream.Stream;

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
		// store initial start time
		
		//all the index exceptions should be done here 
		InvertedIndex test = new InvertedIndex();
		ArgumentParser parser = new ArgumentParser(args);

		Instant start = Instant.now();
		
		
		if(parser.hasFlag("-path") && parser.getPath("-path") != null)
		{
			Path path = parser.getPath("-path");
			// TODO A little more of this needs to move to another class.
			try(Stream<Path> nextPath = Files.walk(path, FileVisitOption.FOLLOW_LINKS)){
				var iterator = nextPath.iterator();
				while(iterator.hasNext()) {
					var path1 = iterator.next();
					if(path1.toString().toLowerCase().endsWith(".txt") || path1.toString().toLowerCase().endsWith(".text")) {
						test.addPath(path1);
					}
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	
		
		if(parser.hasFlag("-index") && parser.getString("-index") != null)
		{
			test.printIndex(parser.getString("-index"));
		}
		
		if(parser.hasFlag("-index"))
		{
			SimpleJsonWriter.asDoubleNestedObject(test.getnewindex(), Path.of("index.json"));
		}
		
		/* TODO
		if (parser.hasFlag("-index"))
		{
			Path path = parser.getPath("-index", Path.of("index.json"));
			
			try {
				test.writeJson(path);
			}
			catch (IOException e) {
				System.out.println("Warning: Unable to output your inverted index as JSON at: " + path);
			}			
		}
		*/
		
		if(parser.hasFlag("-counts"))
		{
			SimpleJsonWriter.asObject(test.getNumbers(),Path.of("actual/counts.json"));
		}
	
		

		System.out.println(Arrays.toString(args)); // TODO Remove

		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}

	
}
