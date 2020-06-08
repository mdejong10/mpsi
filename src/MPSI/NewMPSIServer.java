package MPSI;

import java.math.BigInteger;
import java.util.ArrayList;

import paillierp.PaillierThreshold;
import paillierp.PartialDecryption;
import paillierp.key.PaillierPrivateThresholdKey;

/** Class for the NewMPSI Server
 * @author Michael
 *
 */
public class NewMPSIServer extends GenericParty {
	
	private ArrayList<String> intersection;
	private int numClients;
	private Bloomfilter bloomfilter;
	private PaillierThreshold paillier;
	private ArrayList<ArrayList<BigInteger>> EIBFs;
	private BigInteger final_c[];
	private BigInteger final_random_c[];
	private BigInteger randomized_c[][];
	private PartialDecryption dec_shares[][];
	private BigInteger dec[];
	
	/** Initializes the NewMPSI server
	 * @param numClients Number of clients
	 * @param dataset Dataset of the server
	 * @param private_key Private key of the server
	 * @param bloomfilter Bloomfilter of the server
	 */
	public NewMPSIServer(int numClients, ArrayList<String> dataset, PaillierPrivateThresholdKey private_key, Bloomfilter bloomfilter)
	{
		super(0, dataset, private_key);
		
		this.intersection = new ArrayList<String>();
		this.numClients = numClients;
		paillier = new PaillierThreshold(private_key.getPublicKey());
		paillier.setDecryptEncrypt(private_key);
		EIBFs = new ArrayList<ArrayList<BigInteger>>();
		randomized_c = new BigInteger[dataset.size()][numClients];
		dec_shares = new PartialDecryption[dataset.size()][numClients];
		dec = new BigInteger[dataset.size()];
		this.bloomfilter = bloomfilter;
	}
	
	public void initialize()
	{
		return;
	}
	
	/** Receives the EIBF sent by a client
	 * @param EIBF The EIBF received
	 */
	public void receiveEIBF(ArrayList<BigInteger> EIBF)
	{
		EIBFs.add(EIBF);
	}
	
	/**
	 * First stage of the server. The comments use the same latex notation as in the corresponding paper.
	 */
	public void stage1()
	{
		// Computes k hash values of each y_j in S_t
		int hashes[][] = new int[dataset.size()][bloomfilter.K];
		for(int i=0; i<dataset.size(); i++)
		{
			for(int j=0; j<bloomfilter.K; j++)
			{
				hashes[i][j] = bloomfilter.hash(dataset.get(i), j);
			}
		}
		
		// Computes all C_d^{i,j} = EIBF_i[h_d(y_j)]
		BigInteger C_values[][][] = new BigInteger[numClients][dataset.size()][bloomfilter.K];
		for(int i=0; i<numClients; i++)
		{
			for(int j=0; j<dataset.size(); j++)
			{
				for(int k=0; k<bloomfilter.K; k++)
				{
					C_values[i][j][k] = EIBFs.get(i).get(hashes[j][k]);
				}
			}
		}
		
		// Computes c_j^i = C_1^{i,j} +H ... +H C_k^{i,j}
		BigInteger c_values[][] = new BigInteger[numClients][dataset.size()];
		for(int i=0; i<numClients; i++)
		{
			for(int j=0; j<dataset.size(); j++)
			{
				c_values[i][j] = C_values[i][j][0];
				for(int k=1; k<bloomfilter.K; k++)
				{
					c_values[i][j] = paillier.add(c_values[i][j], C_values[i][j][k]);
				}
			}
		}
		
		// Computes c_j = ReRand(c_j^1 +H ... +H c_j^{t-1})
		final_c = new BigInteger[dataset.size()];
		for(int j=0; j<dataset.size(); j++)
		{
			final_c[j] = c_values[0][j];
			
			for(int i=1; i<numClients; i++)
			{
				final_c[j] = paillier.add(final_c[j], c_values[i][j]);
			}
			
			final_c[j] = paillier.randomize(final_c[j]);
		}
		
	}
	
	/** Sends the final c values
	 * @return The c values
	 */
	public BigInteger[] sendc()
	{
		return final_c;
	}
	
	/** Receive the randomized final c values of the client
	 * @param c Randomized c values by the client
	 * @param client Id of the client
	 */
	public void receiveRandomc(BigInteger[] c, int client)
	{
		for(int j=0; j<dataset.size(); j++)
		{
			randomized_c[j][client] = c[j];
		}
	}
	
	/**
	 * Second stage of the server. The comments use the same latex notation as in the corresponding paper.
	 */
	public void stage2()
	{
		// Adds together all the randomized c values by the client to compute the final c_j values that the clients will decrypt
		final_random_c = new BigInteger[dataset.size()];
		for(int j=0; j<dataset.size(); j++)
		{
			final_random_c[j] = randomized_c[j][0];
			for(int i=1; i<numClients; i++)
			{
				final_random_c[j] = paillier.add(randomized_c[j][i-1], randomized_c[j][i]);
			}
			final_random_c[j] = paillier.randomize(final_random_c[j]);
		}
	}
	
	/** Send the final randomized c values to the clients
	 * @return The final randomized c values
	 */
	public BigInteger[] sendRandomc()
	{
		return final_random_c;
	}
	
	/** Receive the decryption shares of the final randomized c values
	 * @param shares Decryption shares
	 * @param client Id of the client
	 */
	public void receiveShares(PartialDecryption shares[], int client)
	{
		for(int j=0; j<dataset.size(); j++)
		{
			dec_shares[j][client] = shares[j];
		}
	}
	
	/**
	 * Third stage of the server. The comments use the same latex notation as in the corresponding paper.
	 */
	public void stage3()
	{
		// Computes D(c_j) <- Comb(sh_{i,j}, ..., sh_{t-1,j})
		for(int j=0; j<dataset.size(); j++)
		{
			dec[j] = paillier.combineShares(dec_shares[j]);
		}
		
		// If D(c_j) = 0, add the corresponding y_j to the intersection
		for(int j=0; j<dataset.size(); j++)
		{
			if(dec[j] == BigInteger.ZERO)
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
