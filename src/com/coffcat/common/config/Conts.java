package com.coffcat.common.config;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.coffcat.util.Sys;
import com.jfinal.kit.PropKit;

public class Conts {
	
	public final static String MsgTitle="【优客良依】";
	public final static String PassExamineMsg = "亲爱的用户，您好，您的预约订单Y%orderid%已通过审核。";
	public final static String OrderPdMsg = "亲,您好，您的预约订单Y%orderid%已生效,您的量体师%name%（电话：%tel%）将于%time%在%position%为您服务,如有疑问请拨打400-000-9676";
	public final static String ExamineBeforeMsg = "亲，感谢您的预约，您的预约单提交成功，优客良依客服即将致电给您，请注意接听。";
	
	/**
	 * 分页大小
	 */
	public final static int PageSize = 10;
	
	public static String wxurl = "https://api.weixin.qq.com/sns/oauth2/access_token?grant_type=authorization_code";
	@SuppressWarnings("deprecation")
	public static String wxoauth = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx38195700106aac78&redirect_uri="+URLEncoder.encode("http://www.gcgc.cn/wxOauth")+"&response_type=code&scope=snsapi_userinfo&state=*state*#wechat_redirect";

	/**
	 * 默认上传地址
	 */
	public final static String BaseUploadDir = "upload";

	public static String[] type;

	public static List<String[]> typefield;

	public static List<Map<String, Object>> persondata;

	public static List<Map<String, Object>> orderpersontrousersdata;
	
	public static List<Map<String, Object>> orderpersondata;
	
	public static List<String> good_class = new ArrayList<String>(); 

	public static boolean test = true;

	public static void init() {
		Sys.out("Start init Data");

		Sys.out("Init LT Data");

		Sys.out(PropKit.get("type"));
		type = PropKit.get("type").split("/");

		char a = 'a';

		typefield = new ArrayList<String[]>();

		for (int i = 0; i < type.length; i++) {
			String s = PropKit.get("type" + (char) (a + i));
			s.split("/");
			String[] ss = s.split("/");
			typefield.add(ss);
			Sys.out(s);
		}

		Sys.out("Init person Data");

		persondata = new ArrayList<Map<String, Object>>();
		JSONArray ja = new JSONArray(PropKit.get("persondata"));
		for (int i = 0; i < ja.length(); i++) {
			JSONObject js = (JSONObject) ja.get(i);
			String s = js.get("grade").toString();
			String data = js.getString("data");
			Sys.out(data + ":" + s);

			List<String> l = new ArrayList<String>();
			for (String son : s.split("/"))
				l.add(son);

			Map<String, Object> m = new HashMap<String, Object>();
			m.put("data", data);
			m.put("list", l);

			persondata.add(m);
		}

		orderpersondata = new ArrayList<Map<String, Object>>();
		ja = new JSONArray(PropKit.get("orderpersondata"));
		for (int i = 0; i < ja.length(); i++) {
			JSONObject js = (JSONObject) ja.get(i);
			String s = js.get("grade").toString();
			String data = js.getString("data");
			Sys.out(data + ":" + s);

			List<String> l = new ArrayList<String>();
			for (String son : s.split("/"))
				l.add(son);

			Map<String, Object> m = new HashMap<String, Object>();
			m.put("data", data);
			m.put("list", l);

			orderpersondata.add(m);
		}
		
		orderpersontrousersdata = new ArrayList<Map<String, Object>>();
		ja = new JSONArray(PropKit.get("orderpersontrousersdata"));
		for (int i = 0; i < ja.length(); i++) {
			JSONObject js = (JSONObject) ja.get(i);
			String s = js.get("grade").toString();
			String data = js.getString("data");
			Sys.out(data + ":" + s);

			List<String> l = new ArrayList<String>();
			for (String son : s.split("/"))
				l.add(son);

			Map<String, Object> m = new HashMap<String, Object>();
			m.put("data", data);
			m.put("list", l);

			orderpersontrousersdata.add(m);
		}
		
		String [] ss = PropKit.get("good_class").split("/");
		for(String s : ss){
			good_class.add(s);
		}
		

		Sys.out("End Init");
	}

	public static int getI(String[] type, String s) {

		for (int i = 0; i < type.length; i++) {
			if (s.equals(type[i]))
				return i;

		}
		return -1;
	}

}
