package MPSI;
import java.util.ArrayList;
import java.util.List;

/** Class for the HazayVenkita protocol
 * @author Michael
 *
 */
public class HazayVenkitaMPSI extends GenericMPSI {
	
	private HazayVenkitaServer server;
	private List<HazayVenkitaClient> clients;
	private int random_bits;
	
	/** Initializes the NewMPSI protocol
	 * @param datasets List of datasets for each party
	 * @param network Network measurement
	 * @param performance Performance measurement
	 * @param prime_bits Specifies the number of bits required for the prime factor of n
	 * @param num_parties Number of parties
	 * @param random_bits Number of bits for randomization
	 */
	public HazayVenkitaMPSI(ArrayList<ArrayList<String>> datasets, Network network, Performance performance, int prime_bits, int num_parties, int random_bits)
	{
		super(datasets, network, performance, prime_bits, num_parties, num_parties-1);
		
		this.random_bits = random_bits;
	}
	
	/**
	 * Initializes the protocol. Creates the server and the clients. For each client the initialization step is performed.
	 */
	@Override
	public void initialize() 
	{
		server = new HazayVenkitaServer(num_parties-1, datasets.get(num_parties-1), keys.getPrivate(0), random_bits);
		
		clients = new ArrayList<HazayVenkitaClient>();
		for(int i=1; i<num_parties; i++)
		{
			HazayVenkitaClient c = new HazayVenkitaClient(i, datasets.get(i), keys.getPrivate(i));
			clients.add(c);
			
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
		
		// Each client performs its first stage, which is to encrypt its polynomial
		for(int i=0; i<num_clients; i++)
		{
			performance.start(i+1);
			clients.get(i).stage1();
			performance.stop(i+1);
		}
		
		// Each client sends its encrypted polynomial coefficients to the server
		for(int i=0; i<num_clients; i++)
		{
			server.receiveEncCoef(clients.get(i).sendEncCoef(), i);
			network.send(i+1, 0, clients.get(i).sendEncCoef());
		}
		
		// The server performs its first stage, which is to combine the client polynomials into one and evaluate for each element of the server dataset
		performance.start(0);
		server.stage1();
		performance.stop(0);
		
		// The server sends the evaluations to the client
		for(int i=0; i<num_clients; i++)
		{
			clients.get(i).receiveEvaluationsEnc(server.sendEvaluationsEnc());
			network.send(0, i+1, server.sendEvaluationsEnc());
		}
		
		// The client performs its second stage, which is to compute the decryption shares of the evaluations
		for(int i=0; i<num_clients; i++)
		{
			performance.start(i+1);
			clients.get(i).stage2();
			performance.stop(i+1);
		}
		
		// The clients send the decryption shares of the evaluations back to the server
		for(int i=0; i<num_clients; i++)
		{
			server.receiveEvaluationsShares(clients.get(i).sendEvaluationsShares(), i);
			network.send(i+1, 0, clients.get(i).sendEvaluationsShares());
		}
		
		// The server performs its second stage, which is to combine the decryption shares and compute the intersection. 
		performance.start(0);
		server.stage2();
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
