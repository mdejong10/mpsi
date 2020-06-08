package MPSI;
import java.util.Random;

import paillierp.key.KeyGen;
import paillierp.key.PaillierPrivateThresholdKey;

/** Class for generating threshold Paillier private keys
 * @author Michael
 *
 */
public class KeyGenerator {
	private PaillierPrivateThresholdKey private_keys[];
	
	/** Generates threshold Paillier private keys
	 * @param prime_bits Specifies the number of bits required for the prime factor of n
	 * @param numKeys Number of keys to generate
	 * @param dec_threshold Least number of decryption servers required for shared decryption
	 */
	public KeyGenerator(int prime_bits, int numKeys, int dec_threshold)
	{
		Random rnd = new Random();
		private_keys = KeyGen.PaillierThresholdKey(prime_bits, numKeys, dec_threshold, rnd.nextLong());
	}
	
	/** Gets the private key at the given index
	 * @param key The index in the list of keys
	 * @return The private key
	 */
	public PaillierPrivateThresholdKey getPrivate(int key)
	{
		return private_keys[key];
	}
}
