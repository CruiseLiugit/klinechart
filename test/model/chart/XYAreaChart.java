package model.chart;

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import javax.swing.JPanel;

import model.kline.KLineDataSet;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.Minute;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class XYAreaChart extends ApplicationFrame {

	/**
	 * Creates a new demo.
	 * 
	 * @param title
	 *            the frame title.
	 */
	public XYAreaChart(String title) {

		super(title);

		KLineDataSet klineset = new KLineDataSet();
		// 得到数据集合
		XYDataset dataset = klineset.createXYAreaDataSet(1, 200);
		//XYDataset dataset = createDataset();

		JFreeChart chart = createChart(dataset);

		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(chartPanel);
	}

	private static XYDataset createDataset() {
		TimeSeries series1 = new TimeSeries("Random 1");
		double value = 0.0;
		RegularTimePeriod start = new Minute();
		for (int i = 0; i < 200; i++) {
			value = Math.random()*10000;
			series1.add(start, value);
			start = (Minute) start.next();
		}

		TimeSeriesCollection dataset = new TimeSeriesCollection(series1);
		return dataset;
	}

	/**
	 * Creates a chart.
	 * 
	 * @param dataset
	 *            the dataset.
	 * 
	 * @return The chart.
	 */
	private static JFreeChart createChart(XYDataset dataset) {
		JFreeChart chart = ChartFactory.createXYAreaChart(null, null, null,
				dataset, PlotOrientation.VERTICAL, true, // legend
				true, // tool tips
				false // URLs
				);
		XYPlot plot = chart.getXYPlot();

		ValueAxis y1Axis = new DateAxis("Time");
		y1Axis.setLowerMargin(0.0);
		y1Axis.setUpperMargin(0.0);
		plot.setDomainAxis(y1Axis);  //轴区域
		plot.setForegroundAlpha(0.5f);

		XYItemRenderer renderer = plot.getRenderer();
		// 工具栏生成
		// public static final String DEFAULT_TOOL_TIP_FORMAT =
		// "{0}: ({1}, {2})";
		// 设置 x y 轴的数据格式
		renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator(
				StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
				new SimpleDateFormat("HH:mm"), new DecimalFormat("0.00")));

		// 修改图片 样式
		chart.setBackgroundPaint(Color.white);

		// 生成 Swing 窗口，显示报表图
		ChartFrame frame = new ChartFrame("Area 图", chart);
		frame.pack();
		frame.setVisible(true);

		return chart;
	}

	/**
	 * Creates a panel for the demo.
	 * 
	 * @return A panel.
	 */
	public static JPanel createDemoPanel() {
		return new ChartPanel(createChart(createDataset()));
	}

	/**
	 * Starting point for the demonstration application.
	 * 
	 * @param args
	 *            ignored.
	 */
	public static void main(String[] args) {

		XYAreaChart demo = new XYAreaChart("XY Area Chart Demo 2");
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);

	}

}
