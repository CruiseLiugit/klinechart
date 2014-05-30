package controller.timer.listener;

import java.util.Timer;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import util.DateUtils;
import controller.timer.task.KDayTimerTask;


/**
 * 为了在 Tomcat 服务器自动调用并生成图像。
 * 需要定义如下定时器
 * 1、生成日K 线的定时器，每天凌晨 5:00 运行，生成前一天的 日 K 线
 *    这个定时器，每天新创建一个 json 
 * 2、生成 5 分钟 K 线的定时器，每 5 分钟运行一次，生成一张 5 分钟 K 线图 
 * 3、生成 30 分钟 K 线的定时器，每 30 分钟运行一次，生成一张 30 分钟 K 线图
 * 4、生成 分时曲线图定时器，每 1 分钟运行一次，生成一张 1 分钟 K 线图
 * 
 * @author lililiu
 *
 */
public class KDayTimerManager implements ServletContextListener {
	
	/**
	 * 定时器
	 */
	private Timer timer;

	
	/**
	  * 在Web应用结束时停止任务
	  */
	public void contextDestroyed(ServletContextEvent arg0) {
		timer.cancel(); // 定时器销毁
	}

	/**
	  * 在Web应用启动时初始化任务
	  */
	public void contextInitialized(ServletContextEvent event) {
		 //定义定时器
		  timer = new Timer("1天K线图定时器",true);
		  
		  ServletContext servletContext = event.getServletContext();
		
		  //每天凌晨 5 点执行
		  //DateUtils.NO_DELAY
		  timer.schedule(new KDayTimerTask(servletContext),DateUtils.getDayTime(), DateUtils.PERIOD_DAY);
	}

	

}
