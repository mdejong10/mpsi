package MPSI;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;

import paillierp.PaillierThreshold;

/** Class to represent bloomfilters given number of bits SIZE and number of hash functions K
 * @author Michael
 *
 */
public class Bloomfilter {
	public int SIZE;
	public int K;
	
	private BitSet hashes;
	
	/** Constructs a new bloomfilter given the parameters
	 * @param size number of bits in the bit array
	 * @param k number of hash functions
	 */
	public Bloomfilter(int size, int k)
	{
		this.SIZE = size;
		this.K = k;
		hashes = new BitSet(size);
	}

	/** Encrypts the bloomfilter by encrypting the inversion of each bit in the bloomfilter
	 * @param paillier PaillierThreshold object needed for encryption
	 * @return a list of the inverted encrypted entries
	 */
	public ArrayList<BigInteger> invertEncrypt(PaillierThreshold paillier)
	{
		ArrayList<BigInteger> result = new ArrayList<BigInteger>();
		
		for(int i = 0; i < SIZE; i++)
		{
			if(getBit(i))
				result.add(paillier.encrypt(BigInteger.ZERO));
			else
				result.add(paillier.encrypt(BigInteger.ONE));
		}
		
		return result;
	}
	
	/** Encrypts the bloomfilter by encrypting each bit in the bloomfilter
	 * @param paillier PaillierThreshold object needed for encryption
	 * @return a list of the encrypted entries
	 */
	public ArrayList<BigInteger> encrypt(PaillierThreshold paillier)
	{
		ArrayList<BigInteger> result = new ArrayList<BigInteger>();
		
		for(int i = 0; i < SIZE; i++)
		{
			if(getBit(i))
				result.add(paillier.encrypt(BigInteger.ONE));
			else
				result.add(paillier.encrypt(BigInteger.ZERO));
		}
		
		return result;
	}
	
	/** Initializes the entries in the bit array
	 * @param bits Array of bits
	 */
	public void initialize(BigInteger bits[])
	{
		hashes.clear();
		
		for(int i = 0; i < SIZE; i++)
		{
			if(bits[i].compareTo(BigInteger.ONE) == 0)
				hashes.set(i);
		}
	}
	
	/** Checks whether an element is in the bloomfilter
	 * @param element Element to check
	 * @return True if the element is found, else otherwise
	 */
	public boolean check(String element)
	{
		boolean inSet = true;
		
		for(int i = 0; i < K && inSet; i++)
		{
			if(!getBit(hash(element, i)))
				inSet = false;
		}
		
		return inSet;
	}
	
	/** Inserts an element into the bloomfilter
	 * @param element Element to insert
	 */
	public void insert(String element)
	{
		for(int i = 0; i < K; i++)
	         hashes.set(hash(element, i));
	}
	
	/** Hashes the element with the ith hash function.
	 * Hashing is based on the hashCode() of the element
	 * with i preprended to it.
	 * @param element Element to hash
	 * @param i Index of the hash function
	 * @return ith hash of the element, an integer between 0 and SIZE-1.
	 */
	public int hash(String element, int i)
	{
		element = i + element;
		return Math.abs((int)(element.hashCode()%SIZE));
	}
	
	/** Gets the entry of the bit array at the given index
	 * @param index Index in bit array
	 * @return True if the bit at the index is 1, else otherwise
	 */
	public boolean getBit(int index)
	{
		return hashes.get(index);
	}
}
