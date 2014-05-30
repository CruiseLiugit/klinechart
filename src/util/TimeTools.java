package util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

//K 线图数据集中的 开盘时间参数类型
import org.jfree.data.time.Day;

import org.jfree.data.time.Minute;

public class TimeTools {

	// 得到系统当前时间
	public static Timestamp getCurrentTime() {
		Timestamp d = new Timestamp(System.currentTimeMillis());
		return d;
	}

	//得到系统当前时间
	//
	/**
	 * 得到精确到分钟的时间
	 * @return 返回当前系统的时间
	 */
	public static String getCurrentTimeWithMinute() {
		return TimeTools.getString2Time(TimeTools.getCurrentTime());
	}

	public static Day changeStringToDay(String date) {
		Day day = null;

		Date opentime = TimeTools.getString2Data2(date);
		// //分别取出年、月、日
		// Calendar cal = Calendar.getInstance();
		// cal.setTime(opentime);
		// int year = cal.get(Calendar.YEAR);
		// int month = cal.get(Calendar.MONTH+1);
		// int dayofmonth = cal.get(Calendar.DAY_OF_MONTH);
		//
		// // 当前时：HOUR_OF_DAY-24小时制
		// int hour24 = cal.get(Calendar.HOUR_OF_DAY); // HOUR-12小时制
		// int hour12 = cal.get(Calendar.HOUR);
		// // 当前分
		// int minute = cal.get(Calendar.MINUTE);
		// // 当前秒
		// int second = cal.get(Calendar.SECOND);
		// // 星期几 Calendar.DAY_OF_WEEK用数字（1~7）表示（星期日~星期六）
		// //System.out.println(year+"-"+month+"-"+dayofmonth+" "+hour24+":"+minute+":"+second);
		day = new Day(opentime);

		return day;
	}

	public static Minute changeStringToMinute(String date) {
		Minute minute = null;

		Date opentime = TimeTools.getString2Data2(date);
		// //分别取出年、月、日
		// Calendar cal = Calendar.getInstance();
		// cal.setTime(opentime);
		// int year = cal.get(Calendar.YEAR);
		// int month = cal.get(Calendar.MONTH+1);
		// int dayofmonth = cal.get(Calendar.DAY_OF_MONTH);
		//
		// // 当前时：HOUR_OF_DAY-24小时制
		// int hour24 = cal.get(Calendar.HOUR_OF_DAY); // HOUR-12小时制
		// int hour12 = cal.get(Calendar.HOUR);
		// // 当前分
		// int minute = cal.get(Calendar.MINUTE);
		// // 当前秒
		// int second = cal.get(Calendar.SECOND);
		// // 星期几 Calendar.DAY_OF_WEEK用数字（1~7）表示（星期日~星期六）
		// //System.out.println(year+"-"+month+"-"+dayofmonth+" "+hour24+":"+minute+":"+second);
		minute = new Minute(opentime);

		return minute;
	}

	/**
	 * 根据季节和时间，判断当前时间点的数据是否是正在进行每天的核算
	 * 
	 * @param season
	 *            季节，夏季传入 1，条件是 4:00--6:00 ; 冬季传入2，条件是 4:00--7:00 ;
	 * @param time
	 *            要判断时间
	 * 
	 * @return true 表示正在核算
	 */
	public static boolean checkIsToday(int season, String time) {
		boolean flag = false;
		// 要判断的时间
		Date checkTime = getString2DataHourMins(time);

		// 设置时间为当天 6 点
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(checkTime);
		cal1.set(cal1.HOUR_OF_DAY, 6);
		cal1.set(Calendar.MINUTE, 0);
		cal1.set(Calendar.SECOND, 0);

		// 设置时间为当天 7 点
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(checkTime);
		cal2.set(cal2.HOUR_OF_DAY, 7);
		cal2.set(Calendar.MINUTE, 0);
		cal2.set(Calendar.SECOND, 0);

		// 求当天凌晨 4 点
		Calendar cal3 = Calendar.getInstance();
		cal3.setTime(checkTime);
		cal3.set(cal3.HOUR_OF_DAY, 4);
		cal3.set(Calendar.MINUTE, 0);
		cal3.set(Calendar.SECOND, 0);

		// 判断季节
		if (season == 1) {
			// 夏季 22 小时，一天共 79200000 个数据
			// 早上 6:00 开始，第二天凌晨 4:00 结束
			// System.out.println("夏令时，time ="+time);
			// 6:00-23:59
			if (checkTime.getTime() < cal1.getTimeInMillis()
					&& checkTime.getTime() > cal3.getTimeInMillis()) {
				//System.out.println("是夏令时一天的数据  凌晨 4:00 到 凌晨 6:00 开始之间的数据，休市核算");
				flag = true;
			}

		}
		if (season == 2) {
			// 冬季
			// System.out.println("冬令时, time ="+time);
			// 早上 7:00 开始，凌晨 4:00 结束 21 小时
			if (checkTime.getTime() < cal2.getTimeInMillis()
					&& checkTime.getTime() > cal3.getTimeInMillis()) {
				//System.out.println("是冬令时一天的数据  凌晨 4:00 到 凌晨 7:00 开始之间的数据，休市核算");
				flag = true;
			}
		}

		return flag;
	}

	/**
	 * 判断给定时间所属的 是夏令时还是 冬令时 夏令时:3月第二个周日---11月第一个周六 冬令时:11月第一个周日---3月第二个周六
	 * 
	 * 夏令时 4:00-6:00 冬令时 4:00-7:00
	 * 
	 * 休市时间 交易时间：周一早 8:00 至 周六早 4:00(结算时间、法定价格、国际市场休息外) 结算休市时间 交易日内，夏令时为 4:00 --
	 * 6:00 ；冬令时为 4:00 -- 7:00
	 * 
	 * @param date
	 *            格式 yyyy-MM-dd HH:mm:ss
	 * @return 返回1 表示当前是夏令时 返回2 表示当前是冬令时 返回0 表示参数错误
	 */
	public static int checkSeason(String date) {
		int result = 0;
		// 1 参数检测
		java.util.Date dt = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			dt = df.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			//System.out.println("参数格式不正确，yyyy-MM-dd HH:mm:ss");
			e.printStackTrace();
			return result;
		}

		// 2 转换为日历对象，详细判断日期参数
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		// 3 检查这个日历中的 月份、周数，看是否符合 夏令时、冬令时规定
		// 月份、周数
		int month = cal.get(Calendar.MONTH) + 1;
		int week = cal.get(Calendar.DAY_OF_WEEK_IN_MONTH);
		// System.out.println("当前时间是 "+month+" 月，是第"+week+" 周");

		// 夏令时:3月第二个周日---11月第一个周六
		if (month >= 3 && month <= 11) {
			if (month == 3 && week >= 3 || month == 11 && week <= 1) {
				result = 1;
			} else if (month > 3 && month < 11) {
				result = 1;
			}
		}

		// 冬令时:11月第一个周日---3月第二个周六
		if (month <= 3 || month >= 11) {
			if (month == 3 && week < 3 || month == 11 && week > 1) {
				result = 2;
			} else if (month < 3 || month > 11) {
				result = 2;
			}
		}

		return result;
	}

	/**
	 * @author LuoB.
	 * @param oldTime
	 *            较小的时间
	 * @param newTime
	 *            较大的时间 (如果为空 默认当前时间 ,表示和当前时间相比)
	 * @return -1 ：同一天. 0：昨天 . 1 ：至少是前天.
	 * @throws ParseException
	 *             转换异常
	 */
	public static int isYeaterday(Date oldTime, Date newTime)
			throws ParseException {
		if (newTime == null) {
			newTime = new Date();
		}
		// 将下面的 理解成 yyyy-MM-dd 00：00：00 更好理解点
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String todayStr = format.format(newTime);
		Date today = format.parse(todayStr);
		// 昨天 86400000=24*60*60*1000 一天
		if ((today.getTime() - oldTime.getTime()) > 0
				&& (today.getTime() - oldTime.getTime()) <= 86400000) {
			return 0;
		} else if ((today.getTime() - oldTime.getTime()) <= 0) { // 至少是今天
			return -1;
		} else { // 至少是前天
			return 1;
		}

	}

	// Timestamp 与String类型相互转换
	// Time -> String
	public static String getString2Time(Timestamp time) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 定义格式
		String str = df.format(time);
		return str;
	}

	// Time -> String
	public static String getTime2String1(Timestamp time) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");// 定义格式
		String str = df.format(time);
		return str;
	}

	// Time -> String
	public static String getTime2String2(Timestamp time) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 定义格式
		String str = df.format(time);
		return str;
	}

	// java.util.Date -> String
	public static String getUtilDate2String1(Date time) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");// 定义格式
		String str = df.format(time);
		return str;
	}

	// String -> Time
	public static Timestamp getTime2String(String str) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Timestamp ts = null;
		try {
			java.util.Date dt = df.parse(str);
			ts = new Timestamp(dt.getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ts;
	}

	public static java.util.Date getString2DataHourMins(String str) {
		java.util.Date dt = null;
		// 2014-01-06 16:25:00
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Timestamp ts = null;
		try {
			dt = df.parse(str);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dt;
	}

	public static java.util.Date getString2Data(String str) {
		java.util.Date dt = null;
		// 2014-01-06 16:25:00
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd ");
		Timestamp ts = null;
		try {
			dt = df.parse(str);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dt;
	}

	public static java.util.Date getString2Data2(String str) {
		java.util.Date dt = null;
		// 2014-01-06 16:25:00
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Timestamp ts = null;
		try {
			dt = df.parse(str);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dt;
	}

	/**
	 * 得到一个其他 凌晨 4:00 的时刻
	 * 
	 * @param str
	 * @return
	 */
	public static java.util.Date getString2DataEarly4(String str) {
		java.util.Date dt = null;
		// 2014-01-06 16:25:00
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Timestamp ts = null;
		try {
			java.util.Date dt1 = df.parse(str);
			// 得到日期的 天、小时
			Calendar caltemp = Calendar.getInstance();
			caltemp.setTime(dt1);
			// 3 检查这个日历中的 月份、周数，看是否符合 夏令时、冬令时规定
			// 得到年、月、日、
			int tempYear = caltemp.get(Calendar.YEAR);
			int tempMonth = caltemp.get(Calendar.MONTH) + 1;
			int tempDay = caltemp.get(Calendar.DAY_OF_MONTH);
			int tempHour = 4; // 24 小时
			String time = tempYear + "-" + tempMonth + "-" + tempDay
					+ " 4:00:00";
			//System.out.println("time =" + time);
			dt = df.parse(time);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dt;
	}

	// java.sql.Date与java.sql.Timestamp相互转换
	// java.sql.Date--->java.sql.Timestamp
	public static Timestamp sqlDate2Time(java.sql.Date date) {
		return new java.sql.Timestamp(date.getTime());
	}

	// java.sql.Timestamp-->java.sql.Date
	public static java.sql.Date time2SqlDate(Timestamp time) {
		return new java.sql.Date(time.getTime());
	}

	/**
	 * 2014年2约20日 新增方法 判断两个时间点之间时间间隔是否满足第三个参数的要求
	 * 
	 * 
	 * @return
	 */
	public static boolean checkTimeBetweenTime() {
		boolean flag = false;

		return flag;
	}

	// ////////////////////////////////////////////////
	/**
	 * 检查日期格式
	 * 
	 * @param date
	 * @return
	 * 
	 *         public static boolean checkDate(String date) { String eL =
	 *         "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1][0-9])|([2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$"
	 *         ; Pattern p = Pattern.compile(eL); Matcher m = p.matcher(date);
	 *         boolean b = m.matches(); return b; }
	 */

	/**
	 * 检查整数
	 * 
	 * @param num
	 * @param type
	 *            "0+":非负整数 "+":正整数 "-0":非正整数 "-":负整数 "":整数
	 * @return
	 * 
	 *         public static boolean checkNumber(String num,String type){ String
	 *         eL = ""; if(type.equals("0+"))eL = "^\\d+$";//非负整数 else
	 *         if(type.equals("+"))eL = "^\\d*[1-9]\\d*$";//正整数 else
	 *         if(type.equals("-0"))eL = "^((-\\d+)|(0+))$";//非正整数 else
	 *         if(type.equals("-"))eL = "^-\\d*[1-9]\\d*$";//负整数 else eL =
	 *         "^-?\\d+$";//整数 Pattern p = Pattern.compile(eL); Matcher m =
	 *         p.matcher(num); boolean b = m.matches(); return b; }
	 */

	/**
	 * 检查浮点数
	 * 
	 * @param num
	 * @param type
	 *            "0+":非负浮点数 "+":正浮点数 "-0":非正浮点数 "-":负浮点数 "":浮点数
	 * @return
	 * 
	 *         public static boolean checkFloat(String num,String type){ String
	 *         eL = ""; if(type.equals("0+"))eL = "^\\d+(\\.\\d+)?$";//非负浮点数
	 *         else if(type.equals("+"))eL =
	 *         "^((\\d+\\.\\d*[1-9]\\d*)|(\\d*[1-9]\\d*\\.\\d+)|(\\d*[1-9]\\d*))$"
	 *         ;//正浮点数 else if(type.equals("-0"))eL =
	 *         "^((-\\d+(\\.\\d+)?)|(0+(\\.0+)?))$";//非正浮点数 else
	 *         if(type.equals("-"))eL =
	 *         "^(-((\\d+\\.\\d*[1-9]\\d*)|(\\d*[1-9]\\d*\\.\\d+)|(\\d*[1-9]\\d*)))$"
	 *         ;//负浮点数 else eL = "^(-?\\d+)(\\.\\d+)?$";//浮点数 Pattern p =
	 *         Pattern.compile(eL); Matcher m = p.matcher(num); boolean b =
	 *         m.matches(); return b; }
	 */

	/*
	 * 一、String与Date（java.util.Date）互转
	 * 
	 * 1.1 String -> Date
	 * 
	 * Java代码 收藏代码 String dateStr = "2010/05/04 12:34:23"; Date date = new
	 * Date(); //注意format的格式要与日期String的格式相匹配 DateFormat sdf = new
	 * SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); try { date = sdf.parse(dateStr);
	 * System.out.println(date.toString()); } catch (Exception e) {
	 * e.printStackTrace(); }
	 * 
	 * 
	 * 1.2 Date -> String
	 * 
	 * 日期向字符串转换，可以设置任意的转换格式format Java代码 收藏代码 String dateStr = ""; Date date =
	 * new Date(); //format的格式可以任意 DateFormat sdf = new
	 * SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); DateFormat sdf2 = new
	 * SimpleDateFormat("yyyy-MM-dd HH/mm/ss"); try { dateStr =
	 * sdf.format(date); System.out.println(dateStr); dateStr =
	 * sdf2.format(date); System.out.println(dateStr); } catch (Exception e) {
	 * e.printStackTrace(); } 二、String与Timestamp互转
	 * 
	 * 
	 * 2.1 String ->Timestamp
	 * 
	 * 
	 * 使用Timestamp的valueOf()方法 Java代码 收藏代码 Timestamp ts = new
	 * Timestamp(System.currentTimeMillis()); String tsStr =
	 * "2011-05-09 11:49:45"; try { ts = Timestamp.valueOf(tsStr);
	 * System.out.println(ts); } catch (Exception e) { e.printStackTrace(); }
	 * 注：String的类型必须形如： yyyy-mm-dd hh:mm:ss[.f...] 这样的格式，中括号表示可选，否则报错！！！
	 * 如果String为其他格式，可考虑重新解析下字符串，再重组~~
	 * 
	 * 2.2 Timestamp -> String
	 * 
	 * 使用Timestamp的toString()方法或者借用DateFormat
	 * 
	 * Java代码 收藏代码 Timestamp ts = new Timestamp(System.currentTimeMillis());
	 * String tsStr = ""; DateFormat sdf = new
	 * SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); try { //方法一 tsStr =
	 * sdf.format(ts); System.out.println(tsStr); //方法二 tsStr = ts.toString();
	 * System.out.println(tsStr); } catch (Exception e) { e.printStackTrace(); }
	 * 很容易能够看出来，方法一的优势在于可以灵活的设置字符串的形式。 三、Date（ java.util.Date ）和Timestamp互转
	 * 
	 * 声明：查API可知，Date和Timesta是父子类关系
	 * 
	 * 
	 * 3.1 Timestamp -> Date
	 * 
	 * 
	 * Java代码 收藏代码 Timestamp ts = new Timestamp(System.currentTimeMillis());
	 * Date date = new Date(); try { date = ts; System.out.println(date); }
	 * catch (Exception e) { e.printStackTrace(); }
	 * 很简单，但是此刻date对象指向的实体却是一个Timestamp，即date拥有Date类的方法，但被覆盖的方法的执行实体在Timestamp中。
	 * 
	 * 
	 * 3.2 Date -> Timestamp
	 * 
	 * 父类不能直接向子类转化，可借助中间的String~~~~ 注：使用以下方式更简洁 Timestamp ts = new
	 * Timestamp(date.getTime());
	 * 
	 * 
	 * 
	 * ///得到具体的时间段 final SimpleDateFormat sdf =new
	 * SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); long period=1000*60*24;//间隔执行时间
	 * //设置时间为当天12点钟 Calendar cal = Calendar.getInstance();
	 * cal.set(Calendar.HOUR_OF_DAY, 12); cal.set(Calendar.MINUTE, 0);
	 * cal.set(Calendar.SECOND, 0); //如果当天时间超过12点，定时器启动时间设置为下一天 if(new
	 * Date().getTime() > cal.getTimeInMillis()){ cal.add(Calendar.DAY_OF_YEAR,
	 * 1); } //System.out.println(sdf.format(cal.getTime())); Timer timer = new
	 * Timer(); timer.schedule(new TimerTask() {
	 * 
	 * @Override public void run() { Date now = new Date();
	 * System.out.println(sdf.format(now)+"\t helloworld"); }
	 * },cal.getTime(),period);
	 */

	// 用来全局控制 上一周，本周，下一周的周数变化
	private int weeks = 0;
	private int MaxDate;// 一月最大天数
	private int MaxYear;// 一年最大天数

	/**
	 * @param args
	 * 
	 *            public static void main(String[] args) { 19. Test2 tt = new
	 *            Test2(); 20.
	 *            System.out.println("获取当天日期:"+tt.getNowTime("yyyy-MM-dd")); 21.
	 *            System.out.println("获取本周一日期:"+tt.getMondayOFWeek()); 22.
	 *            System.out.println("获取本周日的日期~:"+tt.getCurrentWeekday()); 23.
	 *            System.out.println("获取上周一日期:"+tt.getPreviousWeekday()); 24.
	 *            System.out.println("获取上周日日期:"+tt.getPreviousWeekSunday()); 25.
	 *            System.out.println("获取下周一日期:"+tt.getNextMonday()); 26.
	 *            System.out.println("获取下周日日期:"+tt.getNextSunday()); 27.
	 *            System.out
	 *            .println("获得相应周的周六的日期:"+tt.getNowTime("yyyy-MM-dd")); 28.
	 *            System.out.println("获取本月第一天日期:"+tt.getFirstDayOfMonth()); 29.
	 *            System.out.println("获取本月最后一天日期:"+tt.getDefaultDay()); 30.
	 *            System.out.println("获取上月第一天日期:"+tt.getPreviousMonthFirst());
	 *            31.
	 *            System.out.println("获取上月最后一天的日期:"+tt.getPreviousMonthEnd());
	 *            32. System.out.println("获取下月第一天日期:"+tt.getNextMonthFirst());
	 *            33. System.out.println("获取下月最后一天日期:"+tt.getNextMonthEnd());
	 *            34.
	 *            System.out.println("获取本年的第一天日期:"+tt.getCurrentYearFirst());
	 *            35. System.out.println("获取本年最后一天日期:"+tt.getCurrentYearEnd());
	 *            36.
	 *            System.out.println("获取去年的第一天日期:"+tt.getPreviousYearFirst());
	 *            37.
	 *            System.out.println("获取去年的最后一天日期:"+tt.getPreviousYearEnd());
	 *            38. System.out.println("获取明年第一天日期:"+tt.getNextYearFirst());
	 *            39. System.out.println("获取明年最后一天日期:"+tt.getNextYearEnd()); 40.
	 *            System.out.println("获取本季度第一天到最后一天:"+tt.getThisSeasonTime(11));
	 *            41.
	 *            System.out.println("获取两个日期之间间隔天数2008-12-1~2008-9.29:"+Test2
	 *            .getTwoDay("2008-12-1","2008-9-29")); 42. }
	 */

	/**
	 * 得到二个日期间的间隔天数
	 * 
	 * public static String getTwoDay(String sj1, String sj2) { 49.
	 * SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd"); 50.
	 * long day = 0; 51. try { 52. java.util.Date date = myFormatter.parse(sj1);
	 * 53. java.util.Date mydate = myFormatter.parse(sj2); 54. day =
	 * (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000); 55. } catch
	 * (Exception e) { 56. return ""; 57. } 58. return day + ""; 59. } 60.
	 */

	/**
	 * 根据一个日期，返回是星期几的字符串
	 * 
	 * @param sdate
	 * @return
	 * 
	 *         68. public static String getWeek(String sdate) { 69. // 再转换为时间
	 *         70. Date date = Test2.strToDate(sdate); 71. Calendar c =
	 *         Calendar.getInstance(); 72. c.setTime(date); 73. // int
	 *         hour=c.get(Calendar.DAY_OF_WEEK); 74. // hour中存的就是星期几了，其范围 1~7
	 *         75. // 1=星期日 7=星期六，其他类推 76. return new
	 *         SimpleDateFormat("EEEE").format(c.getTime()); 77. }
	 */

	/**
	 * 将短时间格式字符串转换为时间 yyyy-MM-dd 81. * 82. * @param strDate 83. * @return 84.
	 * 85. public static Date strToDate(String strDate) { 86. SimpleDateFormat
	 * formatter = new SimpleDateFormat("yyyy-MM-dd"); 87. ParsePosition pos =
	 * new ParsePosition(0); 88. Date strtodate = formatter.parse(strDate, pos);
	 * 89. return strtodate; 90. } 91.
	 */
	/**
	 * 93. * 两个时间之间的天数 94. * 95. * @param date1 96. * @param date2 97. * @return
	 * 98. 99. public static long getDays(String date1, String date2) { 100. if
	 * (date1 == null || date1.equals("")) 101. return 0; 102. if (date2 == null
	 * || date2.equals("")) 103. return 0; 104. // 转换为标准时间 105. SimpleDateFormat
	 * myFormatter = new SimpleDateFormat("yyyy-MM-dd"); 106. java.util.Date
	 * date = null; 107. java.util.Date mydate = null; 108. try { 109. date =
	 * myFormatter.parse(date1); 110. mydate = myFormatter.parse(date2); 111. }
	 * catch (Exception e) { 112. } 113. long day = (date.getTime() -
	 * mydate.getTime()) / (24 * 60 * 60 * 1000); 114. return day; 115. } 116.
	 * 
	 * // 计算当月最后一天,返回字符串 public String getDefaultDay(){ 122. String str = "";
	 * 123. SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 124. 125.
	 * Calendar lastDate = Calendar.getInstance(); 126.
	 * lastDate.set(Calendar.DATE,1);//设为当前月的1号 127.
	 * lastDate.add(Calendar.MONTH,1);//加一个月，变为下月的1号 128.
	 * lastDate.add(Calendar.DATE,-1);//减去一天，变为当月最后一天 129. 130.
	 * str=sdf.format(lastDate.getTime()); 131. return str; 132. } 133. 134. //
	 * 上月第一天 135. public String getPreviousMonthFirst(){ 136. String str = "";
	 * 137. SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 138. 139.
	 * Calendar lastDate = Calendar.getInstance(); 140.
	 * lastDate.set(Calendar.DATE,1);//设为当前月的1号 141.
	 * lastDate.add(Calendar.MONTH,-1);//减一个月，变为下月的1号 142.
	 * //lastDate.add(Calendar.DATE,-1);//减去一天，变为当月最后一天 143. 144.
	 * str=sdf.format(lastDate.getTime()); 145. return str; 146. } 147. 148.
	 * //获取当月第一天 149. public String getFirstDayOfMonth(){ 150. String str = "";
	 * 151. SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 152. 153.
	 * Calendar lastDate = Calendar.getInstance(); 154.
	 * lastDate.set(Calendar.DATE,1);//设为当前月的1号 155.
	 * str=sdf.format(lastDate.getTime()); 156. return str; 157. } 158. 159. //
	 * 获得本周星期日的日期 160. public String getCurrentWeekday() { 161. weeks = 0; 162.
	 * int mondayPlus = this.getMondayPlus(); 163. GregorianCalendar currentDate
	 * = new GregorianCalendar(); 164. currentDate.add(GregorianCalendar.DATE,
	 * mondayPlus+6); 165. Date monday = currentDate.getTime(); 166. 167.
	 * DateFormat df = DateFormat.getDateInstance(); 168. String preMonday =
	 * df.format(monday); 169. return preMonday; 170. } 171. 172. 173. //获取当天时间
	 * 174. public String getNowTime(String dateformat){ 175. Date now = new
	 * Date(); 176. SimpleDateFormat dateFormat = new
	 * SimpleDateFormat(dateformat);//可以方便地修改日期格式 177. String hehe =
	 * dateFormat.format(now); 178. return hehe; 179. } 180. 181. //
	 * 获得当前日期与本周日相差的天数 182. private int getMondayPlus() { 183. Calendar cd =
	 * Calendar.getInstance(); 184. // 获得今天是一周的第几天，星期日是第一天，星期二是第二天...... 185.
	 * int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK)-1; //因为按中国礼拜一作为第一天所以这里减1
	 * 186. if (dayOfWeek == 1) { 187. return 0; 188. } else { 189. return 1 -
	 * dayOfWeek; 190. } 191. } 192. 193. //获得本周一的日期 194. public String
	 * getMondayOFWeek(){ 195. weeks = 0; 196. int mondayPlus =
	 * this.getMondayPlus(); 197. GregorianCalendar currentDate = new
	 * GregorianCalendar(); 198. currentDate.add(GregorianCalendar.DATE,
	 * mondayPlus); 199. Date monday = currentDate.getTime(); 200. 201.
	 * DateFormat df = DateFormat.getDateInstance(); 202. String preMonday =
	 * df.format(monday); 203. return preMonday; 204. } 205. 206. //获得相应周的周六的日期
	 * 207. public String getSaturday() { 208. int mondayPlus =
	 * this.getMondayPlus(); 209. GregorianCalendar currentDate = new
	 * GregorianCalendar(); 210. currentDate.add(GregorianCalendar.DATE,
	 * mondayPlus + 7 * weeks + 6); 211. Date monday = currentDate.getTime();
	 * 212. DateFormat df = DateFormat.getDateInstance(); 213. String preMonday
	 * = df.format(monday); 214. return preMonday; 215. } 216. 217. //
	 * 获得上周星期日的日期 218. public String getPreviousWeekSunday() { 219. weeks=0;
	 * 220. weeks--; 221. int mondayPlus = this.getMondayPlus(); 222.
	 * GregorianCalendar currentDate = new GregorianCalendar(); 223.
	 * currentDate.add(GregorianCalendar.DATE, mondayPlus+weeks); 224. Date
	 * monday = currentDate.getTime(); 225. DateFormat df =
	 * DateFormat.getDateInstance(); 226. String preMonday = df.format(monday);
	 * 227. return preMonday; 228. } 229. 230. // 获得上周星期一的日期 231. public String
	 * getPreviousWeekday() { 232. weeks--; 233. int mondayPlus =
	 * this.getMondayPlus(); 234. GregorianCalendar currentDate = new
	 * GregorianCalendar(); 235. currentDate.add(GregorianCalendar.DATE,
	 * mondayPlus + 7 * weeks); 236. Date monday = currentDate.getTime(); 237.
	 * DateFormat df = DateFormat.getDateInstance(); 238. String preMonday =
	 * df.format(monday); 239. return preMonday; 240. } 241. 242. // 获得下周星期一的日期
	 * 243. public String getNextMonday() { 244. weeks++; 245. int mondayPlus =
	 * this.getMondayPlus(); 246. GregorianCalendar currentDate = new
	 * GregorianCalendar(); 247. currentDate.add(GregorianCalendar.DATE,
	 * mondayPlus + 7); 248. Date monday = currentDate.getTime(); 249.
	 * DateFormat df = DateFormat.getDateInstance(); 250. String preMonday =
	 * df.format(monday); 251. return preMonday; 252. } 253. 254. // 获得下周星期日的日期
	 * 255. public String getNextSunday() { 256. 257. int mondayPlus =
	 * this.getMondayPlus(); 258. GregorianCalendar currentDate = new
	 * GregorianCalendar(); 259. currentDate.add(GregorianCalendar.DATE,
	 * mondayPlus + 7+6); 260. Date monday = currentDate.getTime(); 261.
	 * DateFormat df = DateFormat.getDateInstance(); 262. String preMonday =
	 * df.format(monday); 263. return preMonday; 264. } 265. 266. 267. 268.
	 * private int getMonthPlus(){ 269. Calendar cd = Calendar.getInstance();
	 * 270. int monthOfNumber = cd.get(Calendar.DAY_OF_MONTH); 271.
	 * cd.set(Calendar.DATE, 1);//把日期设置为当月第一天 272. cd.roll(Calendar.DATE,
	 * -1);//日期回滚一天，也就是最后一天 273. MaxDate=cd.get(Calendar.DATE); 274.
	 * if(monthOfNumber == 1){ 275. return -MaxDate; 276. }else{ 277. return
	 * 1-monthOfNumber; 278. } 279. } 280. 281. //获得上月最后一天的日期 282. public String
	 * getPreviousMonthEnd(){ 283. String str = ""; 284. SimpleDateFormat
	 * sdf=new SimpleDateFormat("yyyy-MM-dd"); 285. 286. Calendar lastDate =
	 * Calendar.getInstance(); 287. lastDate.add(Calendar.MONTH,-1);//减一个月 288.
	 * lastDate.set(Calendar.DATE, 1);//把日期设置为当月第一天 289.
	 * lastDate.roll(Calendar.DATE, -1);//日期回滚一天，也就是本月最后一天 290.
	 * str=sdf.format(lastDate.getTime()); 291. return str; 292. } 293. 294.
	 * //获得下个月第一天的日期 295. public String getNextMonthFirst(){ 296. String str =
	 * ""; 297. SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 298.
	 * 299. Calendar lastDate = Calendar.getInstance(); 300.
	 * lastDate.add(Calendar.MONTH,1);//减一个月 301. lastDate.set(Calendar.DATE,
	 * 1);//把日期设置为当月第一天 302. str=sdf.format(lastDate.getTime()); 303. return
	 * str; 304. } 305. 306. //获得下个月最后一天的日期 307. public String
	 * getNextMonthEnd(){ 308. String str = ""; 309. SimpleDateFormat sdf=new
	 * SimpleDateFormat("yyyy-MM-dd"); 310. 311. Calendar lastDate =
	 * Calendar.getInstance(); 312. lastDate.add(Calendar.MONTH,1);//加一个月 313.
	 * lastDate.set(Calendar.DATE, 1);//把日期设置为当月第一天 314.
	 * lastDate.roll(Calendar.DATE, -1);//日期回滚一天，也就是本月最后一天 315.
	 * str=sdf.format(lastDate.getTime()); 316. return str; 317. } 318. 319.
	 * //获得明年最后一天的日期 320. public String getNextYearEnd(){ 321. String str = "";
	 * 322. SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 323. 324.
	 * Calendar lastDate = Calendar.getInstance(); 325.
	 * lastDate.add(Calendar.YEAR,1);//加一个年 326.
	 * lastDate.set(Calendar.DAY_OF_YEAR, 1); 327.
	 * lastDate.roll(Calendar.DAY_OF_YEAR, -1); 328.
	 * str=sdf.format(lastDate.getTime()); 329. return str; 330. } 331. 332.
	 * //获得明年第一天的日期 333. public String getNextYearFirst(){ 334. String str = "";
	 * 335. SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); 336. 337.
	 * Calendar lastDate = Calendar.getInstance(); 338.
	 * lastDate.add(Calendar.YEAR,1);//加一个年 339.
	 * lastDate.set(Calendar.DAY_OF_YEAR, 1); 340.
	 * str=sdf.format(lastDate.getTime()); 341. return str; 342. 343. } 344.
	 * 345. //获得本年有多少天 346. private int getMaxYear(){ 347. Calendar cd =
	 * Calendar.getInstance(); 348. cd.set(Calendar.DAY_OF_YEAR,1);//把日期设为当年第一天
	 * 349. cd.roll(Calendar.DAY_OF_YEAR,-1);//把日期回滚一天。 350. int MaxYear =
	 * cd.get(Calendar.DAY_OF_YEAR); 351. return MaxYear; 352. } 353. 354.
	 * private int getYearPlus(){ 355. Calendar cd = Calendar.getInstance();
	 * 356. int yearOfNumber = cd.get(Calendar.DAY_OF_YEAR);//获得当天是一年中的第几天 357.
	 * cd.set(Calendar.DAY_OF_YEAR,1);//把日期设为当年第一天 358.
	 * cd.roll(Calendar.DAY_OF_YEAR,-1);//把日期回滚一天。 359. int MaxYear =
	 * cd.get(Calendar.DAY_OF_YEAR); 360. if(yearOfNumber == 1){ 361. return
	 * -MaxYear; 362. }else{ 363. return 1-yearOfNumber; 364. } 365. } 366.
	 * //获得本年第一天的日期 367. public String getCurrentYearFirst(){ 368. int yearPlus
	 * = this.getYearPlus(); 369. GregorianCalendar currentDate = new
	 * GregorianCalendar(); 370.
	 * currentDate.add(GregorianCalendar.DATE,yearPlus); 371. Date yearDay =
	 * currentDate.getTime(); 372. DateFormat df = DateFormat.getDateInstance();
	 * 373. String preYearDay = df.format(yearDay); 374. return preYearDay; 375.
	 * } 376. 377. 378. //获得本年最后一天的日期 * 379. public String getCurrentYearEnd(){
	 * 380. Date date = new Date(); 381. SimpleDateFormat dateFormat = new
	 * SimpleDateFormat("yyyy");//可以方便地修改日期格式 382. String years =
	 * dateFormat.format(date); 383. return years+"-12-31"; 384. } 385. 386.
	 * 387. //获得上年第一天的日期 * 388. public String getPreviousYearFirst(){ 389. Date
	 * date = new Date(); 390. SimpleDateFormat dateFormat = new
	 * SimpleDateFormat("yyyy");//可以方便地修改日期格式 391. String years =
	 * dateFormat.format(date); int years_value = Integer.parseInt(years); 392.
	 * years_value--; 393. return years_value+"-1-1"; 394. } 395. 396.
	 * //获得上年最后一天的日期 397. public String getPreviousYearEnd(){ 398. weeks--; 399.
	 * int yearPlus = this.getYearPlus(); 400. GregorianCalendar currentDate =
	 * new GregorianCalendar(); 401.
	 * currentDate.add(GregorianCalendar.DATE,yearPlus
	 * +MaxYear*weeks+(MaxYear-1)); 402. Date yearDay = currentDate.getTime();
	 * 403. DateFormat df = DateFormat.getDateInstance(); 404. String preYearDay
	 * = df.format(yearDay); 405. getThisSeasonTime(11); 406. return preYearDay;
	 * 407. } 408. 409. //获得本季度 410. public String getThisSeasonTime(int month){
	 * 411. int array[][] = {{1,2,3},{4,5,6},{7,8,9},{10,11,12}}; 412. int
	 * season = 1; 413. if(month>=1&&month<=3){ 414. season = 1; 415. } 416.
	 * if(month>=4&&month<=6){ 417. season = 2; 418. } 419.
	 * if(month>=7&&month<=9){ 420. season = 3; 421. } 422.
	 * if(month>=10&&month<=12){ 423. season = 4; 424. } 425. int start_month =
	 * array[season-1][0]; 426. int end_month = array[season-1][2]; 427. 428.
	 * Date date = new Date(); 429. SimpleDateFormat dateFormat = new
	 * SimpleDateFormat("yyyy");//可以方便地修改日期格式 430. String years =
	 * dateFormat.format(date); 431. int years_value = Integer.parseInt(years);
	 * 432. 433. int start_days
	 * =1;//years+"-"+String.valueOf(start_month)+"-1";/
	 * /getLastDayOfMonth(years_value,start_month); 434. int end_days =
	 * getLastDayOfMonth(years_value,end_month); 435. String seasonDate =
	 * years_value
	 * +"-"+start_month+"-"+start_days+";"+years_value+"-"+end_month+"-"
	 * +end_days; 436. return seasonDate; 437. 438. }
	 */

	/**
	 * 获取某年某月的最后一天
	 * 
	 * @param year
	 *            年 443. * @param month 月 444. * @return 最后一天 445. 446. private
	 *            int getLastDayOfMonth(int year, int month) { 447. if (month ==
	 *            1 || month == 3 || month == 5 || month == 7 || month == 8 448.
	 *            || month == 10 || month == 12) { 449. return 31; 450. } 451.
	 *            if (month == 4 || month == 6 || month == 9 || month == 11) {
	 *            452. return 30; 453. } 454. if (month == 2) { 455. if
	 *            (isLeapYear(year)) { 456. return 29; 457. } else { 458. return
	 *            28; 459. } 460. } 461. return 0; 462. }
	 */
	/**
	 * 是否闰年
	 * 
	 * @param year
	 *            年
	 * @return
	 * 
	 *         468. public boolean isLeapYear(int year) { 469. return (year % 4
	 *         == 0 && year % 100 != 0) || (year % 400 == 0); 470. }
	 * */

}
