package com.northmeter.northmetersmart.http;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public class HttpRequest {
	
	
	public static void main(String [] args){
		 //发送 GET 请求
//        String s=HttpRequest.sendGet("http://10.168.1.211:4000/users/getUser", "flag=tel&tel=17876148387");
//        System.out.println(s);

		JSONObject jsonObj = new JSONObject();
 
		jsonObj.put("tel", "18679932627");
		jsonObj.put("unionid", "o0bzZv7RkOov7DN8xsicoaM7jdkQ");
		System.out.println(jsonObj);
        //发送 POST 请求
        String sr;
		try {
			sr = HttpRequest.sendPost("http://10.168.1.211:4000/users/registerUser", "{\"tel\":\"18679932627\",\"unionid\":\"o0bzZv7RkOov7DN8xsicoaM7jdkQ\"}");
			System.out.println(sr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
    /**
     * 向指定URL发送GET方法的请求
     * 
     * @param url
     *            发送请求的URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param) throws Exception{
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置连接超时时间
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
               // System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(),"utf-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } 
//        catch(Exception e){
//        	e.printStackTrace();
//        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }
    
    /**
     * 向指定 URL 发送POST方法的请求
     * 
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) throws Exception{
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            // 设置连接超时时间
 			conn.setConnectTimeout(10000);
 			conn.setReadTimeout(10000);
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
         // 设置content－type获得输出流，便于向服务器发送信息。
         	conn.setRequestProperty("Content-type","application/json");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(),"utf-8"));
            
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            
        }
//        catch (Exception e) {
//            System.out.println("发送 POST 请求出现异常！"+e);
//            e.printStackTrace();
//            return null;
//        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }    
    
    public static String sendGet2(String url, String param,String set_cooks) throws Exception{
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置连接超时时间
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.setRequestProperty("Set-Cookie", set_cooks);
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
               // System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(),"utf-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } 
//        catch(Exception e){
//        	e.printStackTrace();
//        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }
    
    /**
     * 向指定 URL 发送POST方法的请求
     * 
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost2(String url, String param,String set_cooks) throws Exception{
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            // 设置连接超时时间
 			conn.setConnectTimeout(10000);
 			conn.setReadTimeout(10000);
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
         // 设置content－type获得输出流，便于向服务器发送信息。
         	conn.setRequestProperty("Content-type","application/json");
         	conn.setRequestProperty("Set-Cookie", set_cooks);
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(),"utf-8"));
            
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            
        }
//        catch (Exception e) {
//            System.out.println("发送 POST 请求出现异常！"+e);
//            e.printStackTrace();
//            return null;
//        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }    
    
    
    /**
     * 向指定 URL 发送POST方法的请求,获取set_cookie
     * 
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost_getSet_Cookie(String url, String param) throws Exception{
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        String set_cookie = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            // 设置连接超时时间
 			conn.setConnectTimeout(10000);
 			conn.setReadTimeout(10000);
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
         // 设置content－type获得输出流，便于向服务器发送信息。
         	conn.setRequestProperty("Content-type","application/json");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(),"utf-8"));
            
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            
            System.out.println("Set-Cookie==================="+conn.getHeaderField("Set-Cookie"));
            set_cookie = conn.getHeaderField("Set-Cookie");
        }
//        catch (Exception e) {
//            System.out.println("发送 POST 请求出现异常！"+e);
//            e.printStackTrace();
//            return null;
//        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return set_cookie;
    }    
    
    
    
    /*
	 * 发送Post请求到服务器 jsonStr请求体内容，encode编码格式
	 */
	public static String submitPostData(String url_path,String str) {
	
		URL url = null;
		HttpURLConnection conn = null;
		String result = null;
	
		try {
			url = new URL(url_path);
			conn = (HttpURLConnection) url.openConnection();
	
			// 设置连接超时时间
			conn.setConnectTimeout(20000);
			conn.setReadTimeout(20000);
	
			// 设置以Post方式提交数据
			conn.setRequestMethod("POST");
	
			// 打开输入、输出流，以便从服务器获取数据
			conn.setDoInput(true);
			conn.setDoOutput(true);
	
			// 使用Post方式不能使用缓存
			conn.setUseCaches(false);
			conn.setInstanceFollowRedirects(true);
	
			// 设置content－type获得输出流，便于向服务器发送信息。
			conn.setRequestProperty("Content-type","application/json");
	
			// 获得 DataOutputStream 流
			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
	
			// 要上传的参数（这里涉及到字符编码的问题）
			URLEncoder.encode(str, "UTF-8");
	
			// 将参数写入流中
			out.writeBytes(str);
			out.flush();
			out.close();
	
			int responseCode = conn.getResponseCode();
			if (HttpURLConnection.HTTP_OK == responseCode) {
				System.out.println("http.getResponseCode() == 200！");
	
				String inputLine;
	
				// 得到读取内容的输入流
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(conn.getInputStream(), "utf-8"));
	
				while ((inputLine = reader.readLine()) != null) {
					result += inputLine;
				}
	
				reader.close();
	
			} else {
				System.out.println("##########################连接失败！");
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (conn != null)
				conn.disconnect();
		}
	
		System.out.println("#########################result = " + result);
		return result;
	}

}