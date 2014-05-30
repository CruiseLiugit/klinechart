package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间帮助类
 * 
 * @version $Id: DateUtil.java,v 1.1 2008/05/28 04:29:52 linan Exp $
 * @author LiNan
 */
public class DateUtil {
	private Calendar calendar = Calendar.getInstance();

	/**
	 * 得到当前的时间，时间格式yyyy-MM-dd
	 * 
	 * @return
	 */
	public String getCurrentDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}

	/**
	 * 得到当前的时间,自定义时间格式 y 年 M 月 d 日 H 时 m 分 s 秒
	 * 
	 * @param dateFormat
	 *            输出显示的时间格式
	 * @return
	 */
	public String getCurrentDate(String dateFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(new Date());
	}

	/**
	 * 日期格式化，默认日期格式yyyy-MM-dd
	 * 
	 * @param date
	 * @return
	 */
	public String getFormatDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}

	/**
	 * 日期格式化，自定义输出日期格式
	 * 
	 * @param date
	 * @return
	 */
	public String getFormatDate(Date date, String dateFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(date);
	}

	/**
	 * 返回当前日期的前后一个时间日期，
	 * 
	 * amount为正数 当前时间后的时间 
	 *        为负数 当前时间前的时间 默认日期格式yyyy-MM-dd
	 *        
	 * @param date 要得到的时间标准，字符串  "yyyy-MM-dd HH:mm:ss"
	 * 
	 * @param field
	 *            日历字段 y 年 M 月 d 日 H 时 m 分 s 秒
	 * @param amount
	 *            数量
	 *            
	 * @return 一个日期，指定的年或月或日或 时分秒，加上一个数字后的时间
	 */
	public String getPreDate(String date ,String field, int amount) {
		java.util.Date dt = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			dt = df.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			//System.out.println("参数格式不正确，yyyy-MM-dd HH:mm:ss");
			e.printStackTrace();
		}
		
		calendar.setTime(dt);
		if (field != null && !field.equals("")) {
			if (field.equals("y")) {
				calendar.add(calendar.YEAR, amount);
			} else if (field.equals("M")) {
				calendar.add(calendar.MONTH, amount);
			} else if (field.equals("d")) {
				calendar.add(calendar.DAY_OF_MONTH, amount);
			} else if (field.equals("H")) {
				calendar.add(calendar.HOUR, amount);
			}else if (field.equals("m")) {
				calendar.add(calendar.MINUTE, amount);
			}
		} else {
			return null;
		}
		return getFormatDate(calendar.getTime());
	}

	/**
	 * 某一个日期的前一个日期
	 * 
	 * @param d
	 *            ,某一个日期
	 * @param field
	 *            日历字段 y 年 M 月 d 日 H 时 m 分 s 秒
	 * @param amount
	 *            数量
	 * @return 一个日期
	 */
	public String getPreDate(Date d, String field, int amount) {
		calendar.setTime(d);
		if (field != null && !field.equals("")) {
			if (field.equals("y")) {
				calendar.add(calendar.YEAR, amount);
			} else if (field.equals("M")) {
				calendar.add(calendar.MONTH, amount);
			} else if (field.equals("d")) {
				calendar.add(calendar.DAY_OF_MONTH, amount);
			} else if (field.equals("H")) {
				calendar.add(calendar.HOUR, amount);
			}
		} else {
			return null;
		}
		return getFormatDate(calendar.getTime());
	}

	/**
	 * 某一个时间的前一个时间
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public String getPreDate(String date) throws ParseException {
		Date d = new SimpleDateFormat().parse(date);
		String preD = getPreDate(d, "d", 1);
		Date preDate = new SimpleDateFormat().parse(preD);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(preDate);
	}
	
	
	/**
     * <li>功能描述：时间相减得到天数
     * @param beginDateStr
     * @param endDateStr
     * @return
     * long 
     * @author Administrator
     */
    public  long getDaySub(String beginDateStr,String endDateStr)
    {
        long day=0;
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");    
        java.util.Date beginDate;
        java.util.Date endDate;
        try
        {
            beginDate = format.parse(beginDateStr);
            endDate= format.parse(endDateStr);    
            day=(endDate.getTime()-beginDate.getTime())/(24*60*60*1000);    
            //System.out.println("相隔的天数="+day);   
        } catch (ParseException e)
        {
            // TODO 自动生成 catch 块
            e.printStackTrace();
        }   
        return day;
    }
    
    /**
     * <li>功能描述：判断两个时间之间间隔，以半个小时为单位
     * @param beginDateStr
     * @param endDateStr
     * @return
     * long 
     * @author Administrator
     */
    public  long getHourSub(String beginDateStr,String endDateStr)
    {
        long hours=0;
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");    
        java.util.Date beginDate;
        java.util.Date endDate;
        try
        {
            beginDate = format.parse(beginDateStr);
            endDate= format.parse(endDateStr);    
            hours=(endDate.getTime()-beginDate.getTime())/(30*60*1000);    
            //System.out.println("相隔的天数="+day);   
        } catch (ParseException e)
        {
            // TODO 自动生成 catch 块
            e.printStackTrace();
        }   
        return hours;
    }
    
    /**
     * <li>功能描述：判断两个时间之间间隔，以半个小时为单位
     * @param beginDateStr
     * @param endDateStr
     * @return
     * long 
     * @author Administrator
     */
    public  long getMinitSub(String beginDateStr,String endDateStr)
    {
        long minutes=0;
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");    
        java.util.Date beginDate;
        java.util.Date endDate;
        try
        {
            beginDate = format.parse(beginDateStr);
            endDate= format.parse(endDateStr);    
            minutes=(endDate.getTime()-beginDate.getTime())/(5*60*1000);    
            //System.out.println("相隔的分钟="+minutes);   
        } catch (ParseException e)
        {
            // TODO 自动生成 catch 块
            e.printStackTrace();
        }   
        return minutes;
    }
    
    
    
    
}