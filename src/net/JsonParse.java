package net;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class JsonParse {
	
	public Object jsonParse(String url,Map<String,String> param) throws Exception{
		//生成网络服务对象
		HttpService httpService = new HttpService();
		//gson对象
		Gson gson = new Gson();
		Object object;
		
		try {
			String jsonStr = httpService.doPost(url,param);
			// 解析json数据
			Type type = new TypeToken<Object>(){}.getType();
			object = gson.fromJson(jsonStr, type);
		} catch (JsonSyntaxException e) {
			throw new Exception("获取网络数据失败！");
		} catch (Exception e){
			throw new Exception("网络连接失败！");
		}
		
		return object;
	}

}
