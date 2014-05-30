package controller.timer.task;

import java.util.TimerTask;

import javax.servlet.ServletContext;

import net.HttpService;
import util.Constants;
import util.FileTools;

import com.google.gson.Gson;

import entity.KLineJSONEntity;

public class KDayTimerTask extends TimerTask {
	// 生成网络服务对象
	HttpService httpService = new HttpService();
	// 容器对象，获得真实路径
	ServletContext servletContext = null;

	// gson对象
	Gson gson = new Gson();

	public KDayTimerTask() {
	}

	public KDayTimerTask(ServletContext context) {
		this.servletContext = context;
	}

	@Override
	public void run() {
		// 发出 HTTP 请求，调用接口，生成 1 分钟图。
		// 4 种 产品,产品类型 1天通银，2天通钯金，3天通铂金，4天通镍
		String url1 = Constants.K1DayURL + "1";
		String url2 = Constants.K1DayURL + "2";
		String url3 = Constants.K1DayURL + "3";
		String url4 = Constants.K1DayURL + "4";

		// System.out.println("********** url1 = " + url1);

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
			// System.out.println("-----定时分时图: " + jsonarr);

			// 把这个 json 写入一个文本文件
			FileTools ft = new FileTools();
			ft.createJsonFile(servletContext, jsonarr, "onedayjson.txt");

		} catch (Exception e) {
			try {
				throw new Exception("网络连接失败！");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		//System.out.println("----------------1天，K线图-----------------");
	}

}
