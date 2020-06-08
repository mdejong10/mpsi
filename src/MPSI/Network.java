package MPSI;

/** Class for measuring the network communication
 * @author Michael
 *
 */
public class Network {

	private long sent[][];
	
	/** Constructs counting array for holding the sent bytes
	 * @param num_parties Number of parties
	 */
	public Network(int num_parties)
	{
		sent = new long[num_parties][num_parties];
	}
	
	/** Adds the amount of bytes needed to send the given object from the sender to the receiver
	 * @param from Id of sender
	 * @param to Id of sender
	 * @param obj Object to send
	 */
	public void send(int from, int to, Object obj)
	{
		long size = Sizeof.sizeof(obj);
		sent[from][to] += size;
	}
	
	/** Get the total sent bytes for the given client
	 * @param id Id of the client
	 * @return Amount of KB sent by that client
	 */
	public long getTotalSent(int id)
	{
		long total = 0;
		for (int i = 0; i < sent[id].length; i++)
		{
			total += sent[id][i];
		}
		return total/1000;
	}
	
	/** Get the total received bytes for the given client
	 * @param id Id of the client
	 * @return Amount of KB received by that client
	 */
	public long getTotalReceived(int id)
	{
		long total = 0;
		for (int i = 0; i < sent.length; i++)
		{
			total += sent[i][id];
		}
		return total/1000;
	}
	
	/** Gets the measurement results
	 * @return A list of statistics for each client. For each client the total sent and received bytes.
	 */
	public long[][] getStats()
	{
		long stats[][] = new long[sent.length][2];
		
		for (int i = 0; i < sent.length; i++)
		{
			long totSent = getTotalSent(i);
			long totRecv = getTotalReceived(i);
			stats[i][0] = totSent;
			stats[i][1] = totRecv;
		}
		
		return stats;
	}
}
