package model.chart;

import java.awt.Color;

import java.io.File;

import java.io.IOException;

import java.text.DecimalFormat;

import java.text.SimpleDateFormat;

import java.util.ArrayList;

import java.util.Calendar;

import java.util.Date;

import java.util.List;

import model.kline.KLineDataSet;

import org.jfree.chart.ChartUtilities;

import org.jfree.chart.JFreeChart;

import org.jfree.chart.axis.SegmentedTimeline;

import org.jfree.chart.plot.XYPlot;

import org.jfree.data.Range;

import org.jfree.data.time.Minute;

import org.jstockchart.JStockChartFactory;

import org.jstockchart.area.PriceArea;

import org.jstockchart.area.TimeseriesArea;

import org.jstockchart.area.VolumeArea;

import org.jstockchart.axis.TickAlignment;

import org.jstockchart.axis.logic.CentralValueAxis;

import org.jstockchart.axis.logic.LogicDateAxis;

import org.jstockchart.axis.logic.LogicNumberAxis;

import org.jstockchart.dataset.TimeseriesDataset;

import org.jstockchart.model.TimeseriesItem;

import org.jstockchart.util.DateUtils;

/**

 * @author: Daniel Cao

 * @date: 2009-3-24

 * @time: 下午08:21:10

 *

 */

/**
 * 
 * 由于JFreeChart具有高度的可定制性，所以当生成了JFreeChart实例后，
 * 
 * 你可以从中获取图表中的各个元素，如Plot，然后再定制它们的属性。
 * 
 * 详细信息请参见JFreeChart的站点和论坛。
 * 
 * 另外，在创建JStockChart的XXXArea对象时，也可以直接定制若干属性。
 * 
 * 如，通过PriceArea类，可以设置价格线的颜色(PriceArea.setPriceColor)，
 * 
 * 均线的颜色(PriceArea.setAverageColor)；
 * 
 * 是否显示均线(PriceArea.setAverageVisible)；
 * 
 * 是否显示涨跌幅(PriceArea.setRateVisible)；...。
 * 
 * 详细信息请参见JStockChart的API文档
 * 
 * 
 */
public class StockLineDemo {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {

		String imageDir = "/Users/SDJG/images";

		File images = new File(imageDir);

		if (!images.exists()) {

			images.mkdir();

		}

		String imageFile = imageDir + "/jstockchart-timeseries.png";

		//一定是在一天里面的数据
		Date startTime = DateUtils.createDate(2014, 2, 1, 00, 00, 0);

		Date endTime = DateUtils.createDate(2014, 2, 1, 23, 59, 0);
		  
		
		double lastPrice = 23.50;

		// 创建一个包含TimeseriesItem的List实例。
		// 注意：所有的数据，都必须在"同一天"内。
		List data = makeTimeseriesItem(lastPrice, startTime, endTime,Calendar.MINUTE, 1);

		//List<TimeseriesItem> data = makeTimeseriesItemFromService(826);
		
		
		// 创建SegmentedTimeline实例，表示时间区间"09:30-11:30,13:00-15:00"。

		SegmentedTimeline timeline = new SegmentedTimeline(

		SegmentedTimeline.MINUTE_SEGMENT_SIZE, 1351, 89);

		timeline.setStartTime(SegmentedTimeline.firstMondayAfter1900() + 780

		* SegmentedTimeline.MINUTE_SEGMENT_SIZE);

		// 创建TimeseriesDataset实例，时间间隔为1分钟。

		TimeseriesDataset dataset = new TimeseriesDataset(Minute.class, 1,

		timeline, false);

		// 向dataset中加入TimeseriesItem的List

		dataset.addDataItems(data);

		// 创建逻辑价格坐标轴。指定中间价为lastPrice，显示9个坐标值，坐标值的格式为".00"。

		CentralValueAxis logicPriceAxis = new CentralValueAxis(

		new Double(lastPrice), new Range(

		dataset.getMinPrice().doubleValue(), dataset

		.getMaxPrice().doubleValue()), 9,

		new DecimalFormat(".00"));

		// 创建价格区域

		PriceArea priceArea = new PriceArea(logicPriceAxis);

		// 设定显示中线

		priceArea.setMarkCentralValue(true);

		// 设定中线颜色

		priceArea.setCentralPriceColor(new Color(0x0000FF));

		// 不显示均价线
		priceArea.setAverageVisible(false);

		// 设定价格区背景色

		priceArea.setBackgroudColor(new Color(0xFFFFCC));

		// 设定价格线的颜色

		priceArea.setPriceColor(new Color(0x330033));

		// 创建逻辑量坐标轴。显示5个坐标值，坐标值的格式为"0"。

		LogicNumberAxis logicVolumeAxis = new LogicNumberAxis(new Range(dataset

		.getMinVolume().doubleValue(), dataset.getMaxVolume()

		.doubleValue()), 5, new DecimalFormat("0"));

		// 创建量区域

		VolumeArea volumeArea = new VolumeArea(logicVolumeAxis);

		// 背景色

		volumeArea.setBackgroudColor(new Color(0xFFFFFC));

		// 设定量图的颜色

		volumeArea.setVolumeColor(new Color(0x3366FF));

		// 创建时序图区域

		TimeseriesArea timeseriesArea = new TimeseriesArea(priceArea,volumeArea,
				createlogicDateAxis(DateUtils.createDate(2014, 2, 1)));

		// 设定价格区占的权重
		timeseriesArea.setPriceWeight(3);

		// 通过JStockChartFactory的工厂方法生成JFreeChart实例。
		// 指定了该图的标题为"Timeseries Chart Demo"，并且不生成图例(legend)。

		JFreeChart jfreechart = JStockChartFactory.createTimeseriesChart(

		"实时走势图", dataset, timeline, timeseriesArea, false);

		XYPlot plot = (XYPlot) jfreechart.getPlot();

		plot.setBackgroundAlpha(0.618f);

		// 创建图像文件。图像格式为PNG，长为545，宽为300。
		ChartUtilities
				.saveChartAsPNG(new File(imageFile), jfreechart, 545, 300);

	}

	//调用服务器上数据
	private static List<TimeseriesItem> makeTimeseriesItemFromService(int limits){
		KLineDataSet  dataset = new KLineDataSet();
		return dataset.createMinutesDatasetList(1, limits);
	}
	
	
	// 随机生成数据
	private static List<TimeseriesItem> makeTimeseriesItem(
			final double lastPrice, final Date startTime, final Date endDate,

			final int type, final int step) {

		long lsTime = startTime.getTime();
		long leTime = endDate.getTime();

		List<TimeseriesItem> list = new ArrayList<TimeseriesItem>();

		for (long l = lsTime; l <= leTime; l += 1 * 60 * 1000L) {

			Date d = new Date(l);

			double price = lastPrice + (Math.random() - 0.5) * 4.5;

			int v = (int) (Math.random() * 1000);

			TimeseriesItem ti = new TimeseriesItem(d, price, v);

			list.add(ti);

		}

		return list;

	}

	// 指定时期坐标轴中的逻辑坐标。

	private static LogicDateAxis createlogicDateAxis(Date baseDate) {

		LogicDateAxis logicDateAxis = new LogicDateAxis(baseDate,new SimpleDateFormat("HH:mm"));

		logicDateAxis.addDateTick("00:00", TickAlignment.START);
		//logicDateAxis.addDateTick("01:00");
		logicDateAxis.addDateTick("02:00");
		//logicDateAxis.addDateTick("03:00");
		logicDateAxis.addDateTick("04:00", TickAlignment.END);
		
		
		logicDateAxis.addDateTick("07:00", TickAlignment.START);
		//logicDateAxis.addDateTick("08:00");
		logicDateAxis.addDateTick("09:00");
		//logicDateAxis.addDateTick("10:00");
		logicDateAxis.addDateTick("11:00");
		//logicDateAxis.addDateTick("12:00");
		logicDateAxis.addDateTick("13:00");
		//logicDateAxis.addDateTick("14:00");
		logicDateAxis.addDateTick("15:00");
		//logicDateAxis.addDateTick("16:00");
		logicDateAxis.addDateTick("17:00");
		//logicDateAxis.addDateTick("18:00");
		logicDateAxis.addDateTick("19:00");
		//logicDateAxis.addDateTick("20:00");
		logicDateAxis.addDateTick("21:00");
		//logicDateAxis.addDateTick("22:00");
		logicDateAxis.addDateTick("23:00", TickAlignment.END);
		
		return logicDateAxis;

	}
}
