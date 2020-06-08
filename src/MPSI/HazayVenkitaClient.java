package MPSI;
import java.math.BigInteger;
import java.util.ArrayList;

import paillierp.PaillierThreshold;
import paillierp.PartialDecryption;
import paillierp.key.PaillierPrivateThresholdKey;

/** Class for the HazayVenkita client
 * @author Michael
 *
 */
public class HazayVenkitaClient extends GenericParty {
	private PaillierThreshold paillier;
	private OPE p;
	
	private BigInteger modulus;
	private BigInteger evaluations_enc[];
	private PartialDecryption evaluations_shares[];
	
	/** Initializes the HazayVenkita client
	 * @param id Id of this client
	 * @param dataset Dataset of this client
	 * @param private_key Private key of this client
	 */
	public HazayVenkitaClient(int id, ArrayList<String> dataset, PaillierPrivateThresholdKey private_key)
	{
		super(id, dataset, private_key);
		
		paillier = new PaillierThreshold(private_key.getPublicKey());
		paillier.setDecryptEncrypt(private_key);
	
		this.modulus = private_key.getPublicKey().getN();
	}
	
	/**
	 * Generates the polynomial by interpolation
	 */
	public void initialize()
	{
		p = new OPE(modulus);
		p.interpolate(dataset);
	}
	
	/**
	 * First stage of the client. Encrypting the polynomial
	 */
	public void stage1()
	{
		p.encrypt(paillier);
	}
	
	/** Send the encrypted coefficients of the polynomial
	 * @return An array of coefficients in ascending order of degree
	 */
	public BigInteger[] sendEncCoef()
	{
		return p.coefficients();
	}
	
	/** Receive the encrypted evaluation of each element of the server dataset
	 * @param evaluationsEnc Array of evaluations
	 */
	public void receiveEvaluationsEnc(BigInteger[] evaluationsEnc)
	{
		this.evaluations_enc = evaluationsEnc;
	}
	
	/**
	 * Second stage of the client. Computing the decryption shares of the evaluations
	 */
	public void stage2()
	{
		evaluations_shares = new PartialDecryption[evaluations_enc.length];
		
		for(int i=0; i<evaluations_shares.length; i++)
		{
			evaluations_shares[i] = paillier.decrypt(evaluations_enc[i]);
		}
	}
	
	/** Send the decryption shares of the evaluations
	 * @return The decryption shares
	 */
	public PartialDecryption[] sendEvaluationsShares()
	{
		return evaluations_shares;
	}
	
}
