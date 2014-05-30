package model.kline;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import javax.swing.ImageIcon;

import org.jfree.chart.*;
import org.jfree.chart.axis.*; //控制 X Y 轴的数据范围等
import org.jfree.chart.renderer.category.BarRenderer3D; //显示3D柱状图
import org.jfree.chart.renderer.category.LineRenderer3D;//显示3D折线图
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.plot.*; //控制显示区域的
import org.jfree.chart.entity.*; //实体
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.labels.*; //标签
import org.jfree.chart.servlet.*;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;

import org.jfree.data.category.CategoryDataset; //数据集接口类
import org.jfree.data.category.DefaultCategoryDataset; //数据集实现类
import org.jfree.data.time.Day;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.XYDataset;

import org.jfree.ui.Align;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;

import util.Constants;
import util.TimeTools;
import util.URLUtil;

public class KLineDemo {
	private String horizon_name = ""; // 水平线名字,在图上都是空,这里不用设置值
	private String vertical_name = ""; // 垂直线名字,在图上都是空,这里不用设置值
	private String sales_price = "";
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
	private double highValue = Double.MIN_VALUE;// 设置K线数据当中的最大值
	private double minValue = Double.MAX_VALUE;// 设置K线数据当中的最小值

	/***************************************************
	 * 产生 K 线图，及 MA5 10 30 新方法，去掉了时间不连续时候的间隔
	 * time:1---1分钟，2---5分钟，3---30分钟，4---1天 2 5分钟取 276 表示一天 3 30分钟，取 46 个点表示一天 4
	 * 1天，44 表示两个月
	 * 
	 * @param type
	 *            产品类型 1--天通银，2--天通钯金，3--天通铂金，4--天通镍
	 * @param time
	 *            时间类型 2---5分钟，3---30分钟，4---1天
	 * @param limit
	 *            数据点数量 自动生成的数据 23 小时 1380 夏季 22 小时 1320 冬季
	 * @param title
	 *            产品标题
	 * 
	 * @param : String,HttpSession,PrintWriter
	 ***************************************************/
	public String generateKLineChartNew(int type, int time, int limit,
			String title, String iconpath, HttpSession session, PrintWriter pw) {
		String filename = null;
		try {
			KLineDataSet klineset = new KLineDataSet();
			OHLCSeries series = klineset.createDataset(type, time, limit);

			//System.out.println("----日 K 线 数据集 OHLCSeries ="+series+" --------");
			
			// 保留K线数据的数据集，必须申明为final，后面要在匿名内部类里面用到
		   OHLCSeriesCollection seriesCollection = new OHLCSeriesCollection();
			seriesCollection.addSeries(series);

			// 设置K线图的画图器，必须申明为final，后面要在匿名内部类里面用到
			final CandlestickRenderer candlestickRender = new CandlestickRenderer();
			// 设置是否使用自定义的边框线，程序自带的边框线的颜色不符合中国股票市场的习惯
			candlestickRender.setUseOutlinePaint(true);

			// 设置如何对K线图的宽度进行设定
			candlestickRender
					.setAutoWidthMethod(CandlestickRenderer.WIDTHMETHOD_AVERAGE);
			candlestickRender.setAutoWidthGap(0.001);// 设置各个K线图之间的间隔
			candlestickRender.setUpPaint(Color.RED);// 设置股票上涨的K线图颜色
			candlestickRender.setDownPaint(Color.GREEN);// 设置股票下跌的K线图颜色

			// 设置x轴，也就是时间轴
			DateAxis x1Axis = new DateAxis();
			// 设置不采用自动设置时间范围
			x1Axis.setAutoRange(false);
			try {
				// 设置时间范围，注意时间的最大值要比已有的时间最大值要多一天
				//System.out.println("--x 最早时间 lower :" + klineset.getLower()+ "   ,最晚时间 upper :" + klineset.getUpper());
				x1Axis.setRange(klineset.getLower().getTime(), klineset
						.getUpper().getTime());
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 设置时间线显示的规则，用这个方法就摒除掉了周六和周日这些没有交易的日期(很多人都不知道有此方法)，使图形看上去连续
			x1Axis.setTimeline(SegmentedTimeline
					.newMondayThroughFridayTimeline());
			// *****
			// 设置不采用自动选择刻度值，这里如果设置 false ，无法显示 X 轴 Date
			// *****
			x1Axis.setAutoTickUnitSelection(true);
			// 设置显示刻度表刻度线
			x1Axis.setAxisLineVisible(true);
			x1Axis.setAxisLinePaint(new Color(221, 221, 221));// 设置刻度线的颜色
			// 设置标记的位置
			x1Axis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
			// 设置标准的时间刻度单位
			x1Axis.setStandardTickUnits(DateAxis.createStandardDateTickUnits());
			// 设置时间刻度的间隔，一般以周为单位，这里添加上去，无法显示 X 轴 Date
			// //////这一行添加上，也无法显示 日期轴
			// x1Axis.setTickUnit(new DateTickUnit(DateTickUnitType.DAY,7));
			// 设置显示时间的格式
			x1Axis.setDateFormatOverride(new SimpleDateFormat("yyyy-MM-dd"));

			// ///////////设定y轴，就是数字轴/////////////
			NumberAxis y1Axis = new NumberAxis();
			y1Axis.setTickLabelPaint(Color.red); // Y 坐标刻度值颜色
			y1Axis.setAutoRange(false);// 不不使用自动设定范围

			// 设定y轴值的范围，比最低值要低一些，比最大值要大一些，这样图形看起来会美观些
			// ////////////////////////////////////////////
			// 如果调用网络数据，这里需要动态变化
			minValue = klineset.getLowValue();
			highValue = klineset.getHighValue();
			// ///////////////////////////////////////////
			// 调整左侧 Y 轴，取值范围
			y1Axis.setRange(minValue * 0.99, highValue * 1.009);

			// 设置刻度显示的密度
			// y1Axis.setTickUnit(new
			// NumberTickUnit((highValue*1.1-minValue*0.9)/10));

			XYPlot plot1 = new XYPlot(seriesCollection, x1Axis, y1Axis,
					candlestickRender);// 设置画图区域对象

			// //////背景水印////////////
			Image image = null;
			ImageIcon icon = null;
			try {
				// icon 图标 "gorilla.jpg"
				image = ImageIO.read(new File(iconpath));
			} catch (Exception ex) {
				ex.printStackTrace(System.err);
			}
			plot1.setBackgroundImage(image); // 设置背景图片
			// plot.setBackgroundImage(JFreeChart.INFO.getLogo());
			// //系统默认的大猩猩背景图片
			plot1.setBackgroundImageAlignment(Align.BOTTOM_RIGHT);// 设置背景图片位置
			plot1.setBackgroundAlpha(.3f); // icon 背景透明度

			JFreeChart chart = new JFreeChart(null,
					JFreeChart.DEFAULT_TITLE_FONT, plot1, false);
			// 修改图片 样式
			chart.setBackgroundPaint(Color.white);

			// 添加子标题
			// 添加两个描述文件, 放在图片上面，左右两侧
			TextTitle sourceL = new TextTitle(title);
			sourceL.setFont(new Font("SansSerif", Font.PLAIN, 10));
			sourceL.setPosition(RectangleEdge.TOP);
			sourceL.setHorizontalAlignment(HorizontalAlignment.LEFT);

			TextTitle sourceR = new TextTitle(
					TimeTools.getUtilDate2String1(klineset.getToday()));
			sourceR.setFont(new Font("SansSerif", Font.PLAIN, 10));
			sourceR.setPosition(RectangleEdge.TOP);
			sourceR.setHorizontalAlignment(HorizontalAlignment.RIGHT);

			// 添加底部描述文字
			chart.addSubtitle(sourceL);
			chart.addSubtitle(sourceR);
			
			//System.out.println("*********** 日 K 图像生成 chart ="+chart+"*************");

			// /////////////////////////添加一条 MA 线 ///////////////////////
			// MA5

			// ////////////////////////////////////////////////////////

			// Write the chart image to the temporary directory,default of
			// Tomcat is $TOMCAT_HOME\temp
			// 把生成的图片放到临时目录，默认放在 Tomcat 的 temp 目录下
			ChartRenderingInfo info = new ChartRenderingInfo(
					new StandardEntityCollection());
			//System.out.println(" ==========日 K $TOMCAT_HOME\temp  : "+info);
			//System.out.println(" ==========日 K session  : "+session);
			//System.out.println(" ==========日 K chart  : "+chart);

			
			// 把 filename 放在 session 中，以后使用
			filename = ServletUtilities.saveChartAsPNG(chart, 560, 400, null,
					session);
			System.out.println(" ==========日 K file name = " + filename);

			// Write the image map to the PrintWriter
			ChartUtilities.writeImageMap(pw, filename, info, false);
			
			pw.flush();
			
		} catch (Exception e) {
			System.out.println("Exception - " + e.toString());
			e.printStackTrace(System.out);
		}

		return filename;

	}

	/***************************************************
	 * 产生 5 分钟 K 线图
	 * 
	 * @param : String,HttpSession,PrintWriter
	 ***************************************************/
	public String generateKLineChartFor5(int type, int time, int limit,
			String title, String iconpath, HttpSession session, PrintWriter pw) {
		String filename = null;
		try {
			KLineDataSet klineset = new KLineDataSet();
			OHLCSeries series = klineset.createDatasetFor5(type, time, limit);

			// 保留K线数据的数据集，必须申明为final，后面要在匿名内部类里面用到
			OHLCSeriesCollection seriesCollection = new OHLCSeriesCollection();
			seriesCollection.addSeries(series);

			// 设置K线图的画图器，必须申明为final，后面要在匿名内部类里面用到
			final CandlestickRenderer candlestickRender = new CandlestickRenderer();
			// 设置是否使用自定义的边框线，程序自带的边框线的颜色不符合中国股票市场的习惯
			candlestickRender.setUseOutlinePaint(true);

			// 设置如何对K线图的宽度进行设定
			candlestickRender
					.setAutoWidthMethod(CandlestickRenderer.WIDTHMETHOD_AVERAGE);
			candlestickRender.setAutoWidthGap(0.001);// 设置各个K线图之间的间隔
			candlestickRender.setUpPaint(Color.RED);// 设置股票上涨的K线图颜色
			candlestickRender.setDownPaint(Color.GREEN);// 设置股票下跌的K线图颜色

			// 设置x轴，也就是时间轴
			DateAxis x1Axis = new DateAxis();
			// 设置不采用自动设置时间范围
			x1Axis.setAutoRange(false);
			try {
				// 设置时间范围，注意时间的最大值要比已有的时间最大值要多一天
				//System.out.println("--x 最早时间 lower :" + klineset.getLower()+ "   ,最晚时间 upper :" + klineset.getUpper());
				x1Axis.setRange(klineset.getLower().getTime(), klineset
						.getUpper().getTime());
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 设置时间线显示的规则，用这个方法就摒除掉了周六和周日这些没有交易的日期(很多人都不知道有此方法)，使图形看上去连续
			x1Axis.setTimeline(SegmentedTimeline
					.newMondayThroughFridayTimeline());
			// 设置不采用自动选择刻度值
			x1Axis.setAutoTickUnitSelection(true);
			// 设置标记的位置
			x1Axis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
			// 设置标准的时间刻度单位
			x1Axis.setStandardTickUnits(DateAxis.createStandardDateTickUnits());
			// 设置时间刻度的间隔，一般以 5 分钟为单位
			// x1Axis.setTickUnit(new DateTickUnit(DateTickUnitType.MINUTE,5));
			// 设置显示时间的格式
			x1Axis.setDateFormatOverride(new SimpleDateFormat("HH:mm"));

			// ///////////设定y轴，就是数字轴/////////////
			NumberAxis y1Axis = new NumberAxis();
			y1Axis.setTickLabelPaint(Color.red); // Y 坐标刻度值颜色
			y1Axis.setAutoRange(false);// 不不使用自动设定范围

			// 设定y轴值的范围，比最低值要低一些，比最大值要大一些，这样图形看起来会美观些
			// ////////////////////////////////////////////
			// 如果调用网络数据，这里需要动态变化
			minValue = klineset.getLowValue();
			highValue = klineset.getHighValue();
			// ///////////////////////////////////////////
			// 调整左侧 Y 轴，取值范围
			y1Axis.setRange(minValue * 0.9999, highValue * 1.00009);

			// 设置刻度显示的密度
			// y1Axis.setTickUnit(new
			// NumberTickUnit((highValue*1.1-minValue*0.9)/10));

			XYPlot plot1 = new XYPlot(seriesCollection, x1Axis, y1Axis,
					candlestickRender);// 设置画图区域对象

			// //////背景水印////////////
			Image image = null;
			ImageIcon icon = null;
			try {
				// icon 图标 "gorilla.jpg"
				image = ImageIO.read(new File(iconpath));
			} catch (Exception ex) {
				ex.printStackTrace(System.err);
			}
			plot1.setBackgroundImage(image); // 设置背景图片
			// plot.setBackgroundImage(JFreeChart.INFO.getLogo());
			// //系统默认的大猩猩背景图片
			plot1.setBackgroundImageAlignment(Align.BOTTOM_RIGHT);// 设置背景图片位置
			plot1.setBackgroundAlpha(.3f); // icon 背景透明度

			JFreeChart chart = new JFreeChart(null,
					JFreeChart.DEFAULT_TITLE_FONT, plot1, false);

			// 修改图片 样式
			chart.setBackgroundPaint(Color.white);

			// 添加子标题
			// 添加两个描述文件, 放在图片上面，左右两侧
			TextTitle sourceL = new TextTitle(title);
			sourceL.setFont(new Font("SansSerif", Font.PLAIN, 10));
			sourceL.setPosition(RectangleEdge.TOP);
			sourceL.setHorizontalAlignment(HorizontalAlignment.LEFT);

			TextTitle sourceR = new TextTitle(
					TimeTools.getUtilDate2String1(klineset.getUpper()));
			sourceR.setFont(new Font("SansSerif", Font.PLAIN, 10));
			sourceR.setPosition(RectangleEdge.TOP);
			sourceR.setHorizontalAlignment(HorizontalAlignment.RIGHT);

			// 添加底部描述文字
			chart.addSubtitle(sourceL);
			chart.addSubtitle(sourceR);

			// /////////////////////////添加一条 MA 线 ///////////////////////
			// MA5

			// ////////////////////////////////////////////////////////

			// Write the chart image to the temporary directory,default of
			// Tomcat is $TOMCAT_HOME\temp
			// 把生成的图片放到临时目录，默认放在 Tomcat 的 temp 目录下
			ChartRenderingInfo info = new ChartRenderingInfo(
					new StandardEntityCollection());
			
			//System.out.println(" ==========5 K $TOMCAT_HOME\temp  : "+info);
			//System.out.println(" ==========5 K session  : "+session);
			//System.out.println(" ==========5 K chart  : "+chart);

			
			// 把 filename 放在 session 中，以后使用
			filename = ServletUtilities.saveChartAsPNG(chart, 560, 400, null,
					session);
			
			//System.out.println(" ==========5 K  file name = " + filename);
			
			// Write the image map to the PrintWriter
			ChartUtilities.writeImageMap(pw, filename, info, false);
			pw.flush();
		} catch (Exception e) {
			System.out.println("Exception - " + e.toString());
			e.printStackTrace(System.out);
		}

		return filename;

	}
	
	
	
	/***************************************************
	 * 产生 30 分钟 K 线图
	 * 
	 * @param : String,HttpSession,PrintWriter
	 ***************************************************/
	public String generateKLineChartFor30(int type, int time, int limit,
			String title, String iconpath, HttpSession session, PrintWriter pw) {
		String filename = null;
		try {
			KLineDataSet klineset = new KLineDataSet();
			OHLCSeries series = klineset.createDatasetFor30(type, time, limit);

			// 保留K线数据的数据集，必须申明为final，后面要在匿名内部类里面用到
			OHLCSeriesCollection seriesCollection = new OHLCSeriesCollection();
			seriesCollection.addSeries(series);

			// 设置K线图的画图器，必须申明为final，后面要在匿名内部类里面用到
			final CandlestickRenderer candlestickRender = new CandlestickRenderer();
			// 设置是否使用自定义的边框线，程序自带的边框线的颜色不符合中国股票市场的习惯
			candlestickRender.setUseOutlinePaint(true);

			// 设置如何对K线图的宽度进行设定
			candlestickRender
					.setAutoWidthMethod(CandlestickRenderer.WIDTHMETHOD_AVERAGE);
			candlestickRender.setAutoWidthGap(0.001);// 设置各个K线图之间的间隔
			candlestickRender.setUpPaint(Color.RED);// 设置股票上涨的K线图颜色
			candlestickRender.setDownPaint(Color.GREEN);// 设置股票下跌的K线图颜色
			
			
			// 设置x轴，也就是时间轴
			DateAxis x1Axis = new DateAxis();
			// 设置不采用自动设置时间范围
			x1Axis.setAutoRange(false);
			try {
				// 设置时间范围，注意时间的最大值要比已有的时间最大值要多一天
				//System.out.println("--x 最早时间 lower :" + klineset.getLower()+ "   ,最晚时间 upper :" + klineset.getUpper());
				x1Axis.setRange(klineset.getLower().getTime(), klineset
						.getUpper().getTime());
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 设置时间线显示的规则，用这个方法就摒除掉了周六和周日这些没有交易的日期(很多人都不知道有此方法)，使图形看上去连续
			x1Axis.setTimeline(SegmentedTimeline
					.newMondayThroughFridayTimeline());
			// 设置不采用自动选择刻度值
			x1Axis.setAutoTickUnitSelection(true);
			// 设置标记的位置
			x1Axis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
			// 设置标准的时间刻度单位
			x1Axis.setStandardTickUnits(DateAxis.createStandardDateTickUnits());
			// 设置时间刻度的间隔，一般以 5 分钟为单位
			// x1Axis.setTickUnit(new DateTickUnit(DateTickUnitType.MINUTE,5));
			// 设置显示时间的格式
			x1Axis.setDateFormatOverride(new SimpleDateFormat("dd HH:mm"));

			// ///////////设定y轴，就是数字轴/////////////
			NumberAxis y1Axis = new NumberAxis();
			y1Axis.setTickLabelPaint(Color.red); // Y 坐标刻度值颜色
			y1Axis.setAutoRange(false);// 不不使用自动设定范围

			// 设定y轴值的范围，比最低值要低一些，比最大值要大一些，这样图形看起来会美观些
			// ////////////////////////////////////////////
			// 如果调用网络数据，这里需要动态变化
			minValue = klineset.getLowValue();
			highValue = klineset.getHighValue();
			// ///////////////////////////////////////////
			// 调整左侧 Y 轴，取值范围
			y1Axis.setRange(minValue * 0.9999, highValue * 1.0009);

			// 设置刻度显示的密度
			// y1Axis.setTickUnit(new
			// NumberTickUnit((highValue*1.1-minValue*0.9)/10));

			XYPlot plot1 = new XYPlot(seriesCollection, x1Axis, y1Axis,
					candlestickRender);// 设置画图区域对象

			// //////背景水印////////////
			Image image = null;
			ImageIcon icon = null;
			try {
				// icon 图标 "gorilla.jpg"
				image = ImageIO.read(new File(iconpath));
			} catch (Exception ex) {
				ex.printStackTrace(System.err);
			}
			plot1.setBackgroundImage(image); // 设置背景图片
			// plot.setBackgroundImage(JFreeChart.INFO.getLogo());
			// //系统默认的大猩猩背景图片
			plot1.setBackgroundImageAlignment(Align.BOTTOM_RIGHT);// 设置背景图片位置
			plot1.setBackgroundAlpha(.3f); // icon 背景透明度

			JFreeChart chart = new JFreeChart(null,
					JFreeChart.DEFAULT_TITLE_FONT, plot1, false);

			// 修改图片 样式
			chart.setBackgroundPaint(Color.white);

			// 添加子标题
			// 添加两个描述文件, 放在图片上面，左右两侧
			TextTitle sourceL = new TextTitle(title);
			sourceL.setFont(new Font("SansSerif", Font.PLAIN, 10));
			sourceL.setPosition(RectangleEdge.TOP);
			sourceL.setHorizontalAlignment(HorizontalAlignment.LEFT);

			TextTitle sourceR = new TextTitle(
					TimeTools.getUtilDate2String1(klineset.getUpper()));
			sourceR.setFont(new Font("SansSerif", Font.PLAIN, 10));
			sourceR.setPosition(RectangleEdge.TOP);
			sourceR.setHorizontalAlignment(HorizontalAlignment.RIGHT);

			// 添加底部描述文字
			chart.addSubtitle(sourceL);
			chart.addSubtitle(sourceR);

			// /////////////////////////添加一条 MA 线 ///////////////////////
			// MA5

			// ////////////////////////////////////////////////////////

			// Write the chart image to the temporary directory,default of
			// Tomcat is $TOMCAT_HOME\temp
			// 把生成的图片放到临时目录，默认放在 Tomcat 的 temp 目录下
			ChartRenderingInfo info = new ChartRenderingInfo(
					new StandardEntityCollection());
			// System.out.println(" ========== $TOMCAT_HOME\temp  : "+info);

			// 把 filename 放在 session 中，以后使用
			filename = ServletUtilities.saveChartAsPNG(chart, 560, 400, null,
					session);
			//System.out.println(" ========== file name = " + filename);

			// Write the image map to the PrintWriter
			ChartUtilities.writeImageMap(pw, filename, info, false);
			pw.flush();
		} catch (Exception e) {
			System.out.println("Exception - " + e.toString());
			e.printStackTrace(System.out);
		}

		return filename;

	}
	
}
