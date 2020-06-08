package MPSI;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/** Class for representing measurement results for a specific protocol
 * @author Michael
 *
 */
public class ProtocolStats {
	public ArrayList<DataPoint> computational_setup_client;
	public ArrayList<DataPoint> computational_setup_server;
	public ArrayList<DataPoint> computational_interactive_client;
	public ArrayList<DataPoint> computational_interactive_server;
	public ArrayList<DataPoint> computational_combined_client;
	public ArrayList<DataPoint> computational_combined_server;
	public ArrayList<DataPoint> communicational_client;
	public ArrayList<DataPoint> communicational_server;
	
	public ProtocolStats()
	{
		computational_setup_client = new ArrayList<DataPoint>();
		computational_setup_server = new ArrayList<DataPoint>();
		computational_interactive_client = new ArrayList<DataPoint>();
		computational_interactive_server = new ArrayList<DataPoint>();
		computational_combined_client = new ArrayList<DataPoint>();
		computational_combined_server = new ArrayList<DataPoint>();
		communicational_client = new ArrayList<DataPoint>();
		communicational_server = new ArrayList<DataPoint>();
	}
	
	public void writeFiles(String name) throws IOException
	{
		for (int i=0; i < computational_setup_client.size(); i++)  
		{
			long key = computational_setup_client.get(i).key;
			long val_client = computational_interactive_client.get(i).value + computational_setup_client.get(i).value;
			long val_server = computational_interactive_server.get(i).value + computational_setup_server.get(i).value;
			
			computational_combined_client.add(new DataPoint(key, val_client));
			computational_combined_server.add(new DataPoint(key, val_server));
		}
		
		writeFile(name+"_computational_setup_client.txt", computational_setup_client);
		writeFile(name+"_computational_setup_server.txt", computational_setup_server);
		writeFile(name+"_computational_interactive_client.txt", computational_interactive_client);
		writeFile(name+"_computational_interactive_server.txt", computational_interactive_server);
		writeFile(name+"_computational_combined_client.txt", computational_combined_client);
		writeFile(name+"_computational_combined_server.txt", computational_combined_server);
		writeFile(name+"_communicational_client.txt", communicational_client);
		writeFile(name+"_communicational_server.txt", communicational_server);
	}
	
	private void writeFile(String filename, ArrayList<DataPoint> data) throws IOException {
		FileWriter fw = new FileWriter(filename);
		 
		for (int i=0; i < data.size(); i++)  
		{
			fw.write("(" + data.get(i).key + "," + data.get(i).value + ")");
		}
	 
		fw.close();
	}
}

/** Auxiliary class for representing data points
 * @author Michael
 *
 */
class DataPoint {
	public long key;
	public long value;
	
	public DataPoint(long key, long value)
	{
		this.key = key;
		this.value = value;
	}
}