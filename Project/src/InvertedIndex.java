import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * The invertedIndex class adds an element to the inverted index
 * 
 */
public class InvertedIndex 
{
	/**initializing a TreeMap in which we place our elements into*/ 
	private TreeMap<String,TreeMap<String,ArrayList<Integer>>> newindex;
	
	/** The default stemmer algorithm used by this class. */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/**
	 * New TreeMap in which we will count the words per file 
	 */
	TreeMap<String,Integer> numbers;
	
	/** 
	 * Constructor for Inverted Index
	 * initializing the TreeMaps we are using
	 */
	public InvertedIndex() {
		newindex = new TreeMap<>();
		numbers = new TreeMap<>();
	}
	
	/**
	 * returns TreeMap with filename and word count of each 
	 * @return numbers
	 */
	public TreeMap<String, Integer> getNumbers() {
		return numbers;
	}

	/**
	 *  This function will add the parameter element to the inverted index
	 * @param element
	 * @param file
	 * @param position
	 */
	public void add(String element, String file, int position)
	{
			
		if(newindex.containsKey(element))
		{
			if(newindex.get(element).containsKey(file)) {
				if(!(newindex.get(element).get(file).contains(position)))
				{
					newindex.get(element).get(file).add(position);
					this.getNumbers().put(file,getNumbers().get(file)+1);
				}
				
			}
			else
			{
				ArrayList<Integer> innerList = new ArrayList<>();
				innerList.add(position);
				newindex.get(element).put(file, innerList);
				if(getNumbers().get(file) == null)
				{
					this.getNumbers().put(file,1);
				}
				else
				{
					this.getNumbers().put(file,getNumbers().get(file)+1);
				}
				
				
			}
			
		}
		else
		{
			ArrayList<Integer>index = new ArrayList<>();
			TreeMap<String, ArrayList<Integer>> innerMap = new TreeMap<>();
			index.add(position);
			innerMap.put(file, index);
			newindex.put(element,innerMap);
			if(getNumbers().get(file) == null)
			{
				this.getNumbers().put(file,1);
			}
			else
			{
				this.getNumbers().put(file,getNumbers().get(file)+1);
			}
		}
	}
	
	
	/**
	 * Created the Path, this method is used in the Driver class
	 * @param file
	 * @throws IOException
	 */
	public void addPath(Path file) throws IOException
	{
		Stemmer stemmer = new SnowballStemmer(DEFAULT);
		System.out.println("this is the path " + file);
		try(BufferedReader reader = Files.newBufferedReader(file,StandardCharsets.UTF_8);)
		{
			String line = reader.readLine();
			int i = 0;
			while(line != null) {
			
				String[] lines = TextParser.parse(line);
				
				for(String elems: lines)
				{
					String element = (String) stemmer.stem(elems.toString());
					add(element,file.toString(), ++i);
				}
				line = reader.readLine();
			}
			
		}
		
		
		catch(Exception e)
		{
			System.out.println("Something went wrong");
		}
		
	}
	
	/**
	 * Creates the file to be outputted
	 * @param outputFile
	 * @throws IOException
	 */
	
	public void printIndex(String outputFile) throws IOException {
		
		SimpleJsonWriter.asDoubleNestedObject(newindex,Path.of(outputFile));
		
	}
	
	
	
	/**
	 * returns the index we created
	 * @return
	 */
	public TreeMap<String, TreeMap<String, ArrayList<Integer>>> getnewindex(){
		return newindex;
	}
	

}
