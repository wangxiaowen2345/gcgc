package com.coffcat.util;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hero
 * @email:mahaojie299@163.com
 * @version 创建时间：2016-11-3 上午11:29:43 程序的简单说明
 */

public final class HttpCli {

	/**
	 * 执行一个HTTP GET请求，返回请求响应的HTML
	 * 
	 * @param url
	 *            请求的URL地址
	 * @param queryString
	 *            请求的查询参数,可以为null
	 * @param charset
	 *            字符集
	 * @param pretty
	 *            是否美化
	 * @return 返回请求响应的HTML
	 */
	public static String doGet(String url, String queryString, String charset,
			boolean pretty) {
		StringBuffer response = new StringBuffer();
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod(url);
		try {
			if (StringUtils.isNotBlank(queryString))
				// 对get请求参数做了http请求默认编码，好像没有任何问题，汉字编码后，就成为%式样的字符串
				method.setQueryString(URIUtil.encodeQuery(queryString));
			client.executeMethod(method);
			if (method.getStatusCode() == HttpStatus.SC_OK) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(method.getResponseBodyAsStream(),
								charset));
				String line;
				while ((line = reader.readLine()) != null) {
					if (pretty)
						response.append(line).append(
								System.getProperty("line.separator"));
					else
						response.append(line);
				}
				reader.close();
			}
		} catch (URIException e) {
			System.out.println("执行HTTP Get请求时，编码查询字符串“" + queryString
					+ "”发生异常！");
		} catch (IOException e) {
			System.out.println("执行HTTP Get请求" + url + "时，发生异常！");
		} finally {
			method.releaseConnection();
		}
		return response.toString();
	}

	/**
	 * 执行一个HTTP POST请求，返回请求响应的HTML
	 * 
	 * @param url
	 *            请求的URL地址
	 * @param params
	 *            请求的查询参数,可以为null
	 * @param charset
	 *            字符集
	 * @param pretty
	 *            是否美化
	 * @return 返回请求响应的HTML
	 */
	public static String doPost(String url, Map<String, String> params,
			String charset, boolean pretty) {
		StringBuffer response = new StringBuffer();
		HttpClient client = new HttpClient();

		HttpMethod method = new PostMethod(url);

		// 设置通用的请求属性
		method.addRequestHeader("Cache-Control", "no-cache");
		method.addRequestHeader("accept", "text/html,application/xhtml+xml");
		method.addRequestHeader("accept-Encoding", "gbk,GBK2312");
		method.addRequestHeader("accept-Language", "zh-cn");
		method.addRequestHeader("connection", "Keep-Alive");
		method.addRequestHeader("user-agent", "Mozilla/5.0");
		method.addRequestHeader("Content-Type",
				"application/x-www-form-urlencoded");

		// 设置Http Post数据
		if (params != null) {
			HttpMethodParams p = new HttpMethodParams();
			for (Map.Entry<String, String> entry : params.entrySet()) {
				p.setParameter(entry.getKey(), entry.getValue());
			}
			method.setParams(p);
		}
		try {
			client.executeMethod(method);
			if (method.getStatusCode() == HttpStatus.SC_OK) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(method.getResponseBodyAsStream(),
								charset));
				String line;
				while ((line = reader.readLine()) != null) {
					if (pretty)
						response.append(line).append(
								System.getProperty("line.separator"));
					else
						response.append(line);
				}
				reader.close();
			}
		} catch (IOException e) {
			System.out.println("执行HTTP Post请求" + url + "时，发生异常！");
		} finally {
			method.releaseConnection();
		}
		return response.toString();
	}

	public static void main(String[] args) {
		Map<String, String> params = new HashMap<String, String>();

		params.put("token", "6a295bdc7c3f43838e6ccef53e0999a4-321816590");
		params.put("lang", "zh");
		params.put("client_key", "3c2cd3f3");
		params.put("count", "30");
		params.put(
				"referer",
				"ks%3A%2F%2Fphoto%2F368765673%2F1073058331%2F3%2F1_a%2F1545765134287413249_n80%23avatar&os=android&user_id=&sig=2eca7ec5f04305f476d1fba8aafb5644&");
		params.put("os", "android");
		params.put("user_id", "");
		params.put("photo_id", "1215330364");
		params.put("sig", "2eca7ec5f04305f476d1fba8aafb5644");

		// 118c87f7278a672dbb284d13def92603

		String url = "http://api.ksapisrv.com/rest/n/clc/click?mod=Xiaomi(Redmi%20Note%202)&lon=116.23003&country_code=CN&did=ANDROID_40926e52811db9d1&app=0&net=WIFI&oc=MYAPP&ud=&c=XIAOMI&sys=ANDROID_5.0.2&appver=4.49.1.2199&language=zh-cn&lat=39.926049&ver=4.47.0.1852";

		String s = "token=6a295bdc7c3f43838e6ccef53e0999a4-321816590&lang=zh&client_key=5aec8372&count=30&referer=ks%3A%2F%2Fphoto%2F368765673%2F1073058331%2F3%2F1_a%2F1545765134287413249_n80%23avatar&os=android&user_id=";
		// String s1 =
		// "token=6a295bdc7c3f43838e6ccef53e0999a4-321816590&lang=zh&client_key=3c2cd3f3&count=30&referer=ks%3A%2F%2Fphoto%2F368765673%2F1073058331%2F3%2F1_a%2F1545765134287413249_n80%23avatar&os=android&user_id=";

		String sn = MD5Util
				.string2MD5(aaaa(s.replace("&", "") + "3ef750b22f3e"));

		String ss = "&sig=" + sn;

		System.out.println(s + ss);

		for (int t = 0; t < 2000; t++) {
			System.out.println(t);
			String x = doPost("http://api.gifshow.com/rest/n/clc/click?" + s
					+ ss + "", params, "utf8", false);
			System.out.println(x);
		}

	}

	public static String aaaa(String a) {

		char c[] = new char[a.length()];
		for (int i = 0; i < a.length(); i++) {
			c[i] = a.charAt(i);

		}

		for (int i = 0; i < c.length; i++) {
			for (int j = c.length - 1; j > i; j--) {
				if ((int) c[i] > (int) c[j]) {
					char t = c[i];
					c[i] = c[j];
					c[j] = t;
				}
			}
		}

		String ss = "";
		for (int i = 0; i < a.length(); i++) {
			ss += c[i];
		}
		System.out.println(ss);
		return ss;

	}
}