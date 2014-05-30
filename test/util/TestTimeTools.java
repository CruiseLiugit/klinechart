package util;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import model.kline.KLineDataSet;

import org.jfree.data.xy.XYDataset;
import org.junit.Test;

import biz.KLineBiz;

import entity.KLine;

public class TestTimeTools {

	/**
	 * 测试得到  K 线图的数据集中的开盘时间对象 Day
	 */
	@Test
	public void testGetDay(){
		TimeTools.changeStringToDay("2014-02-27 14:22:00");
	}
	
	/**
	 * 求给定日期的下一天
	 */
	@Test
	public void testDateUtilNextDay(){
		DateUtil du = new DateUtil();
		String nextDay = du.getPreDate("2014-02-12 12:21:00", "d", 1);
		System.out.println(nextDay);
	}
	
	/**
	 * 求给定日期的下半个小时
	 */
	@Test
	public void testDateUtilNextHalfHour(){
		DateUtil du = new DateUtil();
		String nextDay = du.getPreDate("2014-02-12 12:00:00", "m", 30);
		System.out.println(nextDay);
	}
	
	
	/**
	 * 测试一个 集合中，如何去除掉重复内容的对象
	 */
	@Test
	public void testListEntity(){
		KLineBiz kbz = new KLineBiz();
		// 得到数据集合
		List<KLine> list;
		try {
			list = kbz.getKLineList(1, 1, 2000);
			System.out.println("size="+list.size());
			Iterator<KLine> it = list.iterator();
			while (it.hasNext()) {
				KLine k = it.next();
				System.out.println(k.getOpentime()+" open:"+k.getOpen());
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 判断两个日期之间间隔的 天数
	 */
	@Test
	public void testBeginDay2EndDay(){
		DateUtil du = new DateUtil();
		long days = du.getDaySub("2014-01-20 12:00", "2014-01-22 23:00");
		System.out.println(days);
	}
	
	/**
	 * 判断日期是否是周六、周日
	 */
	@Test
	public void checkWeekend(){
		boolean b = ManageWeek.getWeekend("2014-02-28 13:43:00");
		System.out.println("b = "+b);
	}

}
