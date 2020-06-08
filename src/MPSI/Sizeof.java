package MPSI;

import java.math.BigInteger;
import java.util.ArrayList;

import paillierp.PartialDecryption;

/** Class for approximating the size of an object
 * @author Michael
 *
 */
final public class Sizeof {

    /** Approximates the size of a given object
     * @param obj Object
     * @return size of the object in bytes
     */
    public static long sizeof(Object obj){
    	if(obj instanceof BigInteger)
    	{
    		BigInteger bi = (BigInteger) obj;
    		return bi.toString().length();
    	}
    	else if(obj instanceof PartialDecryption)
    	{
    		PartialDecryption pd = (PartialDecryption) obj;
    		return sizeof(pd.getDecryptedValue());
    	}
    	else if(obj instanceof BigInteger[])
    	{
    		BigInteger array[] = (BigInteger[]) obj;
    		long result = 0;
    		
    		for(int i=0; i<array.length; i++)
    			result += sizeof(array[i]);
    		
    		return result;
    	}
    	else if(obj instanceof PartialDecryption[])
    	{
    		PartialDecryption array[] = (PartialDecryption[]) obj;
    		long result = 0;
    		
    		for(int i=0; i<array.length; i++)
    			result += sizeof(array[i]);
    		
    		return result;
    	}
    	else if(obj instanceof ArrayList<?>)
    	{
    		ArrayList<?> array = (ArrayList<?>) obj;
    		long result = 0;
    		
    		for(int i=0; i<array.size(); i++)
    			result += sizeof(array.get(i));
    		
    		return result;
    	}

        return 0;
    }
}