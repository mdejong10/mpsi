package MPSI;
import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;

/** Class for showing the computational complexity graph
 * @author Michael
 *
 */
public class GraphComputational extends ApplicationFrame {
	private ProtocolStats stats[];

	/**
	 * @param title Title of the graph
	 * @param label Label of the x axis
	 * @param stats List of measurement data for each protocol
	 */
	public GraphComputational(String title, String label, ProtocolStats stats[]) {
		super(title);
		
		this.stats = stats;
		
		JFreeChart lineChart = ChartFactory.createLineChart(title, label, "Execution time in ms", createDataset(),
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
				String key = "" + stats[j].computational_setup_server.get(i).key;
				long val = stats[j].computational_setup_server.get(i).value + stats[j].computational_interactive_server.get(i).value;
				dataset.addValue(val, names[j]+" Server", key);
				
				key = "" + stats[j].computational_setup_client.get(i).key;
				val = stats[j].computational_setup_client.get(i).value + stats[j].computational_interactive_client.get(i).value;
				dataset.addValue(val, names[j]+" Client", key);
			}
		}

		return dataset;
	}
}
