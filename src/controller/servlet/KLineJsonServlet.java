package controller.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.FileTools;

public class KLineJsonServlet extends HttpServlet {
	String separator = System.getProperty("file.separator"); // 文件分割符号
	private String time; // 时长


	/**
	 * Constructor of the object.
	 */
	public KLineJsonServlet() {
		super();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		time = request.getParameter("time");
		int timeint = new Integer(time.trim()).intValue();
		
		ServletContext servletContext = this.getServletContext();
		//String path = servletContext.getRealPath("/");
		String realPath = servletContext.getRealPath("jsons") + separator;
		
		switch (timeint) {
		case 2:
			System.out.println("---------------------5 分钟 K 线---------------------");
			realPath = realPath+"5minsjson.txt";
			break;
		case 3:
			System.out.println("---------------------30分钟 K 线---------------------");
			realPath = realPath+"30minsjson.txt";
			break;
		case 4:
			System.out.println("---------------------1 日 K 线---------------------");
			realPath = realPath+"onedayjson.txt";
			break;
		default:
			System.out.println("---时间类型 ，不提供服务---");
			break;
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

}
