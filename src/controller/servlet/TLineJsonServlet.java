package controller.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.FileTools;

public class TLineJsonServlet extends HttpServlet {

	String separator = System.getProperty("file.separator"); // 文件分割符号
	
	/**
	 * Constructor of the object.
	 */
	public TLineJsonServlet() {
		super();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		ServletContext servletContext = this.getServletContext();
		String path = servletContext.getRealPath("/");
		String realPath = servletContext.getRealPath("jsons") + separator+"linejson.txt";
				
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
