package MPSI;
import java.util.ArrayList;

/** Abstract class for representing a generic MPSI protocol
 * @author Michael
 *
 */
public abstract class GenericMPSI {
	protected ArrayList<ArrayList<String>> datasets;
	protected int num_parties;
	protected KeyGenerator keys;
	protected Network network;
	protected Performance performance;
	
	/** Constructs a generic MPSI protocol
	 * @param datasets List containing the dataset of each party
	 * @param network Network measurement
	 * @param performance Performance measurement
	 * @param prime_bits Specifies the number of bits required for the prime factor of n
	 * @param num_parties Number of parties
	 * @param dec_threshold Least number of parties required for shared decryption
	 */
	public GenericMPSI(ArrayList<ArrayList<String>> datasets, Network network, Performance performance, int prime_bits, int num_parties, int dec_threshold)
	{
		this.datasets = datasets;
		this.num_parties = num_parties;
		this.network = network;
		this.performance = performance;
		keys = new KeyGenerator(prime_bits, num_parties, dec_threshold);
	}
	
	/**
	 * Initialization stage of the protocol
	 */
	public abstract void initialize();
	
	/**
	 * Interactive stage of the protocol
	 */
	public abstract void execute();
	
	/**
	 * Displaying the computed set intersection
	 */
	public abstract void results();
}
