package MPSI;
import java.util.ArrayList;
import java.util.List;

/** Class for the MiyajiNishida protocol
 * @author Michael
 *
 */
public class MiyajiNishidaMPSI extends GenericMPSI {
	
	private MiyajiNishidaDealer dealer;
	private List<MiyajiNishidaParty> parties;
	private int bloom_size;
	private int bloom_k;
	
	/** Initializes the MiyajiNishida protocol
	 * @param datasets List of datasets for each party
	 * @param network Network measurement
	 * @param performance Performance measurement
	 * @param prime_bits Specifies the number of bits required for the prime factor of n
	 * @param num_parties Number of parties
	 * @param bloom_size Bloomfilter number of bits
	 * @param bloom_k Bloomfilter number of hashes
	 */
	public MiyajiNishidaMPSI(ArrayList<ArrayList<String>> datasets, Network network, Performance performance, int prime_bits, int num_parties, int bloom_size, int bloom_k)
	{
		super(datasets, network, performance, prime_bits, num_parties, num_parties-1);
		
		this.bloom_size = bloom_size;
		this.bloom_k = bloom_k;
	}
	
	/** Creates a new bloomfilter
	 * @return The bloomfilter
	 */
	private Bloomfilter newBloomfilter()
	{
		return new Bloomfilter(bloom_size, bloom_k);
	}
	
	/**
	 * Initializes the protocol. Creates the Dealer and the parties. For each party the initialization step is performed.
	 */
	@Override
	public void initialize() {
		dealer = new MiyajiNishidaDealer(num_parties, keys.getPrivate(0));
		
		parties = new ArrayList<MiyajiNishidaParty>();
		for(int i=0; i<num_parties; i++)
		{
			MiyajiNishidaParty p = new MiyajiNishidaParty(i, datasets.get(i), num_parties, keys.getPrivate(i), newBloomfilter());
			parties.add(p);
			
			performance.start(i);
			p.initialize();
			performance.stop_init(i);
		}
	}

	/**
	 * Executes the interactive part of the protocol
	 */
	@Override
	public void execute()
	{	
		// Each party sends its EBF to the dealer
		for(int i=0; i<num_parties; i++)
		{
			dealer.receiveEBF(parties.get(i).sendEBF());
			network.send(i, num_parties, parties.get(i).sendEBF());
		}
		
		// The dealer performs its first stage, which is to compute the n-subtraction of IBF
		performance.start(num_parties);
		dealer.stage1();
		performance.stop(num_parties);
		
		// The dealer sends the encrypted IBF to all parties
		for(int i=0; i<num_parties; i++)
		{
			parties.get(i).receiveCombined(dealer.sendCombined());
			network.send(num_parties, i, dealer.sendCombined());
		}
		
		// The parties perform the first stage, which is to compute the decryption shares of the received EIBF
		for(int i=0; i<num_parties; i++)
		{
			performance.start(i);
			parties.get(i).stage1();
			performance.stop(i);
		}
		
		// Each party sends their decryption share to every other party
		for(int i=0; i<num_parties; i++)
		{
			for(int j=0; j<num_parties; j++)
			{
				if(i==j)
					continue;
				
				parties.get(j).receiveShares(parties.get(i).sendShares(), i);
				network.send(i, j, parties.get(i).sendShares());
			}
		}
		
		// The parties perform the second stage, which is to combine the received decryption shares and compute the intersection
		for(int i=0; i<num_parties; i++)
		{
			performance.start(i);
			parties.get(i).stage2();
			performance.stop(i);
		}
	}

	/**
	 * Shows the computed set intersection
	 */
	@Override
	public void results() {
		System.out.println("____________________________________________________");
		System.out.println("Set intersection:");
		
		ArrayList<String> intersection = parties.get(0).getIntersection();
		
		for(int j=0; j<intersection.size(); j++)
			System.out.println(intersection.get(j));
	}
}
