package com.crfsdi.whm.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jline.utils.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.configurationprocessor.json.JSONTokener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.crfsdi.whm.model.Person;
import com.crfsdi.whm.model.WechatAuthInfo;
import com.crfsdi.whm.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/wechat")
public class WechatUserController {
	@Autowired
    private UserRepository userRepo;
	
	@Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	// https://api.weixin.qq.com/sns/jscode2session?appid={APPID}&secret={SECRET}&js_code={JSCODE}&grant_type=authorization_code
    @Value("${com.crfsdi.weixin-api-url}")
    private String weixinApiUrl;

    @Value("${com.crfsdi.weixin-appid}")
    private String weixinAppid;

    @Value("${com.crfsdi.weixin-secret}")
    private String weixinSecret;

	@PostMapping("/login")
    public WechatAuthInfo login(HttpServletRequest req, @RequestParam String authCode) {
    	log.info("weichat user login, authCode {}", authCode);
    	WechatAuthInfo authInfo= requestFromWechat(authCode);
    	return authInfo;
    }
	
    public WechatAuthInfo requestFromWechat(@RequestParam String authCode) {
    	String url = weixinApiUrl;
        Map<String,String> params = new HashMap<>();
        params.put("APPID",weixinAppid);
        params.put("SECRET",weixinSecret);
        params.put("JSCODE",authCode);
        RestTemplate restTemplate = new RestTemplate();
        String request = restTemplate.getForObject(url,String.class,params);
        log.info("wechat result: {}", request);
        JSONObject obj = null;
		try {
			obj = (JSONObject) new JSONTokener(request).nextValue();
		} catch (JSONException e) {
	          Log.warn("JSONTokener error: {}", e);
		}
		return WechatAuthInfo.from(obj);
    }
    
    @PostMapping("/bind")
    public void bind(@RequestParam String username, @RequestParam String password, @RequestParam String openid, HttpServletRequest req,HttpServletResponse res) {
    	if(StringUtils.isEmpty(username) || StringUtils.isEmpty(openid)) {
    		throw new RuntimeException("username or openid are not empty");
    	}
    	log.info("bind wechat user, username:{},openid:{}", username, openid);
    	Person  user = userRepo.findByUsername(username);
    	Person  userOpenId = userRepo.findByUsername(openid);
    	if(bCryptPasswordEncoder.matches(password, user.getPassword())) {
        	if(userOpenId != null && !userOpenId.getId().equals(user.getId())) {
        		userOpenId.setOpenId("");
        		userRepo.update(userOpenId);
        	}
    		user.setOpenId(openid);
        	userRepo.update(user);
    	}
    }
}