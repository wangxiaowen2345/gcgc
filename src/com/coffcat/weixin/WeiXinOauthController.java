package com.coffcat.weixin;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.coffcat.common.config.Conts;
import com.coffcat.util.ApiResult;
import com.coffcat.util.SnsAccessToken;
import com.coffcat.util.Sys;
import com.coffcat.util.UserApi;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.jfinal.ApiController;
import com.jfinal.weixin.sdk.kit.ParaMap;
import com.jfinal.weixin.sdk.utils.HttpUtils;

/**
 * @author hero
 * @email:mahaojie299@163.com
 * @version 创建时间：2016-12-30 上午10:33:54 程序的简单说明
 */
public class WeiXinOauthController extends ApiController {
	static Log log = Log.getLog(WeiXinOauthController.class);

	/**
	 * 如果要支持多公众账号，只需要在此返回各个公众号对应的 ApiConfig 对象即可 可以通过在请求 url 中挂参数来动态从数据库中获取
	 * ApiConfig 属性值
	 */
	public ApiConfig getApiConfig() {
		ApiConfig ac = new ApiConfig();

		// 配置微信 API 相关常量
		ac.setToken(PropKit.get("token"));
		ac.setAppId(PropKit.get("appId"));
		ac.setAppSecret(PropKit.get("appSecret"));
		
		ApiConfigKit.setThreadLocalApiConfig(ac);

		/**
		 * 是否对消息进行加密，对应于微信平台的消息加解密方式： 1：true进行加密且必须配置 encodingAesKey
		 * 2：false采用明文模式，同时也支持混合模式
		 */
		ac.setEncryptMessage(PropKit.getBoolean("encryptMessage", false));
		ac.setEncodingAesKey(PropKit.get("encodingAesKey",
				"setting it in config file"));
		return ac;
	}

	public void index() {
		int subscribe = 0;
		// 用户同意授权，获取code
		String code = getPara("code");
		String state = getPara("state");
		if (code != null) {
			String appId = ApiConfigKit.getApiConfig().getAppId();
			String secret = ApiConfigKit.getApiConfig().getAppSecret();
			
			log.info(state + " " +code + " " + appId);
			
			// 通过code换取网页授权access_token
			
			SnsAccessToken snsAccessToken = new SnsAccessToken(getSnsAccessToken(appId, secret, code));
			
			//this.renderText(snsAccessTokenjs);
			
			//String json=snsAccessToken.getJson();
			String token = snsAccessToken.getAccessToken();
			String openId = snsAccessToken.getOpenid();
			// 拉取用户信息(需scope为 snsapi_userinfo)
			ApiResult apiResult = getUserInfo(token, openId);

			log.warn("getUserInfo:" + apiResult.toString());
			if (apiResult.isSucceed()) {
				JSONObject jsonObject = new JSONObject(apiResult.getJson());
				String[] ss  = JSONObject.getNames(jsonObject);
				Map<String, Object> m = new HashMap<String, Object>();
				for(String s : ss){
					m.put(s, jsonObject.get(s));
				}
//				
//				String nickName = jsonObject.getString("nickname");
//				// 用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
//				int sex = jsonObject.getInt("sex");
//				String city = jsonObject.getString("city");// 城市
//				String province = jsonObject.getString("province");// 省份
//				String country = jsonObject.getString("country");// 国家
//				String headimgurl = jsonObject.getString("headimgurl");
//				String unionid = jsonObject.getString("unionid");
				
				//String filename = System.currentTimeMillis()+".png";
				
				//log.error(filename);
				
				//String header = PathKit.getWebRootPath() + "\\"
				//		+ Conts.BaseUploadDir + "\\" + filename;
				
				//saveImageToDisk(headimgurl, header);
				//m.put("header", Conts.BaseUploadDir + "/" + filename);
				
				// 获取用户信息判断是否关注
				ApiResult userInfo = UserApi.getUserInfo(openId);
				log.warn(JsonKit.toJson("is subsribe>>" + userInfo));
				if (userInfo.isSucceed()) {
					String userStr = userInfo.toString();
					subscribe = new JSONObject(userStr).getInt("subscribe");
				}
				
				
				
				setSessionAttr("wxdata_"+openId, m);

				// Users.me.save(openId,
				// WeiXinUtils.filterWeixinEmoji(nickName), unionid, headimgurl,
				// country, city, province, sex);
			}

			setSessionAttr("openId", openId);
			if (subscribe == 0) {
				redirect(PropKit.get("subscribe_rul"));
			} else {
				// 根据state 跳转到不同的页面
				if (state.equals("bd")) {
					redirect("/m/bd?openid="+openId);
				}else if (state.equals("person")) {
					redirect("/m/person?openid="+openId);
				}else if (state.equals("ltdata")) {
					redirect("/m/startltdata?openid="+openId);
				}
				else {
					redirect("/login");
				}
			}

		} else {
			renderText("code is  null");
		}
	}
	
	
	
	
	
	
	
	
	
	/*
	 * util
	 */
	
	public static String getSnsAccessToken(String appId, String secret, String code)
    {
        final Map<String, String> queryParas = ParaMap.create("appid", appId).put("secret", secret).put("code", code).getData();
        String json = HttpUtils.get(Conts.wxurl, queryParas);
        log.error(json);
        Sys.out(json);
        return json;
    }
	private static String getUserInfo = "https://api.weixin.qq.com/sns/userinfo";
	public static ApiResult getUserInfo(String accessToken, String openId)
    {
        ParaMap pm = ParaMap.create("access_token", accessToken).put("openid", openId).put("lang", "zh_CN");
        return new ApiResult(HttpUtils.get(getUserInfo, pm.getData()));
    }
	
	/**

	    * 根据文件id下载文件

	    * 

	    * @param mediaId

	    *            媒体id

	    * @throws Exception

	    */

	   public  InputStream getInputStream(String url) {

	      

	       InputStream is = null;


	       try {

	           URL urlGet = new URL(url);

	           HttpURLConnection http = (HttpURLConnection) urlGet

	                   .openConnection();

	           http.setRequestMethod("GET"); // 必须是get方式请求

	           http.setRequestProperty("Content-Type",

	                   "application/x-www-form-urlencoded");

	           http.setDoOutput(true);

	           http.setDoInput(true);

	           System.setProperty("sun.net.client.defaultConnectTimeout", "30000");// 连接超时30秒

	           System.setProperty("sun.net.client.defaultReadTimeout", "30000"); // 读取超时30秒

	           http.connect();

	           // 获取文件转化为byte流

	           is = http.getInputStream();

	       } catch (Exception e) {

	           e.printStackTrace();

	       }

	       return is;

	   }

	   /**

	    * 获取下载图片信息（jpg）

	    * 

	    * @param mediaId

	    *            文件的id

	    * @throws Exception

	    */

	   public void saveImageToDisk(String url,String path){

	       InputStream inputStream = getInputStream(url);

	       byte[] data = new byte[1024];

	       int len = 0;

	       FileOutputStream fileOutputStream = null;

	       try {

	           fileOutputStream = new FileOutputStream(path);

	           while ((len = inputStream.read(data)) != -1) {

	               fileOutputStream.write(data, 0, len);

	           }

	       } catch (IOException e) {

	           e.printStackTrace();

	       } finally {

	           if (inputStream != null) {

	               try {

	                   inputStream.close();

	               } catch (IOException e) {

	                   e.printStackTrace();

	               }

	           }

	           if (fileOutputStream != null) {

	               try {

	                   fileOutputStream.close();

	               } catch (IOException e) {

	                   e.printStackTrace();

	               }

	           }

	       }

	   }
}
