import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.stream.Stream;


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
		
		if(parser.hasFlag("-counts"))
		{
			SimpleJsonWriter.asObject(test.getNumbers(),Path.of("actual/counts.json"));
		}
	
		

		System.out.println(Arrays.toString(args));

		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}

	
}
