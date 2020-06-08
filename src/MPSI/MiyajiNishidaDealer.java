package MPSI;
import java.math.BigInteger;
import java.util.ArrayList;

import paillierp.PaillierThreshold;
import paillierp.key.PaillierPrivateThresholdKey;

/** Class for the MiyajiNishida Dealer
 * @author Michael
 *
 */
public class MiyajiNishidaDealer {
	
	private int numParties;
	private PaillierThreshold paillier;
	private ArrayList<ArrayList<BigInteger>> EBFs;
	private BigInteger EIBF[];
	
	/** Initializes the MiyajiNishida dealer
	 * @param numParties Number of parties
	 * @param private_key Private key of the dealer
	 */
	public MiyajiNishidaDealer(int numParties, PaillierPrivateThresholdKey private_key)
	{
		this.numParties = numParties;
		paillier = new PaillierThreshold(private_key.getPublicKey());
		paillier.setDecryptEncrypt(private_key);
		EBFs = new ArrayList<ArrayList<BigInteger>>();
	}
	
	/** Receive the EBF of a party
	 * @param EBF The EBF received
	 */
	public void receiveEBF(ArrayList<BigInteger> EBF)
	{
		EBFs.add(EBF);
	}
	
	/**
	 * First stage of the dealer. The comments use the same latex notation as in the corresponding paper.
	 */
	public void stage1()
	{
		// Initialize the IBF, which is the combined EBF of all parties
		int bloomSize = EBFs.get(0).size();
		EIBF = new BigInteger[bloomSize];
		for(int i=0; i<bloomSize; i++)
		{
			EIBF[i] = paillier.encrypt(BigInteger.ZERO);
		}
		
		// Add all EBFs to the EIBF, where EBF = Enc_y(BF_{m,k}(S_i)) and EIBF = Enc_y(IBF_{m,k}(U S_i))
		// Note that EIBF is not the encrypted inverted bloomfilter as in the NewMPSI protocol!
		// It is the encrypted integrated bloomfilter
		for(int i=0; i<EBFs.size(); i++)
		{
			ArrayList<BigInteger> EBF = EBFs.get(i);
			for(int j=0; j<bloomSize; j++)
			{
				EIBF[j] = paillier.add(EIBF[j], EBF.get(j));
			}
		}
		
		// Computes the n-subtraction of the IBF by subtracting Enc_y(-n) from each entry
		// in the EIBF and then rerandomizing the result
		BigInteger encN = paillier.encrypt(paillier.getPublicKey().getNSPlusOne().subtract(BigInteger.valueOf(numParties)));
		for(int j=0; j<bloomSize; j++)
		{
			EIBF[j] = paillier.add(encN, EIBF[j]);
			EIBF[j] = paillier.randomize(EIBF[j]);
		}
	}
	
	/** Send the combined EBF
	 * @return The combined EBF
	 */
	public BigInteger[] sendCombined()
	{
		return EIBF;
	}
}
