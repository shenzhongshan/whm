package com.crfsdi.whm.model;

import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.jline.utils.Log;

import lombok.Data;

@Data
public class WechatAuthInfo {
    private String openId;
    private String sessionKey;
	public static WechatAuthInfo from(JSONObject obj) {
		if(obj==null) {
			return null;
		}
		WechatAuthInfo ai =  new WechatAuthInfo();
		try {
			ai.setOpenId(obj.getString("openid"));
			ai.setSessionKey(obj.getString("session_key"));
		} catch (JSONException e) {
          Log.warn("WechatAuthInfo error: {}", e);
          return null;
		}
		return ai;
	}
}
