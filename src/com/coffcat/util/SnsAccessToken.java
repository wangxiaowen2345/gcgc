package com.coffcat.util;

import java.io.Serializable;
import java.util.Map;

import org.json.JSONObject;

import com.jfinal.weixin.sdk.api.ReturnCode;
import com.jfinal.weixin.sdk.utils.RetryUtils.ResultCheck;

public class SnsAccessToken implements ResultCheck, Serializable 
{
    
    private static final long serialVersionUID = 6369625123403343963L;
    
    private String access_token;    
    private Integer expires_in;        
    private String refresh_token;    
    private String openid;    
    private String scope;   
    private String unionid;    
    private Integer errcode;        
    private String errmsg;            

    private Long expiredTime;        
    private String json;

    public SnsAccessToken(String jsonStr)
    {
        this.json = jsonStr;

        try
        {
            @SuppressWarnings("unchecked")
           // Map<String, Object> temp = JsonUtils.parse(jsonStr, Map.class);
            
            JSONObject temp = new JSONObject(jsonStr);
            
            access_token = (String) temp.get("access_token");
            expires_in = temp.getInt("expires_in");
            refresh_token = (String) temp.get("refresh_token");
            openid = (String) temp.get("openid");
            unionid = (String) temp.get("unionid");
            scope = (String) temp.get("scope");
            if(temp.has("errcode"))
            	errcode = temp.getInt("errcode");
            else 
            	errcode=null;
            if(temp.has("errmsg"))
            	errmsg = (String) temp.get("errmsg");
            else 
            	errmsg=null;

            if (expires_in != null)
                expiredTime = System.currentTimeMillis() + ((expires_in - 5) * 1000);

        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public String getJson()
    {
        return json;
    }

    private Integer getInt(Map<String, Object> temp, String key) {
        Number number = (Number) temp.get(key);
        return number == null ? null : number.intValue();
    }
    
    public boolean isAvailable()
    {
        if (expiredTime == null)
            return false;
        if (errcode != null)
            return false;
        if (expiredTime < System.currentTimeMillis())
            return false;
        return access_token != null;
    }

    public String getAccessToken()
    {
        return access_token;
    }

    public Integer getExpiresIn()
    {
        return expires_in;
    }

    public String getRefresh_token()
    {
        return refresh_token;
    }

    public String getOpenid()
    {
        return openid;
    }

    public String getScope()
    {
        return scope;
    }

    public Integer getErrorCode()
    {
        return errcode;
    }

    public String getErrorMsg()
    {
        if (errcode != null)
        {
            String result = ReturnCode.get(errcode);
            if (result != null)
                return result;
        }
        return errmsg;
    }

    public String getUnionid()
    {
        return unionid;
    }

	@Override
	public boolean matching() {
		return isAvailable();
	}

}
