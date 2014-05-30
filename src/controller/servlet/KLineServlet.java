package controller.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.kline.KLineDemo;

//import org.apache.struts2.ServletActionContext;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.servlet.ServletUtilities;

import util.Constants;
import util.TimeTools;

import com.google.gson.Gson;

import entity.KLineJSONEntity;

public class KLineServlet extends HttpServlet {

	String separator = System.getProperty("file.separator"); // 文件分割符号
	// 图片上传
	private static final int BUFFER_SIZE = 16 * 1024;

	private String type; // 类型
	private String time; // 时长
	private String limit; // 分页条数
	private String matype; // MA 类型
	
	/**
	 * K 线图 对应的 Action 参考 http://jincaishen.com.cn/hq/details?symbol=TJXAG
	 * 
	 * 为 K 线图中的 MA 均线1
	 * 
	 * @param type
	 *            1--天通银，2--天通钯金，3--天通铂金，4--天通镍
	 * @param time
	 *            1--1分钟，2--5分钟，3--30分钟，4--1天
	 * @param limit
	 *            默认 10条，求 K 线图中的 MA 数据条数要根据当前条数往前取 5 条、10条、20条数据
	 * @param matype
	 *            MA 线的类型，均线分为 MA5 MA10 MA20 三条线
	 * 
	 *            计算公式： MA = (C1+C2+C3+C4+C5+....+Cn)/n C 为收盘价，n 为移动平均周期数
	 * 
	 *            例如，现货黄金的 5 日移动平均价格计算方法为： MA 5 =
	 *            （前四天收盘价+前三天收盘价+前天收盘价+昨天收盘价+今天收盘价）/5
	 * 
	 * @author Liulili
	 * 
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		HttpSession httpsession = request.getSession();
		//使用HttpSession对象的方法setMaxInactiveInterVal(int)设置一个会话维持非活动状态的最大秒数。
		httpsession.setMaxInactiveInterval(2);

		type = request.getParameter("type");
		time = request.getParameter("time");
		limit = request.getParameter("limit");

		//System.out.println("得到页面数据 type =" + type + " , time=" + time+ " , limit = " + limit);

		String[] titlearr = this.createTitleByType(type);
		int typeint = new Integer(type.trim()).intValue();
		int timeint = new Integer(time.trim()).intValue();
		int limitint = new Integer(limit.trim()).intValue();

		// //////////////////////////////////////////
		// 从 tomcat/temp 目录取出图片，写入 upload 目录
		ServletContext servletContext = this.getServletContext();
		String path = servletContext.getRealPath("/");
		String realPath = servletContext.getRealPath("upload") + separator;
		// 判断目录是否存在，不存在先创建
		File dir = new File(realPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		// //得到 tomcat/path 路径
		//System.out.println("-------------------目录分割线   " + separator);

		String tomcatpath = "";
		if ("/".equals(separator.trim())) {
			// Mac os
			tomcatpath = path.replaceAll("webapps" + separator + "klinechart"
					+ separator + "$", "temp");
		} else {
			// window 系统
			tomcatpath = path.replaceAll("webapps" + separator + separator
					+ "klinechart" + separator + separator + "$", "temp");
		}

		//System.out.println("----------------------path  = " + path);
		//System.out.println("----------------------realPath =" + realPath);
		//System.out.println("----------------------tomcatpath = " + tomcatpath);
		// ////////////////////////////////////////

		// 杀死session
		// session.invalidate();
		// //////////////////////////////////////////
		// 得到 icon 图片路径
		String iconpath = realPath + "gorilla.jpg";
		//System.out.println("icon 图片路径  :" + iconpath);

		// 生成图片
		KLineDemo chart = new KLineDemo();
		String filename = "";
		
		switch (timeint) {
		case 2:
			System.out.println("---------------------5 分钟 K 线---------------------");
			filename = chart.generateKLineChartFor5(typeint, timeint, limitint,
					titlearr[0], iconpath, httpsession, new PrintWriter(System.out));
			break;
		case 3:
			System.out.println("---------------------30分钟 K 线---------------------");
			filename = chart.generateKLineChartFor30(typeint, timeint, limitint,
					titlearr[0], iconpath, httpsession, new PrintWriter(System.out));
			break;
		case 4:
			System.out.println("---------------------1 日 K 线---------------------");
			filename = chart.generateKLineChartNew(typeint, timeint, limitint, titlearr[0],
					iconpath, httpsession, new PrintWriter(System.out));
			break;
		default:
			System.out.println("---时间类型 ，不提供服务---");
			break;
		}
		

		//System.out.println("---------------------filename = " + filename);
		// 整合得到 tomcat/temp 目录中图片的完整路径
		String tempimg = tomcatpath + separator + filename;
		String uploadimg = realPath + filename;
		// System.out.println("sourceimage  :"+tempimg);
		// System.out.println("targetimage  :"+uploadimg);

		// 开始把图片写入到 upload 目录
		boolean result = this.saveImage(tempimg, uploadimg);
		//System.out.println("开始把图片写入到 upload 目录结果  :" + result);

		KLineJSONEntity entity = new KLineJSONEntity();
		entity.setTime(TimeTools.getCurrentTimeWithMinute());
		entity.setType(titlearr[1]);
		//entity.setType(""+typeint);
		entity.setTitle(titlearr[0]);
		entity.setImgpath(Constants.HTTPURL + filename);
		Gson gson = new Gson();
		String jsonstr = gson.toJson(entity);

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

	// ////////////////////////////////
	public boolean saveImage(String sourcepath, String topath) {
		boolean successful = false;
		try {

			File sourceFile = new File(sourcepath);
			File targetFile = new File(topath);

			InputStream in = new FileInputStream(sourceFile);
			OutputStream out = new FileOutputStream(targetFile);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();

		} catch (Exception e) {

			successful = false;

			e.printStackTrace();

		}

		return successful;
	}

	private String[] createTitleByType(String type) {
		String [] arr = new String[2];
		String title = "";
		String returntype = "";
		int typeint = new Integer(type.trim()).intValue();
		// 1--天通银，2--天通钯金，3--天通铂金，4--天通镍
		switch (typeint) {
		case 1:
			title = "现货白银";
			returntype = "1";
			break;
		case 2:
			title = "现货钯金";
			returntype = "2";
			break;
		case 3:
			title = "现货铂金";
			returntype = "3";
			break;
		case 4:
			title = "现货镍";
			returntype = "4";
			break;
		default:
			break;
		}
		
		arr[0] = title;
		arr[1] = returntype;
		
		return arr;

	}

}
