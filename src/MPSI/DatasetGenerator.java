package MPSI;
import java.util.ArrayList;
import java.util.Collections;

/** Class for generating datasets
 * @author Michael
 *
 */
public class DatasetGenerator {
	private int num_sets;
	private int set_size;
	private int intersection_size;
	private int element_length;
	private ArrayList<ArrayList<String>> datasets;
	
	/** Constructs a new dataset generator
	 * @param num_sets Number of sets to generate, one for each party
	 * @param set_size Number of elements in each set
	 * @param intersection_size Number of equal elements in the sets
	 * @param element_length Number of characters for each element
	 */
	public DatasetGenerator(int num_sets, int set_size, int intersection_size, int element_length)
	{
		this.num_sets = num_sets;
		this.set_size = set_size;
		this.intersection_size = intersection_size;
		this.element_length = element_length;
		
		datasets = new ArrayList<ArrayList<String>>();
		generate();
	}
	
	/** Creates a random element
	 * @return a random element
	 */
	private String randomElement()
	{
		String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		StringBuilder builder = new StringBuilder();
		
		for(int i=0; i<element_length; i++) 
		{
			int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
			builder.append(ALPHA_NUMERIC_STRING.charAt(character));
		}

		return builder.toString();
	}
	
	/**
	 * Generates the datasets according to the parameters specified before.
	 */
	private void generate()
	{
		for(int i=0; i<num_sets; i++)
		{
			datasets.add(new ArrayList<String>());
			for(int j=0; j<set_size; j++)
			{
				datasets.get(i).add(randomElement());
			}
		}
		
		for(int i=0; i<intersection_size; i++)
		{
			String common = randomElement();
			for(int j=0; j<num_sets; j++)
			{
				datasets.get(j).add(common);
			}
		}
		
		for(int i=0; i<num_sets; i++)
		{
			Collections.shuffle(datasets.get(i));
		}
	}
	
	/**
	 * @return The list of sets that have been generated
	 */
	public ArrayList<ArrayList<String>> getSets()
	{
		return datasets;
	}
}
