package MPSI;
import java.util.ArrayList;

/** Class for performing the measurements for the three protocols with varying parameters
 * @author Michael
 *
 */
public class Measurements {
	private int min_num_parties;
	private int max_num_parties;
	private int num_parties_interval;
	private int min_set_size;
	private int max_set_size;
	private int set_size_interval;
	private int intersection_size;
	private int element_length;
	private int default_set_size;
	private int default_num_parties;
	private ProtocolStats stats[][];
	private int prime_bits;
	
	/** Initializes the measurements with the given parameters
	 * @param min_num_parties Minimum number of parties
	 * @param max_num_parties Maximum number of parties
	 * @param num_parties_interval Interval for the number of parties
	 * @param min_set_size Minimum set size
	 * @param max_set_size Maximum set size
	 * @param set_size_interval Interval for the set size
	 */
	public Measurements(int min_num_parties, int max_num_parties, int num_parties_interval, int min_set_size, int max_set_size, int set_size_interval)
	{
		this.min_num_parties = min_num_parties;
		this.max_num_parties = max_num_parties;
		this.num_parties_interval = num_parties_interval;
		this.min_set_size = min_set_size;
		this.max_set_size = max_set_size;
		this.set_size_interval = set_size_interval;
		
		// default parameter values
		intersection_size = 3;
		element_length = 10;
		default_set_size = 200;
		default_num_parties = 4;
		prime_bits = 60;
		
		stats = new ProtocolStats[2][3];
		for(int i=0; i<3; i++)
		{
			stats[0][i] = new ProtocolStats();
			stats[1][i] = new ProtocolStats();
		}
	}
	
	/** Get the measurement results
	 * @return List for varying number of parties, and varying set sizes, a list of the statistics for each of the three protocols
	 */
	public ProtocolStats[][] getStats()
	{
		return stats;
	}
	
	/**
	 * Starts the measurements with the different parameters specified before
	 */
	public void start()
	{
		// Get the measurements for varying number of parties
		for(int num_parties = min_num_parties; num_parties <= max_num_parties; num_parties += num_parties_interval)
		{
			System.out.println("Update - number of parties: " + num_parties);
			
			DatasetGenerator setGen = new DatasetGenerator(num_parties, default_set_size, intersection_size, element_length);
			ArrayList<ArrayList<String>> sets = setGen.getSets();
			long intermediate_stats[][][];
			long total;
			
			intermediate_stats = NewMPSI(sets);
			
			total = intermediate_stats[0][0][0] + intermediate_stats[0][0][1];
			stats[0][0].communicational_server.add(new DataPoint(num_parties, total));
			total = intermediate_stats[0][1][0] + intermediate_stats[0][1][1];
			stats[0][0].communicational_client.add(new DataPoint(num_parties, total));
			
			stats[0][0].computational_setup_server.add(new DataPoint(num_parties, intermediate_stats[1][0][0]));
			stats[0][0].computational_interactive_server.add(new DataPoint(num_parties, intermediate_stats[1][0][1]));
			stats[0][0].computational_setup_client.add(new DataPoint(num_parties, intermediate_stats[1][1][0]));
			stats[0][0].computational_interactive_client.add(new DataPoint(num_parties, intermediate_stats[1][1][1]));
			
			intermediate_stats = MiyajiNishidaMPSI(sets);
			int dealer = intermediate_stats[0].length - 1;
			
			total = intermediate_stats[0][dealer][0] + intermediate_stats[0][dealer][1];
			stats[0][1].communicational_server.add(new DataPoint(num_parties, total));
			total = intermediate_stats[0][0][0] + intermediate_stats[0][0][1];
			stats[0][1].communicational_client.add(new DataPoint(num_parties, total));
			
			stats[0][1].computational_setup_server.add(new DataPoint(num_parties, intermediate_stats[1][dealer][0]));
			stats[0][1].computational_interactive_server.add(new DataPoint(num_parties, intermediate_stats[1][dealer][1]));
			stats[0][1].computational_setup_client.add(new DataPoint(num_parties, intermediate_stats[1][0][0]));
			stats[0][1].computational_interactive_client.add(new DataPoint(num_parties, intermediate_stats[1][0][1]));
			
			intermediate_stats = HazayVenkitaMPSI(sets);
			total = intermediate_stats[0][0][0] + intermediate_stats[0][0][1];
			stats[0][2].communicational_server.add(new DataPoint(num_parties, total));
			total = intermediate_stats[0][1][0] + intermediate_stats[0][1][1];
			stats[0][2].communicational_client.add(new DataPoint(num_parties, total));
			
			stats[0][2].computational_setup_server.add(new DataPoint(num_parties, intermediate_stats[1][0][0]));
			stats[0][2].computational_interactive_server.add(new DataPoint(num_parties, intermediate_stats[1][0][1]));
			stats[0][2].computational_setup_client.add(new DataPoint(num_parties, intermediate_stats[1][1][0]));
			stats[0][2].computational_interactive_client.add(new DataPoint(num_parties, intermediate_stats[1][1][1]));
			
		}
		
		// Get the measurements for varying set sizes
		for(int set_size = min_set_size; set_size <= max_set_size; set_size += set_size_interval)
		{
			System.out.println("Update - set size: " + set_size);
			
			DatasetGenerator setGen = new DatasetGenerator(default_num_parties, set_size, intersection_size, element_length);
			ArrayList<ArrayList<String>> sets = setGen.getSets();
			long intermediate_stats[][][];
			long total;
			
			intermediate_stats = NewMPSI(sets);
			
			total = intermediate_stats[0][0][0] + intermediate_stats[0][0][1];
			stats[1][0].communicational_server.add(new DataPoint(set_size, total));
			total = intermediate_stats[0][1][0] + intermediate_stats[0][1][1];
			stats[1][0].communicational_client.add(new DataPoint(set_size, total));
			
			stats[1][0].computational_setup_server.add(new DataPoint(set_size, intermediate_stats[1][0][0]));
			stats[1][0].computational_interactive_server.add(new DataPoint(set_size, intermediate_stats[1][0][1]));
			stats[1][0].computational_setup_client.add(new DataPoint(set_size, intermediate_stats[1][1][0]));
			stats[1][0].computational_interactive_client.add(new DataPoint(set_size, intermediate_stats[1][1][1]));
			
			intermediate_stats = MiyajiNishidaMPSI(sets);
			int dealer = intermediate_stats[0].length - 1;
			
			total = intermediate_stats[0][dealer][0] + intermediate_stats[0][dealer][1];
			stats[1][1].communicational_server.add(new DataPoint(set_size, total));
			total = intermediate_stats[0][0][0] + intermediate_stats[0][0][1];
			stats[1][1].communicational_client.add(new DataPoint(set_size, total));
			
			stats[1][1].computational_setup_server.add(new DataPoint(set_size, intermediate_stats[1][dealer][0]));
			stats[1][1].computational_interactive_server.add(new DataPoint(set_size, intermediate_stats[1][dealer][1]));
			stats[1][1].computational_setup_client.add(new DataPoint(set_size, intermediate_stats[1][0][0]));
			stats[1][1].computational_interactive_client.add(new DataPoint(set_size, intermediate_stats[1][0][1]));
			
			intermediate_stats = HazayVenkitaMPSI(sets);
			total = intermediate_stats[0][0][0] + intermediate_stats[0][0][1];
			stats[1][2].communicational_server.add(new DataPoint(set_size, total));
			total = intermediate_stats[0][1][0] + intermediate_stats[0][1][1];
			stats[1][2].communicational_client.add(new DataPoint(set_size, total));
			
			stats[1][2].computational_setup_server.add(new DataPoint(set_size, intermediate_stats[1][0][0]));
			stats[1][2].computational_interactive_server.add(new DataPoint(set_size, intermediate_stats[1][0][1]));
			stats[1][2].computational_setup_client.add(new DataPoint(set_size, intermediate_stats[1][1][0]));
			stats[1][2].computational_interactive_client.add(new DataPoint(set_size, intermediate_stats[1][1][1]));
			
		}
	}
	
	/** Get optimal bloomfilter size and number of hashes given the number of elements in the set.
	 * The false positive rate is set to 2^(-50)
	 * @param set_size Number of elements in the set
	 * @return A list with the first element being the optimal number of bits and the second element being the optimal number of hash functions
	 */
	public int[] bloomError(int set_size)
	{
		int params[] = new int[2];
		double FPR = 8.881784197 * 0.0000000000000001; // 2^(-50)
		params[0] = (int) Math.ceil((set_size * Math.log(FPR)) / Math.log(1 / Math.pow(2, Math.log(2))));
		params[1] = (int) Math.round((params[0] / set_size) * Math.log(2));
		return params;
	}
	
	/** Execute the NewMPSI protocol
	 * @param sets List of sets for each party
	 * @return Network and performance measurement results
	 */
	public long[][][] NewMPSI(ArrayList<ArrayList<String>> sets)
	{
		int num_parties = sets.size();
		int bloom_params[] = bloomError(sets.get(0).size());
		int bloom_size = bloom_params[0];
		int bloom_k = bloom_params[1];
		int random_exponent = 100;
		
		Network network = new Network(num_parties);
		Performance performance = new Performance(num_parties);
		NewMPSI test = new NewMPSI(sets, network, performance, prime_bits, num_parties, bloom_size, bloom_k, random_exponent);
		
		test.initialize();
		test.execute();
		test.results();
		
		long stats[][][] = new long[2][][];
		stats[0] = network.getStats();
		stats[1] = performance.getStats();
		
		return stats;
	}
	
	/** Execute the MiyajiNishidaMPSI protocol
	 * @param sets List of sets for each party
	 * @return Network and performance measurement results
	 */
	public long[][][] MiyajiNishidaMPSI(ArrayList<ArrayList<String>> sets)
	{
		int num_parties = sets.size();
		int bloom_params[] = bloomError(sets.get(0).size());
		int bloom_size = bloom_params[0];
		int bloom_k = bloom_params[1];
		
		Network network = new Network(num_parties+1);
		Performance performance = new Performance(num_parties+1);
		
		// limit number of parties, because this protocol's complexity is not linear
		if(num_parties <= 30)
		{
			MiyajiNishidaMPSI test = new MiyajiNishidaMPSI(sets, network, performance, prime_bits, num_parties, bloom_size, bloom_k);
			
			test.initialize();
			test.execute();
			test.results();
		}
		
		long stats[][][] = new long[2][][];
		stats[0] = network.getStats();
		stats[1] = performance.getStats();
		
		return stats;
	}
	
	/** Execute the HazayVenkitaMPSI protocol
	 * @param sets List of sets for each party
	 * @return Network and performance measurement results
	 */
	public long[][][] HazayVenkitaMPSI(ArrayList<ArrayList<String>> sets)
	{
		int num_parties = sets.size();
		int random_exponent = 100;
		
		Network network = new Network(num_parties);
		Performance performance = new Performance(num_parties);
		
		// limit set size, because this protocol's complexity is not linear
		if(sets.get(0).size() <= 3500)
		{
			HazayVenkitaMPSI test = new HazayVenkitaMPSI(sets, network, performance, prime_bits, num_parties, random_exponent);
			
			test.initialize();
			test.execute();
			test.results();
		}
		
		long stats[][][] = new long[2][][];
		stats[0] = network.getStats();
		stats[1] = performance.getStats();
		
		return stats;
	}
	
	public void test()
	{
		DatasetGenerator setGen = new DatasetGenerator(3, 500, 4, 10);
		ArrayList<ArrayList<String>> sets = setGen.getSets();
		
		HazayVenkitaMPSI(sets);
	}
}
