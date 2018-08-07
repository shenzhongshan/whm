package com.crfsdi.whm.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public Object login(HttpServletRequest req, String authCode) {
    	log.info("weichat user login, authCode {}", authCode);
    	WechatAuthInfo authInfo= requestFromWechat(authCode);
    	return authInfo;
    }
	
    public WechatAuthInfo requestFromWechat(String authCode) {
    	String url = weixinApiUrl.replaceAll("APPID", weixinAppid).replaceAll("SECRET", weixinAppid).replaceAll("JSCODE", authCode);
        Map<String,String> params = new HashMap<>();
        params.put("APPID",weixinAppid);
        params.put("SECRET",weixinSecret);
        params.put("JSCODE",authCode);
        RestTemplate restTemplate = new RestTemplate();
        WechatAuthInfo request = restTemplate.getForObject(url,WechatAuthInfo.class,params);
    	return request;
    }
    
    @PostMapping("/bind")
    public String bind(String username, String password, String openid) {
    	log.info("bind wechat user, username:{},openid:{}", username, openid);
    	Person  user = userRepo.findByUsername(username);
    	if(user.getPassword().equals(bCryptPasswordEncoder.encode(password))) {
    		user.setOpenId(openid);
        	userRepo.update(user);
    	}
    	return "forward:/login";
    }
}