package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.lang.System;

import javax.servlet.ServletContext;

public class FileTools {


	String separator = System.getProperty("file.separator"); // 文件分割符号
	
	/**
	 * 把 JSON 字符串，放入制定的 json 文件中
	 */
	public void createJsonFile(ServletContext context,String jsonStr, String fileName) {
		try {
			//写入 json 文件的路径 目录
			String jsonFileDir = context.getRealPath("jsons") + separator;
			//System.out.println("目录名  :"+jsonFileDir);
			//创建文本文件目录
			String filepath = jsonFileDir+fileName;
			//System.out.println("文件名  :"+filepath);
			
			// 判断目录是否存在，不存在先创建
			File dir = new File(jsonFileDir);
			File jsonfile = new File(filepath);
			
			if (!dir.exists()) {
				dir.mkdirs();
			}	
			if (!jsonfile.exists()) {
				jsonfile.createNewFile();
			}
			
			this.writeString2File(filepath, jsonStr);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 把字符串内容写入 文件
	 * @param filePath 文件路径
	 * @param jsonStr  要写入的字符串内容
	 */
	public void writeString2File(String filePath,String jsonStr) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(filePath);
			// 定义一个指向D:/TEXT.TXT文件

			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
					fileOutputStream);

			BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
			//把字符串写入文件流
			bufferedWriter.write(jsonStr);
			bufferedWriter.close();
			outputStreamWriter.close();
			fileOutputStream.close();
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
	/**
	 * 从 文件 中读取字符串内容
	 * @param filePath 文件路径
	 * @return String  从文件中读取出来的字符串内容
	 */
	public String readStringFromFile(String filePath) {
		//读取出来的字符串
		String ss = new String();
		try {
		    FileInputStream fileInputStream = new FileInputStream(filePath);     
		  //字节流转换成InputStreamReader  
		    InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);   
		  //InputStreamReader 转换成带缓存的bufferedReader 
		    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);   
		      
		    //可以把读出来的内容赋值给字符   
		       
		    String s;   
		    while((s = bufferedReader.readLine())!=null){   
		            ss += s;   
		    }   
		} catch (Exception e) {
			// TODO: handle exception
		}
		return ss;
	}

	
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
	
}
