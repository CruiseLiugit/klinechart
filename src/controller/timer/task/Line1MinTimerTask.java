package controller.timer.task;

import java.util.TimerTask;
import javax.servlet.ServletContext;

import util.Constants;
import util.FileTools;

import net.HttpService;
import com.google.gson.Gson;
import entity.KLineJSONEntity;

/**
 * 1 分钟分时图定时任务
 * @author lililiu
 *
 */
public class Line1MinTimerTask extends TimerTask {
	// 生成网络服务对象
	HttpService httpService = new HttpService();
	// 容器对象，获得真实路径
	ServletContext servletContext = null;

	// gson对象
	Gson gson = new Gson();

	public Line1MinTimerTask(){}
	
	public Line1MinTimerTask(ServletContext context){
		this.servletContext = context;
	}
	
	@Override
	public void run() {
		// 发出 HTTP 请求，调用接口，生成 1 分钟图。
		// 4 种 产品,产品类型 1天通银，2天通钯金，3天通铂金，4天通镍
		String url1 = Constants.Line1MinURL + "1";
		String url2 = Constants.Line1MinURL + "2";
		String url3 = Constants.Line1MinURL + "3";
		String url4 = Constants.Line1MinURL + "4";

		System.out.println("----------------1分钟，分时图-----------------");
		System.out.println("********** 分时图 url1 = " + url1);

		try {
			String jsonStr1 = httpService.doGet(url1);
			String jsonStr2 = httpService.doGet(url2);
			String jsonStr3 = httpService.doGet(url3);
			String jsonStr4 = httpService.doGet(url4);

			// 转化为对象，再转化为数组
			Gson gson = new Gson();
			KLineJSONEntity entity1 = gson.fromJson(jsonStr1,
					KLineJSONEntity.class);
			KLineJSONEntity entity2 = gson.fromJson(jsonStr2,
					KLineJSONEntity.class);
			KLineJSONEntity entity3 = gson.fromJson(jsonStr3,
					KLineJSONEntity.class);
			KLineJSONEntity entity4 = gson.fromJson(jsonStr4,
					KLineJSONEntity.class);

			KLineJSONEntity[] arr = { entity1, entity2, entity3, entity4 };
			String jsonarr = gson.toJson(arr);
			 System.out.println("-----1 分钟分时图: " + jsonarr);

			// 把这个 json 写入一个文本文件
			FileTools ft = new FileTools();
			ft.createJsonFile(servletContext,jsonarr, "linejson.txt");
			
		} catch (Exception e) {
			try {
				throw new Exception("网络连接失败！");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		
	}

	

	
}
