package com.northmeter.northmetersmart.http;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class HttpUtils {

	public HttpUtils() {
	}

	public static String getJsonContent(String url_path, String encode /* utf-8 */) {
		String jsonString = "";
		try {
			URL url = new URL(url_path);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();

			connection.setConnectTimeout(15000);// 请求延时15秒
			connection.setRequestMethod("GET"); // 请求的方法
			connection.setDoInput(true); // 从服务器获得数据

			int responseCode = connection.getResponseCode(); // 响应的状态码

			// 200表示服务器已经准备好了
			if (200 == responseCode) {
				jsonString = changeInputStream(connection.getInputStream(),
						encode);

			}

		} catch (Exception e) {
		}

		return jsonString;
	}

	private static String changeInputStream(InputStream inputStream,
			String encode) throws IOException {
		String jsonString = null;

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int len = 0;
		while ((len = inputStream.read(data)) != -1) {
			outputStream.write(data, 0, len); // 写数据
		}

		jsonString = new String(outputStream.toByteArray(), encode);

		inputStream.close();

		return jsonString;
	}

	/*
	 * 发送Post请求到服务器 jsonStr请求体内容，encode编码格式
	 */
	public static String submitPostData(String str, String url_path,
			String encode) {

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
			conn.setRequestProperty("Content-type",
					"application/x-www-form-urlencoded");

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
