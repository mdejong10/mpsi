package MPSI;
import java.util.ArrayList;
import java.util.List;

/** Class for the NewMPSI protocol
 * @author Michael
 *
 */
public class NewMPSI extends GenericMPSI {
	
	private NewMPSIServer server;
	private List<NewMPSIClient> clients;
	private int bloom_size;
	private int bloom_k;
	private int random_exponent;
	
	/** Initializes the NewMPSI protocol
	 * @param datasets List of datasets for each party
	 * @param network Network measurement
	 * @param performance Performance measurement
	 * @param prime_bits Specifies the number of bits required for the prime factor of n
	 * @param num_parties Number of parties
	 * @param bloom_size Bloomfilter number of bits
	 * @param bloom_k Bloomfilter number of hashes
	 * @param random_exponent Number of bits for the random exponent when randomizing
	 */
	public NewMPSI(ArrayList<ArrayList<String>> datasets, Network network, Performance performance, int prime_bits, int num_parties, int bloom_size, int bloom_k, int random_exponent)
	{
		super(datasets, network, performance, prime_bits, num_parties, num_parties-1);
		
		this.bloom_size = bloom_size;
		this.bloom_k = bloom_k;
		this.random_exponent = random_exponent;
	}
	
	/** Creates a new bloomfilter
	 * @return The bloomfilter
	 */
	private Bloomfilter newBloomfilter()
	{
		return new Bloomfilter(bloom_size, bloom_k);
	}
	
	/**
	 * Initializes the protocol. Creates the server and the clients. For each client the initialization step is performed.
	 */
	@Override
	public void initialize() 
	{
		server = new NewMPSIServer(num_parties-1, datasets.get(num_parties-1), keys.getPrivate(0), newBloomfilter());
		
		clients = new ArrayList<NewMPSIClient>();
		for(int i=1; i<num_parties; i++)
		{
			NewMPSIClient c = new NewMPSIClient(i, datasets.get(i), keys.getPrivate(i), newBloomfilter(), random_exponent);
			clients.add(c);
			
			// Initialize the client (EIBF generation)
			performance.start(i);
			c.initialize();
			performance.stop_init(i);
		}
	}

	/**
	 * Executes the interactive part of the protocol
	 */
	@Override
	public void execute() 
	{
		int num_clients = num_parties-1;
		
		// The clients have computed their own EIBF and sends it to the server
		for(int i=0; i<num_clients; i++)
		{
			server.receiveEIBF(clients.get(i).sendEIBF());
			network.send(i+1, 0, clients.get(i).sendEIBF());
		}
		
		// Server performs its first stage, which is to compute the c values
		performance.start(0);
		server.stage1();
		performance.stop(0);
		
		// The c values are sent to the client and the client performs its first stage, which is to
		// initialize the ShDec0 algorithm and randomize the c values received by the server by raising
		// it to a random exponent
		for(int i=0; i<num_clients; i++)
		{
			NewMPSIClient client = clients.get(i);
			client.receivec(server.sendc());
			network.send(0, i+1, server.sendc());
			
			performance.start(i+1);
			client.stage1();
			performance.stop(i+1);

		}
		
		// The server receives the randomized c values
		for(int i=0; i<num_clients; i++)
		{
			server.receiveRandomc(clients.get(i).sendRandomc(), i);
			network.send(i+1, 0, clients.get(i).sendRandomc());
		}
		
		// Server performs its first stage, which is to finish the ShDec0 by
		// adding the randomized c values from all clients together and sending
		// them back to the clients for decryption
		performance.start(0);
		server.stage2();
		performance.stop(0);
		
		// The clients receive the final combined randomized c values and
		// compute their decryption shares
		for(int i=0; i<num_clients; i++)
		{
			NewMPSIClient client = clients.get(i);
			client.receiveRandomc(server.sendRandomc());
			network.send(0, i+1, server.sendRandomc());
			
			performance.start(i+1);
			client.stage2();
			performance.stop(i+1);
		}
		
		// The clients send the decryption shares to the server
		for(int i=0; i<num_clients; i++)
		{
			server.receiveShares(clients.get(i).sendShares(), i);
			network.send(i+1, 0, clients.get(i).sendShares());
		}
		
		// Server performs its last stage and combines the decryption shares
		// and computes the set intersection
		performance.start(0);
		server.stage3();
		performance.stop(0);
	}

	/**
	 * Shows the computed set intersection
	 */
	@Override
	public void results() 
	{
		System.out.println("____________________________________________________");
		System.out.println("Set intersection:");
		
		ArrayList<String> intersection = server.getIntersection();
		
		for(int j=0; j<intersection.size(); j++)
			System.out.println(intersection.get(j));
	}
}
