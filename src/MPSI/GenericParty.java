package MPSI;
import java.util.ArrayList;

import paillierp.key.PaillierPrivateThresholdKey;

/** Abstract class for representing a generic MPSI party
 * @author Michael
 *
 */
public abstract class GenericParty {
	protected int id;
	protected ArrayList<String> dataset;
	protected PaillierPrivateThresholdKey private_key;
	
	/** Constructs a generic MPSI party
	 * @param id Id of the party
	 * @param dataset Dataset of the party
	 * @param private_key Private key of the party
	 */
	public GenericParty(int id, ArrayList<String> dataset, PaillierPrivateThresholdKey private_key)
	{
		this.id = id;
		this.private_key = private_key;
		this.dataset = dataset;
	}
	
	/**
	 * Initialization stage of the party
	 */
	public abstract void initialize();
}
