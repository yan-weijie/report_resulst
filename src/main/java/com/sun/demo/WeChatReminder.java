package com.sun.demo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.DefaultHttpParams;
import org.testng.annotations.Test;

public class WeChatReminder {
//	private HttpClientMethod httpClient = new HttpClientMethod();
	private String CorpID = "xxx";
	private String Secret = "xxx";
	private String baseUrl = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?";
	private String url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=";
	private String serverUrl = "xxx";
	
	@Test
	public void f() {
		String message = "报告名称：" + "allin-h5-api-test-results"
				 + "成功率:" + "90%\\n" 
				 + "生成时间：" + "20160816112142\\n"
				 +"报告链接:"+ "statistics-test-results20160816112142.html";
		
		sendWeChatNewsMsg("测试报告", message, "allin-h5-api-test-results");
	}
	
	// 获取接口访问权限码
    public String getAccessToken() {
        HttpClient client = new HttpClient();
        String params = "corpid=" + CorpID + "&corpsecret=" + Secret;
        
        GetMethod get = new GetMethod(baseUrl + params);
        get.releaseConnection();
        get.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        // 设置策略，防止报cookie错误
        DefaultHttpParams.getDefaultParams().setParameter("http.protocol.cookie-policy", CookiePolicy.BROWSER_COMPATIBILITY);
        String result = "";
        try {
            client.executeMethod(get);
            
            result = new String(get.getResponseBodyAsString().getBytes("gbk"));
            System.out.println("token响应："+result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 将数据转换成json
        JSONObject jasonObject = JSONObject.fromObject(result);
        result = (String) jasonObject.get("access_token");
         System.out.println("token是："+result);
 
        return result;
 
    }
    
    /**
     * 企业接口向下属关注用户发送微信消息  
     * @param content	图文消息描述，不超过512个字节，超过会自动截断 
     */
    public void sendWeChatNewsMsg(String title, String content, String result_name) {
 
        URL uRl;
        String ACCESS_TOKEN = getAccessToken();
        // 拼接请求串
        String action = url + ACCESS_TOKEN;
        // 封装发送消息请求json
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        sb.append("\"touser\":" + "\"" + "@all" + "\",");
        sb.append("\"toparty\":" + "\"" + "@all" + "\",");
        sb.append("\"totag\":" + "\"" + "" + "\",");
        sb.append("\"msgtype\":" + "\"" + "news" + "\",");
        sb.append("\"agentid\":" + "\"" + "1" + "\",");
        
        sb.append("\"news\":" + "{");
        sb.append("\"articles\":" + "[");
        sb.append("{");
        sb.append("\"title\":" + "\"" + title + "\",");
        sb.append("\"description\":" + "\"" + content + "\",");
        sb.append("\"url\":" + "\"" + serverUrl + result_name + ".html\"");
        sb.append("}");
        sb.append("]");
        sb.append("}");
        sb.append("}");
        String json = sb.toString();
        try {
 
            uRl = new URL(action);
 
            HttpsURLConnection http = (HttpsURLConnection) uRl.openConnection();
 
            http.setRequestMethod("POST");
 
            http.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
 
            http.setDoOutput(true);
 
            http.setDoInput(true);
 
            System.setProperty("sun.net.client.defaultConnectTimeout", "30000");//
            // 连接超时30秒
 
            System.setProperty("sun.net.client.defaultReadTimeout", "30000"); //
            // 读取超时30秒
 
            http.connect();
 
            OutputStream os = http.getOutputStream();
 
            os.write(json.getBytes("UTF-8"));// 传入参数
 
            InputStream is = http.getInputStream();
 
            int size = is.available();
 
            byte[] jsonBytes = new byte[size];
 
            is.read(jsonBytes);
 
            String result = new String(jsonBytes, "UTF-8");
 
            System.out.println("请求返回结果:" + result);
 
            os.flush();
 
            os.close();
 
        } catch (Exception e) {
 
            e.printStackTrace();
 
        }
    }
	
//	/**
//	 * 模拟http json Post请求
//	 */
//	public void httpClientPostHeaders(String host, String method ,String message) throws IOException {
//		String urls = "";
//		DefaultHttpClient httpclient = new DefaultHttpClient();	
//		HttpPost post = null;
//		try {
////			System.out.println(host + method + params);
//			urls = host + method ;
//			HttpPost httppost = new HttpPost(urls);
//			
//			// 添加http头信息
//			httppost.addHeader("accept", "application/json, text/javascript, */*; q=0.01"); //认证token
//			httppost.addHeader("connection", "Keep-Alive");
//			httppost.addHeader("Accept-Encoding", "gzip, deflate");
//			httppost.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
//			httppost.addHeader("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
//			
//			// 拼装json格式的参数：  {"name": "your name","parentId": "id_of_parent"}
//			JSONObject obj = new JSONObject();
//			obj.put("touser", "@all");
//			obj.put("toparty", "@all");
//			obj.put("msgtype", "news");
//			obj.put("agentid", "1");
//			obj.put("news", "{\"articles\":[{\"title\": \"Blocking bug\",\"description\":\""+message
//					+"\",\"url\": \""+serverUrl+"\"}]}");
//			
//			
//			System.out.println("obj.toString() = " + obj.toString());
//			// 执行
//			httppost.setEntity(new StringEntity(obj.toString()));
//			HttpResponse response = httpclient.execute(httppost);
//			
//			// 判断返回值
//			int code = response.getStatusLine().getStatusCode();
//			System.out.println("code = " + code);
//			
//			if (code == 200) { 
//				String text = EntityUtils.toString(response.getEntity()); //返回json格式： {"id": "27JpL~j4vsL0LX00E00005","version": "abc"}		
//				System.out.println("text = " + text);
////				obj = new JSONObject(rev);
////				String id = obj.getString("id");
////				String version = obj.getString("version");
//			}
//			
//			
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//		}
//	}

}
