package model.kline;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import javax.swing.ImageIcon;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SegmentedTimeline;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.servlet.ServletUtilities;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.Align;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;

import util.TimeTools;

public class XYAreaDemo {

	private String horizon_name = ""; // 水平线名字,在图上都是空,这里不用设置值
	private String vertical_name = ""; // 垂直线名字,在图上都是空,这里不用设置值
	private String sales_price = "";
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
	private double highValue = Double.MIN_VALUE;// 设置K线数据当中的最大值
	private double minValue = Double.MAX_VALUE;// 设置K线数据当中的最小值

	// 自定义的数据模型，线上实时获取的数据模型
	private KLineDataSet klineset = new KLineDataSet();
	
	/***************************************************
	 * 产生 分时 线图，不需要均线，右侧的 百分比数据 
	 * 考虑了根据 当前系统时间判断 交易的 夏季和冬季 
	 * 夏季 交易时间 6:00到第二天凌晨 4:00
	 * 冬季 交易时间 7:00到第二天凌晨 4:00 
	 * time:1---1分钟
	 * 
	 * @param  type 产品类型  1--天通银，2--天通钯金，3--天通铂金，4--天通镍
	 * @param  limit 数据点数量 自动生成的数据
			   23 小时 1380 夏季
			   22 小时 1320 冬季
	 * @param title 产品标题 		   
	 * 
	 * 
	 * @param : String,HttpSession,PrintWriter
	 ***************************************************/
	public String generateTimeLineChartNew(int type, int limit,
			String title, String iconpath, HttpSession session, PrintWriter pw) {
		String filename = null;
		try {
			KLineDataSet klineset = new KLineDataSet();
			// 得到数据集合
			//区域的
			//XYDataset dataset = klineset.createXYAreaDataSet(type, limit);
			//线条的
			TimeSeriesCollection dataset = klineset.createXYLineDataSet(type, limit);
			
			//定义 图形 渲染器
			//区域的 渲染器
			//XYAreaRenderer renderer = new XYAreaRenderer(XYAreaRenderer.AREA);
			//线条的 渲染器
			XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true,false); 
			
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
		    	 	//System.out.println("--x 最早时间 lower :"+klineset.getLower()+"   ,最晚时间 upper :"+klineset.getUpper());
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
		    	// 设置时间刻度的间隔，一般以 5 分钟为单位
			// x1Axis.setTickUnit(new DateTickUnit(DateTickUnitType.MINUTE,5));
			// 设置显示时间的格式
			x1Axis.setDateFormatOverride(new SimpleDateFormat("HH:mm"));
				
		     
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
		     y1Axis.setRange(minValue*0.999, highValue*1.0009);
		     
		     //设置刻度显示的密度
		     //y1Axis.setTickUnit(new NumberTickUnit((highValue*1.1-minValue*0.9)/10));
		     XYPlot plot=new XYPlot(dataset,x1Axis,y1Axis,renderer);//设置画图区域对象
			
			// //////背景水印////////////
			Image image = null;
			ImageIcon icon = null;
			try {
				// icon 图标 "gorilla.jpg"
				image = ImageIO.read(new File(iconpath));
			} catch (Exception ex) {
				ex.printStackTrace(System.err);
			}
			plot.setBackgroundImage(image); // 设置背景图片
			// plot.setBackgroundImage(JFreeChart.INFO.getLogo());
			// //系统默认的大猩猩背景图片
			plot.setBackgroundImageAlignment(Align.BOTTOM_RIGHT);// 设置背景图片位置
			plot.setBackgroundAlpha(.3f); // icon 背景透明度
			
	         JFreeChart chart = new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, plot, false); 
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
