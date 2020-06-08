package MPSI;

/** Class for measuring the execution time
 * @author Michael
 *
 */
public class Performance {
	private long execution[];
	private long init[];
	private long start[];
	
	/** Constructs timing arrays for holding the execution time
	 * @param num_parties Number of parties
	 */
	public Performance(int num_parties)
	{
		execution = new long[num_parties];
		init = new long[num_parties];
		start = new long[num_parties];
	}
	
	/** Starts a timer for a given client
	 * @param client Id of the client
	 */
	public void start(int client)
	{
		start[client] = System.nanoTime();
	}
	
	/** Stops the timer for a given client and adds the time difference to the initialization time for that client
	 * @param client Id of the client
	 */
	public void stop_init(int client)
	{
		long now = System.nanoTime();
		init[client] += ( (now-start[client])/1000000 );
	}
	
	/** Stops the timer for a given client and adds the time difference to the regular execution time for that client
	 * @param client Id of the client
	 */
	public void stop(int client)
	{
		long now = System.nanoTime();
		execution[client] += ( (now-start[client])/1000000 );
	}
	
	/** Gets the measurement results
	 * @return A list of statistics for each client. For each client the total initialization and execution time.
	 */
	public long[][] getStats()
	{
		long stats[][] = new long[execution.length][2];
		
		for (int i=0; i<execution.length; i++)
		{
			stats[i][0] = init[i];
			stats[i][1] = execution[i];
		}
		
		return stats;
	}
}
