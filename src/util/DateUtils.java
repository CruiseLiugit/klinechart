package util;

import java.util.Calendar;
import java.util.Date;

/**
 * 设置定时器工作的时间点
 * 
 * @author lililiu
 *
 */
public class DateUtils {
	/**
	 * 无延迟
	 */
	public static final long NO_DELAY = 0;
	
	//定义  1 分钟常量
	public static final long MILLIS_IN_MINUTE= 1000*60;
	
	//定义  5 分钟常量
	public static final long MILLIS_IN_FIVE_MINUTE= MILLIS_IN_MINUTE*5;
	
	//定义  30 分钟常量
	public static final long MILLIS_IN_HALF_HOURS= MILLIS_IN_MINUTE*30;
		
	//定义  1天 常量
	public static final long PERIOD_DAY= MILLIS_IN_MINUTE*60*24;
	
	//定义每天凌晨 5:00 执行
	public static Date getDayTime() {
		Calendar calendar = Calendar.getInstance();

		// 定制每日5:00执行方法
		calendar.set(Calendar.HOUR_OF_DAY, 12);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		// 第一次执行定时任务的时间
		Date date = calendar.getTime();
		
		return date;

		// 如果第一次执行定时任务的时间 小于 当前的时间
		// 此时要在 第一次执行定时任务的时间 加一天，以便此任务在下个时间点执行。
		//如果不加一天，任务会立即执行。
		/*
		if (date.before(new Date())) {
			date = this.addDay(date, 1);
		}
		*/
		
		//Timer timer = new Timer();
		//SayHello task = new SayHello();
		// 安排指定的任务在指定的时间开始进行重复的固定延迟执行。
		//timer.schedule(task, date, PERIOD_DAY);
	}
	
	
}
