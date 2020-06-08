package MPSI;

import java.math.BigInteger;
import java.util.ArrayList;

import paillierp.PaillierThreshold;
import paillierp.PartialDecryption;
import paillierp.key.PaillierPrivateThresholdKey;

/** Class for the HazayVenkita server
 * @author Michael
 *
 */
public class HazayVenkitaServer extends GenericParty {
	
	private ArrayList<String> intersection;
	private int numClients;
	private PaillierThreshold paillier;
	private BigInteger modulus;
	private BigInteger coefficients[][];
	private BigInteger combined_coef[];
	private OPE ope;
	private int random_bits;
	private BigInteger evaluations_enc[];
	private PartialDecryption evaluations_shares[][];
	private BigInteger evaluations[];

	/** Initializes the HazayVenkita server
	 * @param numClients Number of clients
	 * @param dataset Dataset of the server
	 * @param private_key Private key of the server
	 * @param random_bits Number of bits for randomization
	 */
	public HazayVenkitaServer(int numClients, ArrayList<String> dataset, PaillierPrivateThresholdKey private_key, int random_bits)
	{
		super(0, dataset, private_key);
		
		this.intersection = new ArrayList<String>();
		this.numClients = numClients;
		paillier = new PaillierThreshold(private_key.getPublicKey());
		paillier.setDecryptEncrypt(private_key);
		this.modulus = private_key.getPublicKey().getN();
		coefficients = new BigInteger[numClients][];
		ope = new OPE(modulus);
		this.random_bits = random_bits;
		evaluations_shares = new PartialDecryption[numClients][];
	}
	
	public void initialize()
	{
		return;
	}
	
	/** Receive the encrypted coefficients of the clients
	 * @param coef Array of encrypted coefficients
	 * @param client Id of the sending client
	 */
	public void receiveEncCoef(BigInteger coef[], int client)
	{
		coefficients[client] = coef;
	}
	
	/**
	 * First stage of the server. Combining the client polynomials into one and evaluating for each element of the server dataset
	 */
	public void stage1()
	{
		// Finding the maximum degree of the encrypted client polynomials
		int max = 0;
		for(int i=0; i<coefficients.length; i++)
		{
			if(coefficients[i].length > max)
				max = coefficients[i].length;
		}
		
		// Initialize the coefficients of the combined polynomial
		combined_coef = new BigInteger[max];
		for(int i=0; i<max; i++)
			combined_coef[i] = paillier.encrypt(BigInteger.ZERO);
		
		// Add each coefficient to the combined coefficient
		for(int i=0; i<coefficients.length; i++)
		{
			for(int j=0; j<coefficients[i].length; j++)
			{
				combined_coef[j] = paillier.add(combined_coef[j], coefficients[i][j]);
			}
		}

		// Compute the evaluations of the combined polynomial with the elements of the server dataset
		evaluations_enc = ope.evaluate(paillier, combined_coef, dataset, random_bits);
	}
	
	/** Send the encrypted evaluation of each element of the server dataset
	 * @return Array of evaluations
	 */
	public BigInteger[] sendEvaluationsEnc()
	{
		return evaluations_enc;
	}
	
	/** Receive the decryption shares of the evaluations by the clients
	 * @param evaluationsShares The decryption shares
	 * @param client Id of the sending client
	 */
	public void receiveEvaluationsShares(PartialDecryption[] evaluationsShares, int client)
	{
		this.evaluations_shares[client] = evaluationsShares;
	}
	
	/**
	 * Second stage of the server. Combining the decryption shares and computing the intersection.
	 */
	public void stage2()
	{
		// Combining the received decryption shares of the evaluations
		evaluations = new BigInteger[evaluations_enc.length];
		for(int i=0; i<evaluations_shares[0].length; i++)
		{
			PartialDecryption shares[] = new PartialDecryption[numClients];
			
			for(int j=0; j<numClients; j++)
			{
				shares[j] = evaluations_shares[j][i];
			}
			
			evaluations[i] = paillier.combineShares(shares).mod(modulus);
		}
		
		// If the decrypted evaluation is zero, then add that element to the intersection
		for(int i=0; i<evaluations.length; i++)
		{
			if(evaluations[i].equals(BigInteger.ZERO))
				intersection.add(dataset.get(i));
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
