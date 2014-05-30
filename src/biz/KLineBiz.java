package biz;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import net.HttpService;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import entity.KLine;

/**
 * limit没有的话默认是10条

Open开盘价， QuoteTime 成交时间，High最高价，Code编码，LastClose收盘价,"OpenTime" : "1387915440" 时间戳

 */
public class KLineBiz {
	
	//生成网络服务对象
	HttpService httpService = new HttpService();
	//gson对象
	Gson gson = new Gson();
	
	
	
	//金融案例列表
	/**
	 * @param type  1--天通银，2--天通钯金，3--天通铂金，4--天通镍
	 * @param time  1--1分钟，2--5分钟，3--30分钟，4--1天
	 * @param limit  默认 10条，求 K  线图中的 MA 数据条数要根据当前条数往前取 5 条、10条、20条数据
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<KLine> getKLineList(int type,int time,int limit) throws Exception{
		List<KLine> fcList	= null;
		
		//http://api.jzz183.com/api/getchart.php?username=test&password=098f6bcd4621d373cade4e832627b4f6&type=1&time=1&limit=2
		// 解析地址
		String url = "http://api.jzz183.com/api/getchart.php?username=test&password=098f6bcd4621d373cade4e832627b4f6&type="+type+"&time="+time+"&limit="+limit;
		System.out.println("********** url = "+url);
		
		//过滤重复的Point对象  
	    List<KLine> list = new ArrayList<KLine>(); 
	      
		try {
			String jsonStr = httpService.doGet(url);
			// 解析json数据
			Type tp = new TypeToken<List<KLine>>() {}.getType();
			//System.out.println("-----tp ="+tp);
			fcList = gson.fromJson(jsonStr.toLowerCase(), tp);
			System.out.println("-----过滤前 size ="+fcList.size());
			
			//过滤重复的Point对象  
		     for (KLine o:fcList)    
		      {  
		          if (!list.contains(o))  
		          {  
		              list.add(o);  
		          }  
		      }  
		     System.out.println("-----过滤后 size ="+list.size());
		     
		} catch (JsonSyntaxException e) {
			throw new Exception("获取网络数据失败！");
		} catch (Exception e) {
			throw new Exception("网络连接失败！");
		}
		
		if(null != fcList && fcList.size()==0){
			throw new Exception("============>接口返回数据为空");
		}
		
		return list;
		
	}

}
