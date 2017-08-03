package com.coffcat.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.jfinal.weixin.sdk.kit.ParaMap;
import com.jfinal.weixin.sdk.utils.HttpUtils;
import com.jfinal.weixin.sdk.utils.JsonUtils;

public class UserApi {
	
	private static String getUserInfo = "https://api.weixin.qq.com/cgi-bin/user/info";
	private static String getFollowers = "https://api.weixin.qq.com/cgi-bin/user/get";
	private static String batchGetUserInfo = "https://api.weixin.qq.com/cgi-bin/user/info/batchget?access_token=";

	/**
	 * 鑾峰彇鐢ㄦ埛鍩烘湰淇℃伅锛堝寘鎷琔nionID鏈哄埗锛�
	 * @param openId 鏅�鐢ㄦ埛鐨勬爣璇嗭紝瀵瑰綋鍓嶅叕浼楀彿鍞竴
	 * @return ApiResult
	 */
	public static ApiResult getUserInfo(String openId) {
		ParaMap pm = ParaMap.create("access_token", AccessTokenApi.getAccessTokenStr()).put("openid", openId).put("lang", "zh_CN");
		return new ApiResult(HttpUtils.get(getUserInfo, pm.getData()));
	}
	
	/**
	 * 鑾峰彇鐢ㄦ埛鍒楄〃
	 * @param nextOpenid 绗竴涓媺鍙栫殑OPENID锛屼笉濉粯璁や粠澶村紑濮嬫媺鍙�
	 * @return ApiResult
	 */
	public static ApiResult getFollowers(String nextOpenid) {
		ParaMap pm = ParaMap.create("access_token", AccessTokenApi.getAccessTokenStr());
		if (nextOpenid != null)
			pm.put("next_openid", nextOpenid);
		return new ApiResult(HttpUtils.get(getFollowers, pm.getData()));
	}
	
	/**
	 * 鑾峰彇鐢ㄦ埛鍒楄〃
	 * @return ApiResult
	 */
	public static ApiResult getFollows() {
		return getFollowers(null);
	}

	/**
	 * 鎵归噺鑾峰彇鐢ㄦ埛鍩烘湰淇℃伅, by Unas
	 * @param jsonStr json瀛楃涓�
	 * @return ApiResult
	 */
	public static ApiResult batchGetUserInfo(String jsonStr) {
		String jsonResult = HttpUtils.post(batchGetUserInfo + AccessTokenApi.getAccessTokenStr(), jsonStr);
		return new ApiResult(jsonResult);
	}
	
	/**
	 * 鎵归噺鑾峰彇鐢ㄦ埛鍩烘湰淇℃伅
	 * @param openIdList openid鍒楄〃
	 * @return ApiResult
	 */
	public static ApiResult batchGetUserInfo(List<String> openIdList) {
		Map<String, List<Map<String, Object>>> userListMap = new HashMap<String, List<Map<String, Object>>>();
		
		List<Map<String, Object>> userList = new ArrayList<Map<String,Object>>();
		for (String openId : openIdList) {
			Map<String, Object> mapData = new HashMap<String, Object>();
			mapData.put("openid", openId);
			mapData.put("lang", "zh_CN");
			userList.add(mapData);
		}
		userListMap.put("user_list", userList);
		
		return batchGetUserInfo(JsonUtils.toJson(userListMap));
	}
	
	private static String updateRemarkUrl = "https://api.weixin.qq.com/cgi-bin/user/info/updateremark?access_token=";
	
	/**
	 * 璁剧疆澶囨敞鍚�
	 * @param openid 鐢ㄦ埛鏍囪瘑
	 * @param remark 鏂扮殑澶囨敞鍚嶏紝闀垮害蹇呴』灏忎簬30瀛楃
	 * @return {ApiResult}
	 */
	public static ApiResult updateRemark(String openid, String remark) {
		String url = updateRemarkUrl + AccessTokenApi.getAccessTokenStr();
		
		Map<String, String> mapData = new HashMap<String, String>();
		mapData.put("openid", openid);
		mapData.put("remark", remark);
		String jsonResult = HttpUtils.post(url, JsonUtils.toJson(mapData));
		
		return new ApiResult(jsonResult);
	}
}