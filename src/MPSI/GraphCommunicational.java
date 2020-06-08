package MPSI;
import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;

/** Class for showing the communicational complexity graph
 * @author Michael
 *
 */
public class GraphCommunicational extends ApplicationFrame {
	private ProtocolStats stats[];

	/**
	 * @param title Title of the graph
	 * @param label Label of the x axis
	 * @param stats List of measurement data for each protocol
	 */
	public GraphCommunicational(String title, String label, ProtocolStats stats[]) {
		super(title);
		
		this.stats = stats;
		
		JFreeChart lineChart = ChartFactory.createLineChart(title, label, "KB sent + received", createDataset(),
				PlotOrientation.VERTICAL, true, true, false);
		lineChart.setBackgroundPaint(new Color(255, 255, 255));

		ChartPanel chartPanel = new ChartPanel(lineChart);
		chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
		setContentPane(chartPanel);
	}

	private DefaultCategoryDataset createDataset() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		String names[] = {"New", "M&N", "H&V"};
		
		for(int j=0; j<3; j++)
		{
			for(int i=0; i<stats[j].communicational_server.size(); i++)
			{
				String key = "" + stats[j].communicational_server.get(i).key;
				long val = stats[j].communicational_server.get(i).value;
				dataset.addValue(val, names[j]+" Server", key);
				
				key = "" + stats[j].communicational_client.get(i).key;
				val = stats[j].communicational_client.get(i).value;
				dataset.addValue(val, names[j]+" Client", key);
			}
		}

		return dataset;
	}
}
