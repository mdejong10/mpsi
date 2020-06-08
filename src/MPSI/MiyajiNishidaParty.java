package MPSI;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;

import paillierp.PaillierThreshold;
import paillierp.PartialDecryption;
import paillierp.key.PaillierPrivateThresholdKey;

/** Class for the MiyajiNishida party
 * @author Michael
 *
 */
public class MiyajiNishidaParty extends GenericParty {
	private PaillierThreshold paillier;
	private ArrayList<BigInteger> EBF;
	private BigInteger EIBF[];
	private PartialDecryption dec_shares[][];
	private BigInteger dec[];
	private ArrayList<String> intersection;
	
	private int num_parties;
	private Bloomfilter bloomfilter;
	private Bloomfilter final_bloomfilter;
	
	/** Initializes the MiyajiNishida party
	 * @param id Id of this party
	 * @param dataset Dataset of this party
	 * @param num_parties Number of parties
	 * @param private_key Private key of this party
	 * @param bloomfilter Bloomfilter of this party
	 */
	public MiyajiNishidaParty(int id, ArrayList<String> dataset, int num_parties, PaillierPrivateThresholdKey private_key, Bloomfilter bloomfilter)
	{
		super(id, dataset, private_key);
		
		paillier = new PaillierThreshold(private_key.getPublicKey());
		paillier.setDecryptEncrypt(private_key);
		dec_shares = new PartialDecryption[num_parties][bloomfilter.SIZE];
		dec = new BigInteger[bloomfilter.SIZE];
		
		this.num_parties = num_parties;
		this.bloomfilter = bloomfilter;
		this.intersection = new ArrayList<String>();
	}
	
	/**
	 * Generates the EBF of this client, which is the encrypted bloomfilter
	 */
	public void initialize()
	{
		Iterator<String> itemIter = dataset.iterator();
        while (itemIter.hasNext()) {
        	String item = itemIter.next();
            bloomfilter.insert(item);
        }
        
        EBF = bloomfilter.encrypt(paillier);
	}
	
	/** Sends the EBF
	 * @return The EBF
	 */
	public ArrayList<BigInteger> sendEBF()
	{
		return EBF;
	}
	
	/** Receives the EIBF
	 * @param combined The EIBF
	 */
	public void receiveCombined(BigInteger combined[])
	{
		EIBF = combined;
	}
	
	/**
	 * First stage of the party. Computes the decryption shares of the received EIBF.
	 */
	public void stage1()
	{
		for(int i=0; i<bloomfilter.SIZE; i++)
		{
			dec_shares[id][i] = paillier.decrypt(EIBF[i]);
		}
	}
	
	/** Send decryption shares of this party
	 * @return The decryption shares
	 */
	public PartialDecryption[] sendShares()
	{
		return dec_shares[id];
	}
	
	/** Receive decryption shares of other parties
	 * @param share Array of decryption shares
	 * @param party Id of the sender party
	 */
	public void receiveShares(PartialDecryption share[], int party)
	{
		dec_shares[party] = share;
	}
	
	/**
	 * Second stage of the party. Combines the received decryption shares and compute the intersection.
	 */
	public void stage2()
	{	
		// Combining the received decryption shares
		for(int i=0; i<bloomfilter.SIZE; i++)
		{
			PartialDecryption shares[] = new PartialDecryption[num_parties];
			
			for(int j=0; j<num_parties; j++)
			{
				shares[j] = dec_shares[j][i];
			}
			
			dec[i] = paillier.combineShares(shares);
		}
		
		// Decrypted value of 0 corresponds to a 1 in the bloomfilter for set intersection
		for(int i=0; i<bloomfilter.SIZE; i++)
		{
			if(dec[i].compareTo(BigInteger.ZERO) == 0)
				dec[i] = BigInteger.ONE;
			else
				dec[i] = BigInteger.ZERO;
		}
		
		// Compute the final decrypted integrated bloomfilter
		final_bloomfilter = new Bloomfilter(bloomfilter.SIZE, bloomfilter.K);
		final_bloomfilter.initialize(dec);
		
		// Add elements from the dataset that are contained in the bloomfilter to the intersection
		for(int j=0; j<dataset.size(); j++)
		{
			if(final_bloomfilter.check(dataset.get(j)))
				intersection.add(dataset.get(j));
		}
	}
	
	/** Get the computed intersection
	 * @return The list of elements in the intersection of all parties
	 */
	public ArrayList<String> getIntersection()
	{
		return intersection;
	}
	
}
