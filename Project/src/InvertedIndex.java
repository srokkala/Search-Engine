import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class InvertedIndex 
{
	
	private TreeMap<String,TreeMap<String,ArrayList<Integer>>> newindex;
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	TreeMap<String,Integer> numbers;
	public InvertedIndex() {
		newindex = new TreeMap<>();
		numbers = new TreeMap<>();
	}
	
	public TreeMap<String, Integer> getNumbers() {
		return numbers;
	}

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
	
	public void printIndex(String outputFile) throws IOException {
		
		SimpleJsonWriter.asDoubleNestedObject(newindex,Path.of(outputFile));
		
	}
	public TreeMap<String, TreeMap<String, ArrayList<Integer>>> getnewindex(){
		return newindex;
	}
	

}
