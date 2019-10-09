import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;


public class InvertedBuilder 
{

	/**
	 * File Traversal Done Here
	 * @param index
	 * @param parser
	 */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;
	
	private final InvertedIndex invertedIndex;
	
	private final Map
	
	
	
	
	public static void builder(InvertedIndex index, ArgumentParser parser)
	{

		if(parser.hasFlag("-path") && parser.getPath("-path") != null)
		{
			Path path = parser.getPath("-path");
			try(Stream<Path> nextPath = Files.walk(path, FileVisitOption.FOLLOW_LINKS)){
				var iterator = nextPath.iterator();
				while(iterator.hasNext()) {
					var path1 = iterator.next();
					if((path1.toString().toLowerCase().endsWith(".txt") || path1.toString().toLowerCase().endsWith(".text")) && Files.isRegularFile(index, )) {
						addPath(path1);
					}
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public InvertedBuilder(InvertedIndex invertedindex)
	{
		this.invertedIndex = invertedIndex;
	}
	public static void build(Path path) throws IOException
	{
		for(Path newPath: getTextFiles(path))
		{
			if(isFile(newPath))
			{
				addPath(path);
			}
		}
		
		
	}
	
	public static boolean isFile(Path path) throws IOException
	{
		String lowercaseFile = path.toString().toLowerCase();
		return ((lowercaseFile.endsWith(".txt") || lowercaseFile.endsWith(".text")) && Files.isRegularFile(path));
	}
	
	public static List<Path> getTextFiles(Path path) throws IOException
	{
		List<Path> list = Files.walk(path, FileVisitOption.FOLLOW_LINKS).collect(Collectors.toList());
		return list;
	}
	
		

	public void addPath(Path file) throws IOException 
	{
		
	
		Stemmer stemmer = new SnowballStemmer(DEFAULT);
		System.out.println("this is the path " + file);
		try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);) {
			String line = reader.readLine();
			String location = file.toString();
			int i = 0;
			while (line != null) {

				String[] lines = TextParser.parse(line);

				for (String elems : lines) {
					String element = (String) stemmer.stem(elems.toString());
					this.invertedIndex.add(element, location, ++i);
				}
				line = reader.readLine();
			}

		}

		catch (Exception e) {
			System.out.println("Something went wrong");
		}

	}

		
		
		

	
	
	
	
	
}
