package com.coffcat.util;

import java.util.Map;
import java.util.concurrent.Callable;


import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.cache.IAccessTokenCache;
import com.jfinal.weixin.sdk.kit.ParaMap;
import com.jfinal.weixin.sdk.utils.HttpUtils;
import com.jfinal.weixin.sdk.utils.RetryUtils;

public class AccessTokenApi {
	
	// "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
	private static String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential";
	
	// 鍒╃敤 appId 涓�accessToken 寤虹珛鍏宠仈锛屾敮鎸佸璐︽埛
	static IAccessTokenCache accessTokenCache = ApiConfigKit.getAccessTokenCache();
	
	/**
	 * 浠庣紦瀛樹腑鑾峰彇 access token锛屽鏋滄湭鍙栧埌鎴栬� access token 涓嶅彲鐢ㄥ垯鍏堟洿鏂板啀鑾峰彇
	 * @return AccessToken accessToken
	 */
	public static AccessToken getAccessToken() {
		String appId = ApiConfigKit.getApiConfig().getAppId();
		AccessToken result = accessTokenCache.get(appId);
		if (result != null && result.isAvailable())
			return result;
		
		refreshAccessToken();
		return accessTokenCache.get(appId);
	}
	
	/**
	 * 鐩存帴鑾峰彇 accessToken 瀛楃涓诧紝鏂逛究浣跨敤
	 * @return String accessToken
	 */
	public static String getAccessTokenStr() {
		return getAccessToken().getAccessToken();
	}
	
	/**
	 * 寮哄埗鏇存柊 access token 鍊�
	 */
	public static synchronized void refreshAccessToken() {
		ApiConfig ac = ApiConfigKit.getApiConfig();
		String appId = ac.getAppId();
		String appSecret = ac.getAppSecret();
		final Map<String, String> queryParas = ParaMap.create("appid", appId).put("secret", appSecret).getData();
		
		// 鏈�涓夋璇锋眰
		AccessToken result = RetryUtils.retryOnException(3, new Callable<AccessToken>() {
			
			@Override
			public AccessToken call() throws Exception {
				String json = HttpUtils.get(url, queryParas);
				return new AccessToken(json);
			}
		});
		
		// 涓夋璇锋眰濡傛灉浠嶇劧杩斿洖浜嗕笉鍙敤鐨�access token 浠嶇劧 put 杩涘幓锛屼究浜庝笂灞傞�杩�AccessToken 涓殑灞炴�鍒ゆ柇搴曞眰鐨勬儏鍐�
		accessTokenCache.set(ac.getAppId(), result);
	}

}
