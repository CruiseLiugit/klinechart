package model.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Stroke;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import model.kline.KLineDataSet;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.SegmentedTimeline;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.Day;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.date.DateUtilities;
import org.jfree.ui.Align;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

import util.Constants;
import util.TimeTools;

public class HighLowChartDemo4 extends ApplicationFrame {
	// 自定义的数据模型，线上实时获取的数据模型
	static KLineDataSet klineset = new KLineDataSet();
	
	static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
    static double highValue = Double.MIN_VALUE;//设置K线数据当中的最大值
    static double minValue = Double.MAX_VALUE;//设置K线数据当中的最小值
    //static double high2Value = Double.MIN_VALUE;//设置成交量的最大值
    //static double min2Value = Double.MAX_VALUE;//设置成交量的最低值
    
    static int  time = 0;

	/**
	 * A demonstration application showing a candlestick chart.
	 * 
	 * @param title
	 *            the frame title.
	 */
	public HighLowChartDemo4(String title) {
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
	private static JFreeChart createChart(OHLCSeries series) {
		//保留K线数据的数据集，必须申明为final，后面要在匿名内部类里面用到
		final OHLCSeriesCollection seriesCollection = new OHLCSeriesCollection();
		seriesCollection.addSeries(series);
		
		//设置K线图的画图器，必须申明为final，后面要在匿名内部类里面用到 
		final CandlestickRenderer candlestickRender=new CandlestickRenderer();
		//设置是否使用自定义的边框线，程序自带的边框线的颜色不符合中国股票市场的习惯
		candlestickRender.setUseOutlinePaint(true);
		
		//设置如何对K线图的宽度进行设定
		 candlestickRender.setAutoWidthMethod(CandlestickRenderer.WIDTHMETHOD_AVERAGE);
		 candlestickRender.setAutoWidthGap(0.001);//设置各个K线图之间的间隔
	     candlestickRender.setUpPaint(Color.RED);//设置股票上涨的K线图颜色
	     candlestickRender.setDownPaint(Color.GREEN);//设置股票下跌的K线图颜色
	     
		 //设置x轴，也就是时间轴
	     DateAxis x1Axis=new DateAxis();
	     //设置不采用自动设置时间范围	     
	     x1Axis.setAutoRange(false);
	     try{
	    	 	//设置时间范围，注意时间的最大值要比已有的时间最大值要多一天
	    	 	System.out.println("--x 最早时间 lower :"+klineset.getLower()+"   ,最晚时间 upper :"+klineset.getUpper());
			x1Axis.setRange(klineset.getLower().getTime(),klineset.getUpper().getTime());
	    	 	//2013-12-19 06:00:00
			//2014-02-26 06:00:00
			//x1Axis.setRange(dateFormat.parse("2013-12-19 06:00:00"),dateFormat.parse("2014-02-27 06:00:00"));
	     }catch(Exception e){
	         e.printStackTrace();
	      }
		 

	     //设置时间线显示的规则，用这个方法就摒除掉了周六和周日这些没有交易的日期(很多人都不知道有此方法)，使图形看上去连续
	     x1Axis.setTimeline(SegmentedTimeline.newMondayThroughFridayTimeline());
	     //设置不采用自动选择刻度值，这里如果设置 false ，无法显示 X 轴 Date
	     //////这里差错花了 两天//////////
	     x1Axis.setAutoTickUnitSelection(true);
	     //设置显示刻度表刻度线
	     x1Axis.setAxisLineVisible(true);
	     x1Axis.setAxisLinePaint(new Color(221, 221, 221));// 设置刻度线的颜色
	     //设置标记的位置
	     x1Axis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
	     //设置标准时区的时间刻度单位
	     x1Axis.setStandardTickUnits(DateAxis.createStandardDateTickUnits());
	     //设置时间刻度的间隔，一般以周为单位，这里添加上去，无法显示 X 轴 Date
	     ////////这一句，加上也无法显示  日期轴
	     //x1Axis.setTickUnit(new DateTickUnit(DateTickUnitType.DAY,7));
	     //设置显示时间的格式
	     switch (time) {
			case 4:
				System.out.println("----查看日 K 线，时间格式");
				x1Axis.setDateFormatOverride(new SimpleDateFormat("yyyy-MM-dd"));
				break;
			default:
				System.out.println("----查看 30 分钟、5 分钟 K 线，时间格式");
				x1Axis.setDateFormatOverride(new SimpleDateFormat("HH:mm:ss"));
				break;
			}
	     
	     
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
	     
	     XYPlot plot1=new XYPlot(seriesCollection,x1Axis,y1Axis,candlestickRender);//设置画图区域对象
	     //Stroke stroke = new BasicStroke(0.4f, BasicStroke.CAP_BUTT, BasicStroke.CAP_SQUARE, 0.0f, new float[] { 2.0f, 1.0f }, 1.0f);
	     //plot1.setRangeGridlineStroke(stroke);

	     
			// //////背景水印////////////
			Image image = null;
			ImageIcon icon = null;
			try {
				// 方法一:直接读入 icon 地址
				image = ImageIO.read(new File("gorilla.jpg"));
				// 方法二:从 ImageIcon 中得到 Image
				// icon = new
				// ImageIcon(URLUtil.url2Str("http://www.55like.com/images/logo.jpg"));
				// image = icon.getImage();
			} catch (Exception ex) {
				ex.printStackTrace(System.err);
			}
			plot1.setBackgroundImage(image); // 设置背景图片
			// plot.setBackgroundImage(JFreeChart.INFO.getLogo()); //系统默认的大猩猩背景图片
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
	         ChartFrame frame = new ChartFrame("K线图", chart);
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

		// 系统默认，手动生成的数据
		//A
		//JFreeChart chart = createChart(createDataset());

		//http://api.jzz183.com/api/getchart.php?username=test&password=098f6bcd4621d373cade4e832627b4f6&type=&time=&limit=

		// 自动生成的数据,45 两个月
		// 67 三个月    88 四个月
		//http://api.jzz183.com/api/getchart.php?username=test&password=098f6bcd4621d373cade4e832627b4f6&type=1&time=4&limit=80
		//time = 4;
		//JFreeChart chart = createChart(klineset.createDataset(1, 4, 80));
		
		//求 30 分钟的  K 线
		//http://api.jzz183.com/api/getchart.php?username=test&password=098f6bcd4621d373cade4e832627b4f6&type=1&time=3&limit=80
		//time = 3;
		//JFreeChart chart = createChart(klineset.createDatasetFor30(1, 3, 20));
		
		//求 5 分钟的 K 线
		//http://api.jzz183.com/api/getchart.php?username=test&password=098f6bcd4621d373cade4e832627b4f6&type=1&time=2&limit=36
		time = 2;
		JFreeChart chart = createChart(klineset.createDatasetFor30(1, 2, 36));
		
		

		return new ChartPanel(chart);
	}

	/**
	 * Starting point for the demonstration application.
	 * 
	 * @param args
	 *            ignored.
	 */
	public static void main(String[] args) {
		
		HighLowChartDemo4 demo = new HighLowChartDemo4("Candlestick Demo");
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);
	}

}
