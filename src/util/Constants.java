package util;

/**
 * All static  var
 * @author SDJG
 *
 */
public final class Constants {
	//网址
	//本地
	public static final String BASEURL = "http://localhost:8080/klinechart/";
	public static final String HTTPURL = BASEURL+"upload/";
	
	
	//外网地址
	//public static final String BASEURL1 = "http://117.185.73.90:8080/klinechart/";
	//内网地址
	//public static final String BASEURL = "http://10.0.1.125:8080/klinechart/";
	
	//图片存放路径
	//输出结果的 json 中使用的路径，要修改为 外网地址
	//public static final String HTTPURL = BASEURL1+"upload/";
	
	//接口定义，type 值会变化， time 、limit 不会变化
	//1 分钟分时图
	public static final String Line1MinURL = BASEURL+"minline?limit=1400&type=";
	
	//5 分钟 K 线图
	public static final String K5MinsURL = BASEURL+"kline?time=2&limit=84&type=";

	//30 分钟 K 线图
	public static final String K30MinsURL = BASEURL+"kline?time=3&limit=84&type=";
	
	//1 天 K 线图
	public static final String K1DayURL = BASEURL+"kline?time=4&limit=70&type=";
	
	
	
	// 菜单项有关的跳转路径
	//Image Frame Dimension 尺寸
    //chartPanel.setPreferredSize(new Dimension(600, 400));
	//正常尺寸
	public static final int ChartPanel_WIDTH = 600; 
	public static final int ChartPanel_HEIGHT = 400;
	//小尺寸
	public static final int ChartPanel_WIDTH_SM = 240; 
	public static final int ChartPanel_HEIGHT_SM = 145;
	
	
	//路径跳转
	public static final String TimeDemo1 = "timedemo1";
	
	public static final String HighLowDemo1 = "hldemo1";
	
	
	public static final String BarDemo1 = "bardemo1";
	
	
	
	
	

}
