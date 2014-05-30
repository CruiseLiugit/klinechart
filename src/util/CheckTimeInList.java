package util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import entity.KLine;

public class CheckTimeInList {

	/**
	 * 定义方法，判断 list 列表中，大部分数据是哪一天的。然后确定这一天的开始点和结束点 1 天 按照夏季 23*60 = 1380 个点取 ；
	 * 冬天 22 * 60 = 1320 个点取 1、遍历当前的 list 取出所有的 day ，冒泡对比 天数的多少，如果一个日期超过
	 * 一半，确定显示这一天的数据。 2、判断当前日期是冬季还是 夏季 3、把符合这一天的所有数据点，放入 检查后新点集合中 4、返回新的 集合
	 * 
	 * @param list
	 *            从网络上 按照每次取出 1400 的数据点，得到点 集合
	 * @return 剔除掉不是在固定时间轴 一天内显示的数据点
	 */
	public static List<KLine> checkList(List<KLine> list) {
		List<KLine> rslist = new ArrayList<KLine>();
		Iterator<KLine> it = list.iterator();
		
		//先取出当前列表中所有数据的日期基准
		//String time = CheckTimeInList.getListDay(list);
		//应该以当前系统时间作为基准点
		DateUtil du = new DateUtil();
		String time  = du.getCurrentDate();
		//System.out.println("----------日期基准 :"+time);
		
		Date base = TimeTools.getString2Data2(time);
		Calendar cal = Calendar.getInstance();
		cal.setTime(base);
		// 3 检查这个日历中的 月份、周数，看是否符合 夏令时、冬令时规定
		// 天、小时
		int baseDay = cal.get(Calendar.DAY_OF_MONTH);
		int baseHour = cal.get(Calendar.HOUR_OF_DAY); // 24 小时
		//System.out.println("当前时间是  baseDay =" + baseDay + "， baseHour ="+ baseHour);

		// 判断一下季节
		int season = TimeTools.checkSeason(time);

		if (season == 1) {
			// 夏令时
			//System.out.println("夏令时");
			int i=0;
			//取当天的 6:00 - 23:59   第二天的 00:00-4:00
			while (it.hasNext()) {
				KLine kLine = (KLine) it.next();
				//System.out.println("=================================================== i="+(i++));
				// 得到日期，
				Date temp = TimeTools.getString2Data2(kLine.getOpentime());
				// 得到日期的 天、小时
				Calendar caltemp = Calendar.getInstance();
				caltemp.setTime(temp);
				// 3 检查这个日历中的 月份、周数，看是否符合 夏令时、冬令时规定
				// 天、小时
				int tempDay = caltemp.get(Calendar.DAY_OF_MONTH);
				int tempHour = caltemp.get(Calendar.HOUR_OF_DAY); // 24 小时
				//System.out.println("当前时间是  tempDay =" + tempDay + "， tempHour ="+ tempHour);
				
				if (tempDay == baseDay) {
					//取当天的 6:00 - 23:59
					//System.out.println("--------当天-------");
					if (tempHour >= 6) {
						rslist.add(kLine);
						//System.out.println("***大于 6:00 ***");
					}
				}
				if (tempDay-baseDay == 1) {
					//第二天的 00:00-4:00
					//System.out.println("--------第二天-------");
					if (tempHour <= 4) {
						rslist.add(kLine);
						//System.out.println("***小于 4:00 ***");
					}
				}
			}
			
		} else if (season == 2) {
			// 冬令时
			//System.out.println("冬令时");
			//取当天的 7:00 - 23:59   第二天的 00:00-4:00
			
			while (it.hasNext()) {
				KLine kLine = (KLine) it.next();
				// 得到日期，
				Date temp = TimeTools.getString2Data2(kLine.getOpentime());
				// 得到日期的 天、小时
				Calendar caltemp = Calendar.getInstance();
				caltemp.setTime(temp);
				// 3 检查这个日历中的 月份、周数，看是否符合 夏令时、冬令时规定
				// 天、小时
				int tempDay = caltemp.get(Calendar.DAY_OF_MONTH);
				int tempHour = caltemp.get(Calendar.HOUR_OF_DAY); // 24 小时
				//System.out.println("当前时间是  tempDay =" + tempDay + "， tempHour ="+ tempHour);
				
				if (tempDay == baseDay) {
					//取当天的 7:00 - 23:59
					//System.out.println("--------当天-------");
					if (tempHour >= 7) {
						rslist.add(kLine);
						//System.out.println("***大于 7:00 ***");
					}
				}
				if (tempDay-baseDay == 1) {
					//第二天的 00:00-4:00
					//System.out.println("--------第二天-------");
					if (tempHour <= 4) {
						rslist.add(kLine);
						//System.out.println("***小于 4:00 ***");
					}
				}
			}
		}
		
		//System.out.println("****************************************************");

		return rslist;
	}

	/**
	 * 1、遍历当前的 list 取出所有的 day ，冒泡对比 天数的多少，如果一个日期超过 一半，确定显示这一天的数据。
	 * 
	 * @param list
	 *            从网络上 按照每次取出 1400 的数据点，得到点 集合
	 * @return 这些数据里面，占多数的是哪一天
	 */
	public static String getListDay(List<KLine> list) {
		String day = null;
		List<KLine> tempList = new ArrayList<KLine>();

		// 先取出第一个数据点，作为日期对比点基准
		String time = list.get(0).getOpentime();
		Date base = TimeTools.getString2Data2(time);
		Calendar cal = Calendar.getInstance();
		cal.setTime(base);
		// 3 检查这个日历中的  日期数字
		int baseDay = cal.get(Calendar.DAY_OF_MONTH);
		//System.out.println("当前时间是  baseDay =" + baseDay);

		Iterator<KLine> it = list.iterator();
		int count = 0;
		while (it.hasNext()) {
			KLine kLine = (KLine) it.next();
			// 得到日期，
			Date temp = TimeTools.getString2Data2(kLine.getOpentime());
			// 得到日期的 天、小时
			Calendar caltemp = Calendar.getInstance();
			caltemp.setTime(temp);
			// 3 检查这个日历中的 月份、周数，看是否符合 夏令时、冬令时规定
			// 天、小时
			int tempDay = caltemp.get(Calendar.DAY_OF_MONTH);
			int tempHour = caltemp.get(Calendar.HOUR_OF_DAY); // 24 小时
			//System.out.println("当前时间是  tempDay =" + tempDay + "， tempHour ="+ tempHour);
			if (tempDay == baseDay) {
				count++;
			}
		}
		//System.out.println("天数与最近的日期一直的 count =" + count + " , 总数一半 :size = "+ list.size() / 2);
		// 结束后判断
		if (count >= list.size() / 2) {
			day = time;
		} else {
			day = list.get(list.size() - 1).getOpentime();
		}

		return day;
	}

}
