package net;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import org.apache.http.util.EntityUtils;


public class HttpService {

	// 浏览器Agent
	//public static String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_3) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.151 Safari/535.19";
	public String doPost(String url, Map<String, String> map) {
		HttpResponse httpResponse = null;
		HttpPost post = null;
		String strResult = null;
		try {
			post = new HttpPost(url);

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("content-type",
					"application/x-www-form-urlencoded"));

			if (null != map && map.size() > 0) {
				Set<Entry<String, String>> set = map.entrySet();
				for (Entry<String, String> entry : set) {
					params.add(new BasicNameValuePair(entry.getKey(), entry
							.getValue()));
				}
			}

			//post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

			
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
			HttpConnectionParams.setSoTimeout(httpParams, 5000);

			httpResponse = new DefaultHttpClient(httpParams).execute(post);

			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				/* 读返回数据 */
				HttpEntity hentity = 	httpResponse.getEntity();
				//System.out.println(hentity.);
				strResult = EntityUtils.toString(httpResponse.getEntity());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (post != null) {
				post.abort();
			}

		}
		return strResult;
	}
	
	
	/** 
     * 根据URL获得所有的html信息 
     * @param url 
     * @return 
     */  
    public String doGet(String url){  
        String html = null;  
        HttpClient httpClient = new DefaultHttpClient();//创建httpClient对象  
        HttpGet httpget = new HttpGet(url);//以get方式请求该URL  
   
        try {  
            HttpResponse responce = httpClient.execute(httpget);//得到responce对象  
            int resStatu = responce.getStatusLine().getStatusCode();//返回码  
            if (resStatu==HttpStatus.SC_OK) {//200正常  其他就不对  
                //获得相应实体  
                HttpEntity entity = responce.getEntity();  
                if (entity!=null) {  
                    html = EntityUtils.toString(entity);//获得html源代码  
                }  
            }  
        } catch (Exception e) {  
            System.out.println("访问【"+url+"】出现异常!");  
            e.printStackTrace();  
        } finally {  
            httpClient.getConnectionManager().shutdown();  
        }  
        return html;  
    }  

}
