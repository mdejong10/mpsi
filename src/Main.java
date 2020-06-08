import MPSI.*;

import java.io.IOException;

import org.jfree.ui.RefineryUtilities;

/**
 * Analyzes and compares the communicational and computational performance
 * of three MPSI protocols with varying number of parties and dataset sizes
 * @author Michael
 *
 */
public class Main {
		
	public static void main(String[] args) throws IOException
	{	
		Measurements m = new Measurements(3, 7, 1, 300, 600, 50);
		m.start();
		ProtocolStats stats[][] = m.getStats();
		
		// Write measurement results to files
		/*stats[0][0].writeFiles("measurement/newMPSI_party");
		stats[0][1].writeFiles("measurement/M&N_party");
		stats[0][2].writeFiles("measurement/H&V_party");
		stats[1][0].writeFiles("measurement/newMPSI_set");
		stats[1][1].writeFiles("measurement/M&N_set");
		stats[1][2].writeFiles("measurement/H&V_set");*/
		
		// Graph for varying party sizes
		GraphCommunicational g1 = new GraphCommunicational("Communicational complexity", "Number of parties", stats[0]);
		g1.setSize(800, 600);
		RefineryUtilities.centerFrameOnScreen(g1);
		g1.setVisible(true);
		
		GraphComputational g2 = new GraphComputational("Computational complexity", "Number of parties", stats[0]);
		g2.setSize(800, 600);
		RefineryUtilities.centerFrameOnScreen(g2);
		g2.setVisible(true);
		
		// Graph for varying set sizes
		GraphCommunicational g3 = new GraphCommunicational("Communicational complexity", "Number of set elements", stats[1]);
		g3.setSize(800, 600);
		RefineryUtilities.centerFrameOnScreen(g3);
		g3.setVisible(true);
		
		GraphComputational g4 = new GraphComputational("Computational complexity", "Number of set elements", stats[1]);
		g4.setSize(800, 600);
		RefineryUtilities.centerFrameOnScreen(g4);
		g4.setVisible(true);
		
		//m.test();
	}
}