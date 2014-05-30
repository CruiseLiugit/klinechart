package util;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * 工具类，提供正则表达式，字符串，日期处理等
 * 
 * @author SDJG
 * 
 */
public class JavaTools {
	private static String separator = System.getProperty("file.separator");
	//private static String separator = "/";
	
	/*
	 * //判断是否是数字 public static boolean isNumeric(String str) { Pattern pattern =
	 * Pattern.compile("[0-9]*"); Matcher isNum = pattern.matcher(str); if(
	 * !isNum.matches() ) { return false; } return true; }
	 */

	/**
	 * 检查年份格式
	 * 
	 * @param date
	 * @return
	 */
	public static boolean isYear(String str) {
		// String regeStr =
		// "(?:[0-9]{1,4}(?<!^0?0?0?0))-(?:0?[1-9]|1[0-2])-(?:0?[1-9]|1[0-9]|2[0-8]|(?:(?<=-(?:0?[13578]|1[02])-)(?:29|3[01]))|(?:(?<=-(?:0?[469]|11)-)(?:29|30))|(?:(?<=(?:(?:[0-9]{0,2}(?!0?0)(?:[02468]?(?<![13579])[048]|[13579][26]))|(?:(?:[02468]?[048]|[13579][26])00))-0?2-)(?:29)))";
		String regeStr = "([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})";
		Pattern pattern = Pattern.compile(regeStr);
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;

	}

	// /////////////////////////////////////////////
	/**
	 * 传入参数 2013.01， 返回 2013/201301/201301_1/
	 * 
	 * @param month
	 * @return
	 */
	public static String changeTimeToPath(String month, int count) {
		StringBuffer path = new StringBuffer("");
		System.out.println(month);
		// String[] arr = month.trim().split(".");
		// System.out.println("arr length  ="+arr.length);
		String mon = month;
		String y = month.substring(0, 4);
		String m = mon.substring(5, 7);

		String dir1 = y + m;
		System.out.println("dir1 = " + dir1);

		path.append(y + separator);
		path.append(dir1 + separator);
		path.append(dir1 + "_" + count + separator);

		return path.toString();
	}
	
	public static String changeTimeToPath2(String month, int count) {
		StringBuffer path = new StringBuffer("");
		System.out.println(month);
		// String[] arr = month.trim().split(".");
		// System.out.println("arr length  ="+arr.length);
		String mon = month;
		String y = month.substring(0, 4);
		String m = mon.substring(5, 7);

		String dir1 = y + m;
		System.out.println("dir1 = " + dir1);

		path.append(y + "/");
		path.append(dir1 + "/");
		path.append(dir1 + "_" + count + "/");

		return path.toString();
	}

	/**
	 * 传入参数 2013.01 <magazine name="尖锋杂志" version="1.0.0"> <year value="2013">
	 * <Month value="2013.01"> <Periodical>1</Periodical>
	 * <WholePeriodical>1</WholePeriodical> <Title>尖锋软件公司</Title>
	 * <Synopsis>尖分锋软件公司是一家成立于2008年的高科技企业，现主要致力于IOS、
	 * Android为平台的应用研发，在平台开发方面拥有丰富的经验
	 * ，能够足够满足客户的各种需求，为客户目标的实现提供量身定做的服务方案。尖峰软件的IOS、
	 * Android开发队伍技术全面，且具有更加专业的技术水平，我们不仅为您提供现场咨询以及IOS、
	 * Android应用程序的离岸开发，我们还为您提供专业的营销服务。我们相信，我们在</Synopsis>
	 * <FrontCover>sfrontcover_364770974.jpg</FrontCover>
	 * <CatalogCover>scatalog_347400291.jpg</CatalogCover>
	 * <Ppath>2013/201301/201301_1/201301_1.xml</Ppath> <PeriodicalResourse />
	 * </Month>
	 * 
	 * @param month
	 * @return
	 */
	public static String getPPath(String month, int count) {
		String[] arr = month.split(".");
		String dir = arr[0] + arr[1];

		StringBuffer path = new StringBuffer("");
		String dir1 = changeTimeToPath(month, count);

		path.append(dir1);
		path.append(dir + "_" + count + ".xml");

		return path.toString();
	}

	/**
	 * 传入参数 2013.01 <periodical> <contents> <CoverPage PageName="封面"
	 * ThumbName="sfrontcover_364770974.jpg"
	 * CoverPath="2013/201301/201301_1/frontcover/frontcover.html" />
	 * 
	 * @param month
	 * @return
	 */
	public static String getCoverPath(String month, int count) {
		StringBuffer path = new StringBuffer("");
		String dir1 = changeTimeToPath(month, count);
		path.append(dir1);
		path.append("frontcover"+separator);

		return path.toString();
	}
	
	public static String getCoverImagePath(String month, int count) {
		StringBuffer path = new StringBuffer("");
		String dir1 = changeTimeToPath(month, count);
		path.append(dir1);
		path.append("frontcover"+separator+"resource"+separator);

		return path.toString();
	}
	
	public static String getCoverPath2(String month, int count) {
		StringBuffer path = new StringBuffer("");
		String dir1 = changeTimeToPath2(month, count);
		path.append(dir1);
		path.append("frontcover"+"/");

		return path.toString();
	}

	/**
	 * 每期杂志对应的 xml 文件
	 * 
	 * @param month
	 * @param count
	 * @return
	 */
	public static String getPeriodicalXMLPath(String month, int count) {
		StringBuffer path = new StringBuffer("");
		String dir1 = changeTimeToPath(month, count);
		path.append(dir1);

		StringBuffer pp = new StringBuffer("");
		String mon = month;
		String y = month.substring(0, 4);
		String m = mon.substring(5, 7);

		String mm = y + m;

		path.append(mm + "_" + count + ".xml");

		return path.toString();
	}
	
	public static String getPeriodicalXMLPath2(String month, int count) {
		StringBuffer path = new StringBuffer("");
		String dir1 = changeTimeToPath2(month, count);
		path.append(dir1);

		StringBuffer pp = new StringBuffer("");
		String mon = month;
		String y = month.substring(0, 4);
		String m = mon.substring(5, 7);

		String mm = y + m;

		path.append(mm + "_" + count + ".xml");

		return path.toString();
	}

	// 缩略图目录，所有的图片，里面都要一份
	public static String getThumbPackage(String month, int count) {
		StringBuffer path = new StringBuffer("");
		String dir1 = changeTimeToPath(month, count);
		path.append(dir1);
		path.append("ThumbPackage"+separator);

		return path.toString();
	}
	
	public static String getThumbPackage2(String month, int count) {
		StringBuffer path = new StringBuffer("");
		String dir1 = changeTimeToPath2(month, count);
		path.append(dir1);
		path.append("ThumbPackage"+"/");

		return path.toString();
	}

	/**
	 * 传入参数 2013.01 <Catalog PageName="目录" ThumbName="scatalog_347400291.jpg"
	 * CataPath="2013/201301/201301_1/catalog/catalog.html" />
	 * 
	 * 
	 * @param month
	 * @return
	 */
	public static String getCataPath(String month, int count) {
		StringBuffer path = new StringBuffer("");
		String dir1 = changeTimeToPath(month, count);
		path.append("catalog"+separator);

		return path.toString();
	}

	/**
	 * 传入参数 2013.01 <Topic TopicName="尖锋产品列表展示"
	 * ThumbName="s201301_1_jianfengchanpinliebiaozhanshi_325163512.jpg"
	 * TopicPath=
	 * "2013/201301/201301_1/201301_1_jianfengchanpinliebiaozhanshi/201301_1_jianfengchanpinliebiaozhanshi.html"
	 * Intro="尖峰产品列表展示" />
	 * 
	 * 
	 * @param month
	 * @return
	 */
	public static String getTopicPath(String month, int count) {
		StringBuffer path = new StringBuffer("");
		String dir1 = changeTimeToPath(month, count);
		path.append(dir1);

		String mon = month;
		String y = month.substring(0, 4);
		String m = mon.substring(5, 7);

		path.append(y + "_" + m + separator);

		return path.toString();

	}

	//大图路径里面存放 与图片同名的 html
	public static String getTopicPath(String month, int count, String filename) {
		// 获取不带路径的文件名
		String fname = filename;

		// 获取文件扩展名前面的 名称
		int ii = -1;
		if ((ii = fname.indexOf(".")) != -1) {
			fname = fname.substring(0, ii);
		}

		StringBuffer path = new StringBuffer("");
		String dir1 = changeTimeToPath(month, count);
		path.append(dir1);

		String mon = month;
		String y = month.substring(0, 4);
		String m = mon.substring(5, 7);

		path.append(y + "_" + m + "_" + fname + separator);
		
		return path.toString();

	}
	
	public static String getTopicPath(String month, int count, String filename,String htmlName) {
		// 获取不带路径的文件名
		String fname = filename;

		// 获取文件扩展名前面的 名称
		int ii = -1;
		if ((ii = fname.indexOf(".")) != -1) {
			fname = fname.substring(0, ii);
		}

		StringBuffer path = new StringBuffer("");
		String dir1 = changeTimeToPath(month, count);
		path.append(dir1);

		String mon = month;
		String y = month.substring(0, 4);
		String m = mon.substring(5, 7);

		//path.append(y + "_" + m + "_" + fname + separator);
		//11-24 晚上修改，把最后的一个名称改掉，去掉一级目录
		path.append(y + "_" + m + "_" + fname+ separator +htmlName );
		
		return path.toString();

	}
	
	//大图路径里面，html 文件同目录下新建一个 resource
	public static String getTopicPathImg(String month, int count, String filename) {
		// 获取不带路径的文件名
		String fname = filename;

		// 获取文件扩展名前面的 名称
		int ii = -1;
		if ((ii = fname.indexOf(".")) != -1) {
			fname = fname.substring(0, ii);
		}

		StringBuffer path = new StringBuffer("");
		String dir1 = changeTimeToPath(month, count);
		path.append(dir1);

		String mon = month;
		String y = month.substring(0, 4);
		String m = mon.substring(5, 7);

		path.append(y + "_" + m + "_" + fname + separator+"resource"+separator);

		return path.toString();

	}
	

	public static String getTopicPath2(String month, int count, String filename) {
		// 获取不带路径的文件名
		String fname = filename;

		// 获取文件扩展名前面的 名称
		int ii = -1;
		if ((ii = fname.indexOf(".")) != -1) {
			fname = fname.substring(0, ii);
		}

		StringBuffer path = new StringBuffer("");
		String dir1 = changeTimeToPath2(month, count);
		path.append(dir1);

		String mon = month;
		String y = month.substring(0, 4);
		String m = mon.substring(5, 7);

		
		path.append(y + "_" + m + "_" + fname+"/");
		
		return path.toString();
	}
	
	public static String getTopicPath2(String month, int count, String filename,String htmlName) {
		// 获取不带路径的文件名
		String fname = filename;

		// 获取文件扩展名前面的 名称
		int ii = -1;
		if ((ii = fname.indexOf(".")) != -1) {
			fname = fname.substring(0, ii);
		}

		StringBuffer path = new StringBuffer("");
		String dir1 = changeTimeToPath2(month, count);
		path.append(dir1);

		String mon = month;
		String y = month.substring(0, 4);
		String m = mon.substring(5, 7);

		
		//11-24 晚上修改
		//path.append(y + "_" + m + "_" + fname+"/");
		path.append(y + "_" + m + "_" + fname + "/"+htmlName);
		
		return path.toString();

	}
	
	//11-24 晚修改，为所有的 html 文件，生成与文件夹同名的名称
	public static String getHtmlFileName(String month, int count, String filename) {
		// 获取不带路径的文件名
		String fname = filename;

		// 获取文件扩展名前面的 名称
		int ii = -1;
		if ((ii = fname.indexOf(".")) != -1) {
			fname = fname.substring(0, ii);
		}

		StringBuffer path = new StringBuffer("");
		
		String mon = month;
		String y = month.substring(0, 4);
		String m = mon.substring(5, 7);

		path.append(y + "_" + m + "_" + fname + ".html");
		//path.append(fname + ".html");
		
		return path.toString();

	}
	
	
	public static String getTopicXMLPath(String month, int count) {
		// 获取不带路径的文件名
		StringBuffer path = new StringBuffer("");
		String dir1 = changeTimeToPath(month, count);
		path.append(dir1);

		String mon = month;
		String y = month.substring(0, 4);
		String m = mon.substring(5, 7);

		//path.append(y + m + "_"+count + ".xml");

		return path.toString();

	}
	
	public static String getTopicXMlName(String month, int count)
	{
		String mon = month;
		String y = month.substring(0, 4);
		String m = mon.substring(5, 7);

		String ss = y + m + "_"+count + ".xml";
		return ss;
	}
	
	
	//得到 明细压缩的文件名
	public static String getZipPath(String month, int count, String filename) {
		// 获取不带路径的文件名
		String fname = filename;

		// 获取文件扩展名前面的 名称
		int ii = -1;
		if ((ii = fname.indexOf(".")) != -1) {
			fname = fname.substring(0, ii);
		}

		StringBuffer path = new StringBuffer("");
		String dir1 = changeTimeToPath(month, count);
		path.append(dir1);
		
		String mon = month;
		String y = month.substring(0, 4);
		String m = mon.substring(5, 7);

		path.append(y + "_" + m + "_" + fname+".zip" );

		return path.toString();

	}
	

	/**
	 * 根据路径创建一个文件夹
	 */
	public static boolean createDirect(String path) {
		boolean flag = false;
		try {
			File file = new File(path);
			if (!file.exists()) {
				flag = file.createNewFile();
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		return flag;
	}

	// ////////////////////////////////////////////
	// 压缩，解压目录和文件
	public final static String encoding = "UTF-8";

	

	// /////////////////////////////////////////////
	// 删除文件夹
	// param folderPath 文件夹完整绝对路径
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 删除指定文件夹下所有文件
	// param path 文件夹完整绝对路径
	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + separator + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + separator + tempList[i]);// 再删除空文件夹
				flag = true;
			}
		}
		return flag;
	}

	
	/**
	 * 获得一个文件的后缀名
	 * @param fileName  完整的文件名
	 * @return  得到的后缀名
	 */
	public static String getExtention(String fileName) {
		int pos = fileName.lastIndexOf(".");
		String name = fileName.substring(pos);
		System.out.println("getExtention pos =" + pos + ", name = " + name);
		return name;
	}
	
	
	/**
	 * 获得一个文件的后缀名之前的文件名
	 * @param fileName  完整的文件名
	 * @return  得到的后缀名之前的名称
	 */
	public static String getExtentionPrefix(String fileName) {
		int pos = fileName.lastIndexOf(".");
		String name = fileName.substring(0,pos);
		System.out.println("getExtention pos =" + pos + ", name = " + name);
		return name;
	}
	
}
