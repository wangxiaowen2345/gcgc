package com.coffcat.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.jfinal.weixin.sdk.api.ReturnCode;
import com.jfinal.weixin.sdk.utils.JsonUtils;
import com.jfinal.weixin.sdk.utils.RetryUtils.ResultCheck;

public class AccessToken implements ResultCheck, Serializable {
	
	private static final long serialVersionUID = -822464425433824314L;
	
	private String access_token;	// 姝ｇ‘鑾峰彇鍒�access_token 鏃舵湁鍊�
	private Integer expires_in;		// 姝ｇ‘鑾峰彇鍒�access_token 鏃舵湁鍊�
	private Integer errcode;		// 鍑洪敊鏃舵湁鍊�
	private String errmsg;			// 鍑洪敊鏃舵湁鍊�
	
	private Long expiredTime;		// 姝ｇ‘鑾峰彇鍒�access_token 鏃舵湁鍊硷紝瀛樻斁杩囨湡鏃堕棿
	private String json;
	
	@SuppressWarnings("unchecked")
	public AccessToken(String jsonStr) {
		this.json = jsonStr;

		try {
			JSONObject js  = new JSONObject(jsonStr);
			String [] s = JSONObject.getNames(js);
			Map<String, Object> temp = new HashMap<String, Object>();
			for(String son : s){
				temp.put(son, js.get(son));
			}
			access_token = (String) temp.get("access_token");
			expires_in = getInt(temp, "expires_in");
			errcode = getInt(temp, "errcode");
			errmsg = (String) temp.get("errmsg");

			if (expires_in != null)
				expiredTime = System.currentTimeMillis() + ((expires_in -5) * 1000);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getJson() {
		return json;
	}
	
	public boolean isAvailable() {
		if (expiredTime == null)
			return false;
		if (errcode != null)
			return false;
		if (expiredTime < System.currentTimeMillis())
			return false;
		return access_token != null;
	}
	
	private Integer getInt(Map<String, Object> temp, String key) {
		Number number = (Number) temp.get(key);
		return number == null ? null : number.intValue();
	}
	
	public String getAccessToken() {
		return access_token;
	}
	
	public Integer getExpiresIn() {
		return expires_in;
	}
	
	public Long getExpiredTime() {
		return expiredTime;
	}
	
	public Integer getErrorCode() {
		return errcode;
	}
	
	public String getErrorMsg() {
		if (errcode != null) {
			String result = ReturnCode.get(errcode);
			if (result != null)
				return result;
		}
		return errmsg;
	}

	@Override
	public boolean matching() {
		return isAvailable();
	}
}
