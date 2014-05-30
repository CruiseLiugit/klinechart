package model.chart;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SegmentedTimeline;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.Align;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RefineryUtilities;

import util.Constants;
import util.TimeTools;

import model.kline.KLineDataSet;

public class XYAreaChartDemo4  extends ApplicationFrame{

		// 自定义的数据模型，线上实时获取的数据模型
		static KLineDataSet klineset = new KLineDataSet();
		
		static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
	    static double highValue = Double.MIN_VALUE;//设置K线数据当中的最大值
	    static double minValue = Double.MAX_VALUE;//设置K线数据当中的最小值
	    
	    
	    /**
		 * 创建一个 XY Area 折线分时图
		 * 
		 * @param title
		 *            the frame title.
		 */
		public XYAreaChartDemo4(String title) {
			super(title);
			JPanel chartPanel = createDemoPanel();
			chartPanel.setPreferredSize(new java.awt.Dimension(
					Constants.ChartPanel_WIDTH, Constants.ChartPanel_HEIGHT));
			setContentPane(chartPanel);
		}
		
		/**
		 * Creates a chart.
		 * 
		 * @param dataset
		 *            the dataset.
		 * 
		 * @return The dataset.
		 */
		private static JFreeChart createChart(XYDataset dataset) {
			//定义 图形 渲染器
			XYAreaRenderer renderer = new XYAreaRenderer(XYAreaRenderer.AREA);
			// 工具栏生成
			// public static final String DEFAULT_TOOL_TIP_FORMAT =
			// "{0}: ({1}, {2})";
			// 设置 x y 轴的数据格式
			renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator(
					StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
					new SimpleDateFormat("HH:mm"), new DecimalFormat("0.00")));

			
			 //设置x轴，也就是时间轴
		     DateAxis x1Axis=new DateAxis();
		     //设置不采用自动设置时间范围	     
		     x1Axis.setAutoRange(false);
		     try{
		    	 	//设置时间范围，注意时间的最大值要比已有的时间最大值要多一天
		    	 	System.out.println("--x 最早时间 lower :"+klineset.getLower()+"   ,最晚时间 upper :"+klineset.getUpper());
				x1Axis.setRange(klineset.getLower().getTime(),klineset.getUpper().getTime());
		      }catch(Exception e){
		         e.printStackTrace();
		      }
			 
		     //设置不采用自动选择刻度值，这里如果设置 false ，无法显示 X 轴 Date
		     x1Axis.setAutoTickUnitSelection(true);
		     //设置显示刻度表刻度线
		     x1Axis.setAxisLineVisible(true);
		     x1Axis.setAxisLinePaint(new Color(221, 221, 221));// 设置刻度线的颜色
		     //设置标记的位置
		     x1Axis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
		     //设置标准的时间刻度单位
		     x1Axis.setStandardTickUnits(DateAxis.createStandardDateTickUnits());
		     
		     /////////////设定y轴，就是数字轴/////////////
		     NumberAxis y1Axis=new NumberAxis();
		     y1Axis.setTickLabelPaint(Color.red); // Y 坐标刻度值颜色
		     y1Axis.setAutoRange(false);//不不使用自动设定范围
		     
		     //设定y轴值的范围，比最低值要低一些，比最大值要大一些，这样图形看起来会美观些
		     //////////////////////////////////////////////
		     //如果调用网络数据，这里需要动态变化
		     minValue = klineset.getLowValue();
		     highValue = klineset.getHighValue();
		     /////////////////////////////////////////////
		     //调整左侧 Y 轴，取值范围
		     y1Axis.setRange(minValue*0.99, highValue*1.009);
		     
		     //设置刻度显示的密度
		     //y1Axis.setTickUnit(new NumberTickUnit((highValue*1.1-minValue*0.9)/10));
		     
		     XYPlot plot1=new XYPlot(dataset,x1Axis,y1Axis,renderer);//设置画图区域对象
		     
				// //////背景水印////////////
				Image image = null;
				ImageIcon icon = null;
				try {
					// 方法一:直接读入 icon 地址
					image = ImageIO.read(new File("gorilla.jpg"));
				} catch (Exception ex) {
					ex.printStackTrace(System.err);
				}
				plot1.setBackgroundImage(image); // 设置背景图片
				plot1.setBackgroundImageAlignment(Align.BOTTOM_RIGHT);// 设置背景图片位置
				plot1.setBackgroundAlpha(.3f);
				
			
		    
		            
		         JFreeChart chart = new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, plot1, false);     
		      // 修改图片 样式
		 		chart.setBackgroundPaint(Color.white);
		         
			      // 添加子标题
			 		// 添加两个描述文件, 放在图片上面，左右两侧
			 		TextTitle sourceL = new TextTitle("[T]现货白银 3805.00 -28.00 0.73%");
			 		sourceL.setFont(new Font("SansSerif", Font.PLAIN, 10));
			 		sourceL.setPosition(RectangleEdge.TOP);
			 		sourceL.setHorizontalAlignment(HorizontalAlignment.LEFT);

			 		TextTitle sourceR = new TextTitle(TimeTools.getUtilDate2String1(klineset.getUpper()));
			 		sourceR.setFont(new Font("SansSerif", Font.PLAIN, 10));
			 		sourceR.setPosition(RectangleEdge.TOP);
			 		sourceR.setHorizontalAlignment(HorizontalAlignment.RIGHT);

			 		// 添加底部描述文字
			 		chart.addSubtitle(sourceL);
			 		chart.addSubtitle(sourceR);
		         
			 	
			 	 //生成 Swing 窗口，显示报表图
		         ChartFrame frame = new ChartFrame("分时图", chart);
		         frame.pack();
		         frame.setVisible(true);
		     

			// ////////////////////////////////////////////////////////

			try {
				ChartUtilities.writeChartAsPNG(new FileOutputStream(new File(
						"test2.png")), chart, Constants.ChartPanel_WIDTH,
						Constants.ChartPanel_HEIGHT);
			} catch (IOException ex) {
				ex.printStackTrace();
			}

			return chart;
		}
		
		
		
		/**
		 * Creates a panel for the demo (used by SuperDemo.java).
		 * 
		 * @return A panel.
		 */
		public static JPanel createDemoPanel() {
			// ///////////////////////////////////
			// //// 实时调用线上的数据 /////////
			// //////////////////////////////////
			// 自动生成的数据, 8 个小时  480
			// 23 小时 1380 夏季
			// 22 小时 1320 冬季
			JFreeChart chart = createChart(klineset.createXYAreaDataSet(1, 900));

			return new ChartPanel(chart);
		}
		
		/**
		 * Starting point for the demonstration application.
		 * 
		 * @param args
		 *            ignored.
		 */
		public static void main(String[] args) {
			
			XYAreaChartDemo4 demo = new XYAreaChartDemo4("Candlestick Demo");
			demo.pack();
			RefineryUtilities.centerFrameOnScreen(demo);
			demo.setVisible(true);
		}
	
	
}
