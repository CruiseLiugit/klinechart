package model.kline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.Minute;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.date.DateUtilities;
import org.jstockchart.model.TimeseriesItem;

import util.CheckTimeInList;
import util.DateUtil;
import util.ListTools;
import util.ManageWeek;
import util.TimeTools;

import entity.KLine;

import biz.KLineBiz;

/**
 * 专门为 K 线图(蜡烛图)生成数据集的类
 * 
 * @author SDJG
 * 
 */
public class KLineDataSet {
	// 从服务器获得 K 线数据的类
	private KLineBiz kbz = new KLineBiz();
	private double highValue = Double.MIN_VALUE;// 设置K线数据当中的最大值
	private double minValue = Double.MAX_VALUE;// 设置K线数据当中的最小值
	private Date lower = null; // x轴 最早时间
	private Date upper = null; // x轴 最晚时间
	private Date today = null;  //图片右上方显示时间
	
	

	public Date getToday() {
		return today;
	}


	/**
	 * 为 K 线图中的蜡烛创建数据模型
	 * 
	 * @param type
	 *            1--天通银，2--天通钯金，3--天通铂金，4--天通镍
	 * @param time
	 *            1--1分钟，2--5分钟，3--30分钟，4--1天
	 * @param limit
	 *            默认 10条，一天 K 线 45 条数据
	 * 
	 * @return a sample high low dataset.
	 */
	public OHLCSeries createDataset(int type, int time, int limit) {
		// 高开低收数据序列，股票K线图的四个数据，依次是开，高，低，收
		OHLCSeries series = new OHLCSeries("");
		try {
			List<KLine> list = kbz.getKLineList(type, time, limit);
			int size = list.size();
			//System.out.println("---------日K查询出的数据个数:" + size + "----------");
			
			// 求后一天，让日期轴，显示的时候留有适当间距
			DateUtil du = new DateUtil();
			String nextDay = du.getPreDate(list.get(0).getOpentime(), "d", 2);
			upper = TimeTools.getString2Data2(nextDay);
			today = TimeTools.getString2Data2(list.get(0).getOpentime());
			lower = TimeTools.getString2Data2(list.get(list.size() - 1)
					.getOpentime());
			//System.out.println("  日K x 最早时间 lower :" + lower + "   ,最晚时间 upper :"+ upper);
			
			//工作日，调休放假时，判断并采用补充数据点的方式，让整个 K 线连续
			//遍历，检查两个日期之间的时间间隔，如果有间隔，非周末，在间隔的地方补充一个数据点
			List<KLine> list2 = new ArrayList<KLine>();  
			for (int i = 0; i < list.size(); i++) {
				KLine kl1 = list.get(i);
				if ((i+1) == list.size()) {
					break;
				}
				KLine kl2 = list.get(i+1);
				String beginDateStr =kl1.getOpentime().trim();
				String endDateStr = kl2.getOpentime().trim();
				//System.out.println("============================日K========================");
				//System.out.println(" 日K  begin :"+beginDateStr +" , end :"+endDateStr);
				//这里判断两个时间点之间，间隔 1 天，主要用于判断 日k 线
				//用于判断 30 分钟的 不合适
				long days = du.getDaySub(beginDateStr, endDateStr);
				//System.out.println("-----------------日K -间隔天数  days ="+days);
				if (days == -1) {
					//System.out.println("---正常添加到新 List 中 ");
					list2.add(kl1);
				}
				if (days < -1) {
					//System.out.println("-----------------日K-间隔超过 1 天 ");
					for (int j = 0; j < (Math.abs(days)-1); j++) {
						//System.out.println("------  重复添加，开始那一天的数据  ");
						String nextTime = du.getPreDate(endDateStr, "d", (j+1));
						//System.out.println(" .......nextTime ="+nextTime);
						KLine kltemp =  new KLine();
						//让补充的点，看上去是一个  不红、不绿、横线
						kltemp.setClose(kl1.getOpen());
						kltemp.setCode(kl1.getCode());
						kltemp.setHigh(kl1.getHigh());
						kltemp.setId(kl1.getId());
						kltemp.setLow(kl1.getHigh());
						kltemp.setOpen(kl1.getOpen());
						kltemp.setOpentime(nextTime);
						list2.add(kltemp);
					}
					list2.add(kl1);
				}
			}
			
			//System.out.println("--------- 日K 检查过间隔天数后，数据条数:"+list2.size()+"---------");

			for (int i = 0; i < list2.size(); i++) {
				KLine kLine = (KLine) list2.get(i);

				// 解析时间，得到年，月，日，时，分，秒
				Day opentime = TimeTools.changeStringToDay(kLine.getOpentime());
				double openi = new Double(kLine.getOpen().trim());
				double high = new Double(kLine.getHigh().trim()); // 最高点，线条
				double low = new Double(kLine.getLow().trim()); // 最低点，线条
				double closei = new Double(kLine.getClose().trim());
				
				series.add(opentime, openi, high, low, closei);
				// ///////////////////////////////////////
				// 求y 轴上限、下限值
				if (highValue < high) {
					highValue = high;
				}
				if (minValue > low) {
					minValue = low;
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return series;
	}
	
	
	/**
	 * 为 30 分钟 K 线图中的蜡烛创建数据模型
	 * 
	 * @param type
	 *            1--天通银，2--天通钯金，3--天通铂金，4--天通镍
	 * @param time
	 *            1--1分钟，2--5分钟，3--30分钟，4--1天
	 * @param limit
	 *            默认 10条，一天 K 线 45 条数据
	 * 
	 * @return a sample high low dataset.
	 */
	public OHLCSeries createDatasetFor30(int type, int time, int limit) {
		// 高开低收数据序列，股票K线图的四个数据，依次是开，高，低，收
				OHLCSeries series = new OHLCSeries("");
				try {
					List<KLine> list = kbz.getKLineList(type, time, limit);
					int size = list.size();
					//System.out.println("---------查询出的数据个数:" + size + "----------");
					DateUtil du = new DateUtil();
					// 求后一天，让日期轴，显示的时候留有适当间距
					upper = TimeTools.getString2Data2(list.get(0).getOpentime());
					lower = TimeTools.getString2Data2(list.get(list.size()-1).getOpentime());
					//System.out.println("x 最早时间 lower :"+lower+"   ,最晚时间 upper :"+upper);
					

					//每天4:00-7:00 停盘时，判断并采用补充数据点的方式，让整个 K 线连续
					//遍历，检查两个日期之间的时间间隔，如果有间隔，非周末，在间隔的地方补充一个数据点
					List<KLine> list2 = new ArrayList<KLine>();  
					for (int i = 0; i < list.size(); i++) {
						KLine kl1 = list.get(i);
						if ((i+1) == list.size()) {
							break;
						}
						KLine kl2 = list.get(i+1);
						String beginDateStr =kl1.getOpentime().trim();
						String endDateStr = kl2.getOpentime().trim();
						//System.out.println("====================================================");
						//System.out.println("begin :"+beginDateStr +" , end :"+endDateStr);
						//这里判断两个时间点之间，间隔 1 天，主要用于判断 日k 线
						//用于判断 30 分钟的 不合适
						long hours = du.getHourSub(beginDateStr, endDateStr);
						//System.out.println("---间隔半小时数  hours ="+hours);
						if (hours == -1) {
							//System.out.println("---正常添加到新 List 中 ");
							list2.add(kl1);
						}
						if (hours < -1) {
							//System.out.println("----间隔超过 30 分钟 ");
							for (int j = 0; j < (Math.abs(hours)-1); j++) {
								//System.out.println("------  重复添加，开始那一天的数据  ");
								String nextTime = du.getPreDate(endDateStr, "m", ((j+1)*30));
								//System.out.println(" .......nextTime ="+nextTime);
								KLine kltemp =  new KLine();
								//让补充的点，看上去是一个  不红、不绿、横线
								kltemp.setClose(kl1.getOpen());
								kltemp.setCode(kl1.getCode());
								kltemp.setHigh(kl1.getHigh());
								kltemp.setId(kl1.getId());
								kltemp.setLow(kl1.getHigh());
								kltemp.setOpen(kl1.getOpen());
								kltemp.setOpentime(nextTime);
								list2.add(kltemp);
							}
							list2.add(kl1);
						}
					}
					
					//System.out.println("---------检查过间隔天数后，数据条数:"+list2.size()+"---------");
					
					
					for (int i = 0; i < list2.size(); i++) {
						KLine kLine = (KLine) list2.get(i);

						// 解析时间，得到年，月，日，时，分，秒
						Minute opentime = TimeTools.changeStringToMinute(kLine.getOpentime());
						double openi = new Double(kLine.getOpen().trim());
						double high = new Double(kLine.getHigh().trim()); // 最高点，线条
						double low = new Double(kLine.getLow().trim()); // 最低点，线条
						double closei = new Double(kLine.getClose().trim());

						series.add(opentime, openi, high, low, closei);
						// ///////////////////////////////////////
						// 求y 轴上限、下限值
						if (highValue < high) {
							highValue = high;
						}
						if (minValue > low) {
							minValue = low;
						}

					}

				} catch (Exception e) {
					e.printStackTrace();
				}

				return series;
	}
	
	
	/**
	 * 为 K  线图中的蜡烛创建数据模型
	 * 
	 * @param type   1--天通银，2--天通钯金，3--天通铂金，4--天通镍
	 * @param time   1--1分钟，2--5分钟，3--30分钟，4--1天
	 * @param limit  默认 10条，一天 K 线 45 条数据
	 * 
	 * @return a sample high low dataset.
	 */
	public OHLCSeries createDatasetFor5(int type, int time, int limit) {
		// 高开低收数据序列，股票K线图的四个数据，依次是开，高，低，收
		OHLCSeries series = new OHLCSeries("");
		try {
			List<KLine> list = kbz.getKLineList(type, time, limit);
			int size = list.size();
			//System.out.println("---------查询出的数据个数:" + size + "----------");
			DateUtil du = new DateUtil();
			// 求后一天，让日期轴，显示的时候留有适当间距
			upper = TimeTools.getString2Data2(list.get(0).getOpentime());
			lower = TimeTools.getString2Data2(list.get(list.size()-1).getOpentime());
			//System.out.println("x 最早时间 lower :"+lower+"   ,最晚时间 upper :"+upper);
			

			//每天4:00-7:00 停盘时，判断并采用补充数据点的方式，让整个 K 线连续
			//遍历，检查两个日期之间的时间间隔，如果有间隔，非周末，在间隔的地方补充一个数据点
			List<KLine> list2 = new ArrayList<KLine>();  
			for (int i = 0; i < list.size(); i++) {
				KLine kl1 = list.get(i);
				if ((i+1) == list.size()) {
					break;
				}
				KLine kl2 = list.get(i+1);
				String beginDateStr =kl1.getOpentime().trim();
				String endDateStr = kl2.getOpentime().trim();
				//System.out.println("====================================================");
				//System.out.println("begin :"+beginDateStr +" , end :"+endDateStr);
				//这里判断两个时间点之间，间隔 1 天，主要用于判断 日k 线
				//用于判断 间隔超过 1 分钟的 不合适
				long minutes = du.getMinitSub(beginDateStr, endDateStr);
				//System.out.println("---间隔 分钟 数  minutes ="+minutes);
				if (minutes == -1) {
					//System.out.println("---正常添加到新 List 中 ");
					list2.add(kl1);
				}
				if (minutes < -1) {
					//System.out.println("----间隔超过 1 分钟 ");
					for (int j = 0; j < (Math.abs(minutes)-1); j++) {
						//System.out.println("------  重复添加，开始那一天的数据  ");
						String nextTime = du.getPreDate(endDateStr, "m", ((j+1)*5));
						//System.out.println(" .......nextTime ="+nextTime);
						KLine kltemp =  new KLine();
						//让补充的点，看上去是一个  不红、不绿、横线
						kltemp.setClose(kl1.getOpen());
						kltemp.setCode(kl1.getCode());
						kltemp.setHigh(kl1.getHigh());
						kltemp.setId(kl1.getId());
						kltemp.setLow(kl1.getHigh());
						kltemp.setOpen(kl1.getOpen());
						kltemp.setOpentime(nextTime);
						list2.add(kltemp);
					}
					list2.add(kl1);
				}
			}
			
			//System.out.println("---------检查过间隔天数后，数据条数:"+list2.size()+"---------");
			
			
			
			for (int i = 0; i < list2.size(); i++) {
				KLine kLine = (KLine) list2.get(i);

				// 解析时间，得到年，月，日，时，分，秒
				Minute opentime = TimeTools.changeStringToMinute(kLine.getOpentime());
				double openi = new Double(kLine.getOpen().trim());
				double high = new Double(kLine.getHigh().trim()); // 最高点，线条
				double low = new Double(kLine.getLow().trim()); // 最低点，线条
				double closei = new Double(kLine.getClose().trim());
				
				//System.out.println("opentime ="+kLine.getOpentime()+"  ,openprice ="+openi+"  ,high="+high+"   ,low="+low+"  ,close="+closei);

				series.add(opentime, openi, high, low, closei);
				// ///////////////////////////////////////
				// 求y 轴上限、下限值
				if (highValue < high) {
					highValue = high;
				}
				if (minValue > low) {
					minValue = low;
				}
				
				
			}
			//System.out.println(" highValue ="+highValue+"   ,minValue ="+minValue);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return series;
	}
	

	/**
	 * 根据 数据的最高，获取 图标 y 轴最顶端的值的范围 y 轴最大值要比 数据的最大值大
	 * 
	 * @return
	 */
	public double getHighValue() {
		return highValue;
	}

	/**
	 * 根据 数据的最低值，获取 图标 y 轴最底端的值的范围 y 轴最小值要比 数据的最小值小
	 * 
	 * @return
	 */
	public double getLowValue() {
		return minValue;
	}

	/**
	 * 得到 x 轴最早时间
	 * 
	 * @return
	 */
	public Date getLower() {
		return lower;
	}

	/**
	 * 得到 x 轴最晚时间
	 * 
	 * @return
	 */
	public Date getUpper() {
		return upper;
	}

	/**
	 * 为 分时图 中的折线创建数据模型 判断了 冬令时、夏令时
	 * 
	 
	 * 这个方法 没有使用 
	 * 
	 * @param type
	 *            1--天通银，2--天通钯金，3--天通铂金，4--天通镍
	 * @param time
	 *            1--1分钟 取消，固定传 1
	 * @param limit
	 *            默认 10条，一天 早上6:00-凌晨4:00 23个小时 1380 分钟
	 * 
	 *            调用前判断当前系统时间是 夏令时还是冬令时
	 * 
	 *            创建一个 时序点，参数1 时间、参数2 价格、参数3 数量 TimeseriesItem(java.util.Date
	 *            time, double price, double volume)
	 * 
	 * @return a sample high low dataset.
	 */
	public List<TimeseriesItem> createMinutesDatasetList(int type, int limit) {
		// 分时曲线，每分钟一个点
		List<TimeseriesItem> itemsList = new ArrayList<TimeseriesItem>();
		try {
			List<KLine> list = kbz.getKLineList(type, 1, limit);
			// 求后一天
			DateUtil du = new DateUtil();
			// String nextDay = du.getPreDate(list.get(0).getOpentime(), "d",
			// 2);
			// upper = TimeTools.getString2Data2(nextDay);
			upper = TimeTools.getString2Data2(list.get(0).getOpentime());

			lower = TimeTools.getString2Data2(list.get(list.size() - 1)
					.getOpentime());
			System.out.println("x 最早时间 lower :" + lower + "   ,最晚时间 upper :"
					+ upper);

			// 倒序
			// Collections.reverse(list);

			// 先得到当前的 所有时间点，不能显示前一天的数据
			// 新建一个保存今天数据的新集合
			List<KLine> todayList = new ArrayList<KLine>();
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				// 1 循环检查每个值，看看是否符合时间要求
				KLine kLine = (KLine) iterator.next();
				String opentime = kLine.getOpentime().trim();
				// 判断夏季、冬季是否是当天的数据
				boolean flag = TimeTools.checkIsToday(
						TimeTools.checkSeason(opentime), opentime);

				if (!flag) {
					// 是，添加入数据，成为图上显示的数据
					todayList.add(kLine);
				}
			}

			int size = todayList.size();
			//System.out.println("---------查询出的今天的数据个数:" + size + "----------");

			for (int i = 0; i < todayList.size(); i++) {
				KLine kLine = (KLine) todayList.get(i);
				// 取开盘价格，每分钟一个点
				// 1 取开盘时间
				Date opentime = TimeTools.getString2DataHourMins(kLine
						.getOpentime().trim());
				// 2 开盘价格
				double openprice = new Double(kLine.getOpen().trim());
				// 3 成交数量
				double number = 0.0;

				// 创建一个 时间序列的 item 对象
				TimeseriesItem item = new TimeseriesItem(opentime, openprice,
						number);

				itemsList.add(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemsList;
	}

	/**
	 * 为 分时图 中的折线创建数据模型 判断了一天从 00:00 -- 23:59:00 同时 判断了 冬令时、夏令时
	 * 
	 *  客户需要使用 曲线显示  分时图 ，这个方法使用
	 * 
	 * @param type
	 *            1--天通银，2--天通钯金，3--天通铂金，4--天通镍
	 * @param time
	 *            1--1分钟 取消，固定传 1
	 * @param limit
	 *            默认 10条，一天 早上6:00-凌晨4:00 23个小时 1380 分钟
	 * 
	 *            创建一个 时序点，参数1 时间、参数2 价格、参数3 数量 TimeseriesItem(java.util.Date
	 *            time, double price, double volume)
	 * 
	 * @return a sample high low dataset.
	 */
	public TimeSeriesCollection createXYLineDataSet(int type, int limit) {
		// 分时曲线，每分钟一个点
		TimeSeries series1 = new TimeSeries("不固定开始结束时间的  折线图");
		try {
			List<KLine> list = kbz.getKLineList(type, 1, limit);
			//System.out.println("KLineDataSet.java createXYLineDataSet() ############## list size ="+list.size());
			//检测出当天图上需要显示点的数据，判断了冬令时和夏令时
			List<KLine> list2 = CheckTimeInList.checkList(list);
			//System.out.println("KLineDataSet.java createXYLineDataSet() ############## list2 size ="+list2.size());
			//System.out.println("KLineDataSet.java createXYLineDataSet() ############## list2 get(0) ="+list2.get(0));
			
			
			DateUtil du = new DateUtil();
			String nextDay = du.getPreDate(list2.get(0).getOpentime(), "d",1);
			//System.out.println("KLineDataSet.java createXYLineDataSet() ############## nextDay ="+nextDay);
			//这里求固定到第二天的 4:00
			upper = TimeTools.getString2DataEarly4(nextDay);
			today = TimeTools.getString2Data2(list2.get(0).getOpentime());
			//System.out.println("############## todDay ="+today);
			//这里先判断季节，冬季，固定为 7:00； 夏季，固定为 6:00
			lower = TimeTools.getString2Data2(list2.get(list2.size() - 1).getOpentime());
			//System.out.println("x 最早时间 lower :" + lower + "   ,最晚时间 upper :"+ upper);

			// 倒序
			// Collections.reverse(list);

			for (int i = 0; i < list2.size(); i++) {
				KLine kLine = (KLine) list2.get(i);
				// 取开盘价格，每分钟一个点
				// 1 取开盘时间
				// 解析时间，得到年，月，日，时，分，秒
				// RegularTimePeriod start = new Minute();
				RegularTimePeriod opentime = TimeTools
						.changeStringToMinute(kLine.getOpentime().trim());
				// 2 开盘价格
				double openprice = new Double(kLine.getOpen().trim());
				double high = new Double(kLine.getHigh().trim()); // 最高点，线条
				double low = new Double(kLine.getLow().trim()); // 最低点，线条
				
				//System.out.println("opentime ="+kLine.getOpentime()+"  ,high ="+high+"  ,low="+low);

				// 增加一个时间节点
				series1.add(opentime, openprice);
				// ///////////////////////////////////////
				// 求y 轴上限、下限值
				if (highValue < high) {
					highValue = high;
				}
				if (minValue > low) {
					minValue = low;
				}
			}
			//System.out.println(" highValue ="+highValue+"   ,minValue ="+minValue);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		TimeSeriesCollection dataset = new TimeSeriesCollection(series1);
		return dataset;
	}

	/**
	 * 创建分时图中，XYArea 的数据集合
	 * 
	 * 客户不用 XYArea 图显示数据了。这个方法不用了
	 * 
	 * @param type
	 * @param limit
	 * @return
	 */
	public XYDataset createXYAreaDataSet(int type, int limit) {
		TimeSeries series1 = new TimeSeries("不固定开始结束时间的 区域图");
		try {
			List<KLine> list = kbz.getKLineList(type, 1, limit);
			// 求后一天
			DateUtil du = new DateUtil();
			// String nextDay = du.getPreDate(list.get(0).getOpentime(), "d",
			// 2);
			// upper = TimeTools.getString2Data2(nextDay);
			upper = TimeTools.getString2Data2(list.get(0).getOpentime());

			lower = TimeTools.getString2Data2(list.get(list.size() - 1)
					.getOpentime());
			//System.out.println("x 最早时间 lower :" + lower + "   ,最晚时间 upper :"+ upper);

			// 倒序
			// Collections.reverse(list);

			// 先得到当前的 所有时间点，不能显示前一天的数据
			// 新建一个保存今天数据的新集合
			List<KLine> todayList = new ArrayList<KLine>();
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				// 1 循环检查每个值，看看是否符合时间要求
				KLine kLine = (KLine) iterator.next();
				String opentime = kLine.getOpentime().trim();
				// 判断夏季、冬季是否是当天的数据
				boolean flag = TimeTools.checkIsToday(
						TimeTools.checkSeason(opentime), opentime);

				if (!flag) {
					// 是，添加入数据，成为图上显示的数据
					todayList.add(kLine);
				}
			}

			//List<KLine> todayList2 = ListTools.removeDuplicateWithOrder(todayList);

			int size = todayList.size();
			//System.out.println("---------查询出的今天的数据个数:" + size + "----------");

			for (int i = 0; i < todayList.size(); i++) {
				KLine kLine = (KLine) todayList.get(i);
				// 取开盘价格，每分钟一个点
				// 1 取开盘时间
				// 解析时间，得到年，月，日，时，分，秒
				// RegularTimePeriod start = new Minute();
				RegularTimePeriod opentime = TimeTools
						.changeStringToMinute(kLine.getOpentime().trim());
				// 2 开盘价格
				double openprice = new Double(kLine.getOpen().trim());
				double high = new Double(kLine.getHigh().trim()); // 最高点，线条
				double low = new Double(kLine.getLow().trim()); // 最低点，线条
				

				// 增加一个时间节点
				series1.add(opentime, openprice);
				// ///////////////////////////////////////
				// 求y 轴上限、下限值
				if (highValue < high) {
					highValue = high;
				}
				if (minValue > low) {
					minValue = low;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		TimeSeriesCollection dataset = new TimeSeriesCollection(series1);
		return dataset;
	}

}
