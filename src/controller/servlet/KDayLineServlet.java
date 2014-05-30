package controller.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.HttpService;

import com.google.gson.Gson;

import entity.KLineJSONEntity;

import util.Constants;
import util.FileTools;

public class KDayLineServlet extends HttpServlet {

	String separator = System.getProperty("file.separator"); // 文件分割符号

	// 生成网络服务对象
	HttpService httpService = new HttpService();
	// 容器对象，获得真实路径
	ServletContext servletContext = null;
	// gson对象
	Gson gson = new Gson();

	/**
	 * Constructor of the object.
	 */
	public KDayLineServlet() {
		super();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		//先创建出一个  上下文对象
		servletContext = this.getServletContext();
		// 手动运行一次，生成日 K 线并把 json 写入文件
		this.runonce();
		
		String realPath = servletContext.getRealPath("jsons") + separator;

		System.out.println("---------------------手动生成 1 日 K 线---------------------");
		realPath = realPath + "onedayjson.txt";
		File jsonfile = new File(realPath);
		if (!jsonfile.exists()) {
			jsonfile.createNewFile();
		}
		

		FileTools ft = new FileTools();
		String jsonstr = ft.readStringFromFile(realPath);

		// //////////////////////////////
		// 把生成的路径，输出为 json
		PrintWriter out = response.getWriter();
		out.println(jsonstr);

		out.flush();
		out.close();

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doGet(request, response);
	}

	/**
	 * 手动调用一次，生成当天 日 K 线
	 */
	public void runonce() {
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

		// System.out.println("----------------1天，K线图-----------------");
	}

}
