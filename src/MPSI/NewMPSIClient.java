package MPSI;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import paillierp.PaillierThreshold;
import paillierp.PartialDecryption;
import paillierp.key.PaillierPrivateThresholdKey;

/** Class for the NewMPSI client
 * @author Michael
 *
 */
public class NewMPSIClient extends GenericParty {
	private PaillierThreshold paillier;
	private ArrayList<BigInteger> EIBF;
	private PartialDecryption dec_shares[];
	private BigInteger[] c;
	private BigInteger[] randomized_c;
	private BigInteger[] randomized_c_server;
	
	private Bloomfilter bloomfilter;
	private int random_exponent;
	
	/** Initializes the NewMPSI client
	 * @param id Id of this client
	 * @param dataset Dataset of this client
	 * @param private_key Private key of this client
	 * @param bloomfilter Bloomfilter of this client
	 * @param random_exponent Number of bits for the random exponent when randomizing
	 */
	public NewMPSIClient(int id, ArrayList<String> dataset, PaillierPrivateThresholdKey private_key, Bloomfilter bloomfilter, int random_exponent)
	{
		super(id, dataset, private_key);
		
		paillier = new PaillierThreshold(private_key.getPublicKey());
		paillier.setDecryptEncrypt(private_key);
		
		this.bloomfilter = bloomfilter;
		this.random_exponent = random_exponent;
	}
	
	/**
	 * Generates the EIBF of this client, which is the inverted encrypted bloomfilter
	 */
	public void initialize()
	{
		// Add each element of the dataset to the bloomfilter
		Iterator<String> itemIter = dataset.iterator();
        while (itemIter.hasNext()) {
        	String item = itemIter.next();
            bloomfilter.insert(item);
        }
        
        // Invert and encrypt the bloomfilter
        EIBF = bloomfilter.invertEncrypt(paillier);
	}
	
	/** Sends the EIBF of the client
	 * @return The client's EIBF
	 */
	public ArrayList<BigInteger> sendEIBF()
	{
		return EIBF;
	}
	
	/** Receives the c values sent by the server
	 * @param c The c values
	 */
	public void receivec(BigInteger[] c)
	{
		this.c = c;
	}
	
	/**
	 * First stage of the client. The comments use the same latex notation as in the corresponding paper.
	 */
	public void stage1()
	{
		// Client initializes the ShDec0 algorithm by raising the received c values to a random exponent
		for(int j=0; j<c.length; j++)
		{
			Random rand = new Random();
	        BigInteger exp = new BigInteger(random_exponent, rand);
			c[j] = c[j].modPow(exp, paillier.getPublicKey().getNSPlusOne());
			c[j] = paillier.randomize(c[j]);
		}
		randomized_c = c;
	}
	
	/** Sends the randomized c values back to the server
	 * @return The randomized c values
	 */
	public BigInteger[] sendRandomc()
	{
		return randomized_c;
	}
	
	/** Receives the final combined randomized c values from the server
	 * @param c The c final combined randomized c values
	 */
	public void receiveRandomc(BigInteger[] c)
	{
		this.randomized_c_server = c;
	}
	
	/**
	 * Second stage of the client. The comments use the same latex notation as in the corresponding paper.
	 */
	public void stage2()
	{
		// Finalize computation of sh_{i,j} = ShDec0(sk_i, c_j)
		this.dec_shares = new PartialDecryption[randomized_c_server.length];
		for(int j=0; j<randomized_c_server.length; j++)
		{
			dec_shares[j] = paillier.decrypt(randomized_c_server[j]);
		}
	}
	
	/** Send the decryption shares
	 * @return The decryption shares
	 */
	public PartialDecryption[] sendShares()
	{
		return dec_shares;
	}
	
}
