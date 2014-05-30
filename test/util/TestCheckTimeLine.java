package util;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import biz.KLineBiz;

import entity.KLine;

public class TestCheckTimeLine {
	private KLineBiz kbz = new KLineBiz();
	
	/**
	 * 测试网上取的 1400 个数据，看数据中 哪天占多数
	 */
	@Test
	public void testCheckDay(){
		try {
			List<KLine> list = kbz.getKLineList(1, 1, 1400);
			
			String day = CheckTimeInList.getListDay(list);
			System.out.println(day);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * 根据不同的季节，获取新的 数据，存放图中需要的 时间点
	 */
	@Test
	public void testGetOneDayList(){
		try {
			List<KLine> list = kbz.getKLineList(1, 1, 1400);
			
			List<KLine> list2 = CheckTimeInList.checkList(list);
			System.out.println(list2.size());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据任意一个时间，获得第二天的 凌晨 4:00 时间点
	 */
	@Test
	public void testTimeBefore4(){
		DateUtil du = new DateUtil();
		String nextDay = du.getPreDate("2014-03-03 23:59:00", "d",1);
		//这里求固定到第二天的 4:00
		Date upper = TimeTools.getString2DataEarly4(nextDay);
		
	}
	

}
