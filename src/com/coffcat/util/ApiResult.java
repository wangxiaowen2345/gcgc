package com.coffcat.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.jfinal.weixin.sdk.api.ReturnCode;

public class ApiResult {
	
	private Map<String, Object> attrs;
	private String json;
	
	/**
	 * 閫氳繃 json 鏋勯� ApiResult锛屾敞鎰忚繑鍥炵粨鏋滀笉涓�json 鐨�api锛堝鏋滄湁鐨勮瘽锛�
	 * @param jsonStr json瀛楃涓�
	 */
	@SuppressWarnings("unchecked")
	public ApiResult(String jsonStr) {
		this.json = jsonStr;
		
		try {
			JSONObject js  = new JSONObject(jsonStr);
			String [] s = JSONObject.getNames(js);
			Map<String, Object> temp = new HashMap<String, Object>();
			for(String son : s){
				temp.put(son, js.get(son));
			}
			this.attrs = temp;
			
			refreshAccessTokenIfInvalid();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 閫氳繃 json 鍒涘缓 ApiResult 瀵硅薄锛岀瓑浠蜂簬 new ApiResult(jsonStr)
	 * @param jsonStr json瀛楃涓�
	 * @return {ApiResult}
	 */
	public static ApiResult create(String jsonStr) {
		return new ApiResult(jsonStr);
	}
	
	/**
	 * 濡傛灉 api 璋冪敤杩斿洖缁撴灉琛ㄦ槑 access_token 鏃犳晥锛屽垯鍒锋柊瀹�
	 * 姝ｅ父鎯呭喌涓嬩笉浼氬嚭鐜颁娇鐢ㄦ湰鏂规硶鍒锋柊 access_token 鐨勬搷浣滐紝闄ら潪浠ヤ笅鎯呭喌鍙戠敓锛�
	 * 1锛氬彟涓�▼搴忛噸鏂拌幏鍙栦簡璇ュ叕浼楀彿鐨�access_token
	 * 2锛氫娇鐢ㄥ井淇″叕浼楀钩鍙版帴鍙ｈ皟璇曞伐鍏疯幏鍙栦簡璇ュ叕浼楀彿鐨�access_token锛屾鎯呭喌鏈川涓婁笌 1 涓儏鍐电浉鍚�
	 * 3锛氬井淇℃湇鍔″櫒閲嶆柊璋冩暣浜嗚繃鏈熸椂闂存垨鑰呭彂鐢熷叾瀹�access_token 寮傚父鎯呭喌
	 *
	 * 2016-06-21 by L.cm 娣诲姞 synchronized 閿佹劅璋it@osc #Lucare
	 *
	 */
	private synchronized void refreshAccessTokenIfInvalid() {
		if (isAccessTokenInvalid())
			AccessTokenApi.refreshAccessToken();
	}
	
	public String getJson() {
		return json;
	}
	
	public String toString() {
		return getJson();
	}
	
	/**
	 * APi 璇锋眰鏄惁鎴愬姛杩斿洖
	 * @return {boolean}
	 */
	public boolean isSucceed() {
		Integer errorCode = getErrorCode();
		// errorCode 涓�0 鏃朵篃鍙互琛ㄧず涓烘垚鍔燂紝璇﹁锛歨ttp://mp.weixin.qq.com/wiki/index.php?title=%E5%85%A8%E5%B1%80%E8%BF%94%E5%9B%9E%E7%A0%81%E8%AF%B4%E6%98%8E
		return (errorCode == null || errorCode == 0);
	}
	
	public Integer getErrorCode() {
		return getInt("errcode");
	}
	
	public String getErrorMsg() {
		Integer errorCode = getErrorCode();
		if (errorCode != null) {
			String result = ReturnCode.get(errorCode);
			if (result != null)
				return result;
		}
		return (String)attrs.get("errmsg");
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(String name) {
		return (T)attrs.get(name);
	}
	
	public String getStr(String name) {
		return (String)attrs.get(name);
	}
	
	public Integer getInt(String name) {
		Number number = (Number) attrs.get(name);
		return number == null ? null : number.intValue();
	}
	
	public Long getLong(String name) {
		Number number = (Number) attrs.get(name);
		return number == null ? null : number.longValue();
	}
	
	public BigInteger getBigInteger(String name) {
		return (BigInteger)attrs.get(name);
	}
	
	public Double getDouble(String name) {
		return (Double)attrs.get(name);
	}
	
	public BigDecimal getBigDecimal(String name) {
		return (BigDecimal)attrs.get(name);
	}
	
	public Boolean getBoolean(String name) {
		return (Boolean)attrs.get(name);
	}
	
	@SuppressWarnings("rawtypes")
	public List getList(String name) {
		return (List)attrs.get(name);
	}
	
	@SuppressWarnings("rawtypes")
	public Map getMap(String name) {
		return (Map)attrs.get(name);
	}
	
	/**
	 * 鍒ゆ柇 API 璇锋眰缁撴灉澶辫触鏄惁鐢变簬 access_token 鏃犳晥寮曡捣鐨�
	 * 鏃犳晥鍙兘鐨勬儏鍐�error_code 鍊硷細
	 * 40001 = 鑾峰彇access_token鏃禔ppSecret閿欒锛屾垨鑰卆ccess_token鏃犳晥(鍒锋柊鍚庝篃鍙互寮曡捣鑰乤ccess_token澶辨晥)
	 * 42001 = access_token瓒呮椂
	 * 42002 = refresh_token瓒呮椂
	 * 40014 = 涓嶅悎娉曠殑access_token
	 * @return {boolean}
	 */
	public boolean isAccessTokenInvalid() {
		Integer ec = getErrorCode();
		return ec != null && (ec == 40001 || ec == 42001 || ec == 42002 || ec == 40014);
	}
}





