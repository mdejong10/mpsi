package MPSI;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

import paillierp.PaillierThreshold;

/** Class for oblivious polynomial evaluation
 * @author Michael
 *
 */
public class OPE {
	public int DEGREE;
	private BigInteger coefficients[];
	private BigInteger modulus;
	
	/** Construct the OPE by setting the modulus. The coefficients of the polynomial will be modulo this modulus.
	 * @param modulus
	 */
	public OPE(BigInteger modulus)
	{		
		this.modulus = modulus;
	}
	
	/** Hashes the element, which is based on the hashCode() of the element.
	 * @param element Element to hash
	 * @return Hash of the element, an integer between 0 and modulus-1.
	 */
	public BigInteger hash(String element)
	{ 
		return (BigInteger.valueOf(element.hashCode())).mod(modulus);
	}
	
	/** Generates a polynomial of the given dataset by interpolation.
	 * Each element of the dataset is a root in the computed polynomial.
	 * @param dataset
	 */
	public void interpolate(ArrayList<String> dataset)
	{
		DEGREE = dataset.size();
		Polynomial p = new Polynomial(BigInteger.ONE, 0, modulus);
		
		for(int i=0; i<DEGREE; i++)
		{
        	String item = dataset.get(i);
        	BigInteger root = hash(item);
        	Polynomial r = new Polynomial(root,0,modulus);
        	Polynomial x = new Polynomial(BigInteger.ONE, 1, modulus);
        	p = p.times(x.minus(r));
        }
		
		coefficients = p.coefficients();
	}
	
	/** Encrypt the polynomial by encrypting each coefficient
	 * @param paillier PaillierThreshold object needed for encryption
	 */
	public void encrypt(PaillierThreshold paillier)
	{
		for(int i = 0; i < coefficients.length; i++)
		{
			coefficients[i] = paillier.encrypt(coefficients[i]);
		}
	}
	
	/** Computes an evaluation of each element in the dataset of the polynomial represented by the given coefficients
	 * @param paillier PaillierThreshold object needed for encryption
	 * @param coef Array of the given coefficients in ascending order by degree
	 * @param dataset Dataset containing the elements
	 * @param randomBits Number of bits for randomization
	 * @return Array of evaluations for each element
	 */
	public BigInteger[] evaluate(PaillierThreshold paillier, BigInteger coef[], ArrayList<String> dataset, int randomBits)
	{
		Random rand = new Random();
		BigInteger result[] = new BigInteger[dataset.size()];
		
		for(int i=0; i<dataset.size(); i++)
        {
        	String item = dataset.get(i);
        	BigInteger root = hash(item);
        	
        	BigInteger r = new BigInteger(randomBits, rand);
        	
        	// Compute the evaluation efficiently using Horner's rule
        	int deg = coef.length-1;
        	result[i] = coef[deg];
        	for(int d=deg-1; d>=0; d--)
        	{
        		result[i] = paillier.multiply(result[i], root);
        		result[i] = paillier.add(result[i], coef[d]);
        	}
        	
        	// Randomize the result
        	result[i] = paillier.multiply(result[i], r);
        }
		 
		return result;
	}
	
	/** Get the coefficients of this polynomial
	 * @return The coefficients
	 */
	public BigInteger[] coefficients()
	{
		return coefficients;
	}
}
