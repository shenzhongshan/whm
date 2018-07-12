package com.crfsdi.whm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crfsdi.whm.model.Person;
import com.crfsdi.whm.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
	@Autowired
    private UserRepository userRepository;
	
	@Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @PostMapping("/signup")
    public void signUp(@RequestBody Person user) {
        log.info("这里注册的都算系统内置用户");
    	user.setSys(1);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }
    
    @PostMapping("/resetPwd")
    public void resetPwd(@RequestBody Person user) {
        user.setPassword(bCryptPasswordEncoder.encode("888888"));
        userRepository.update(user);
    }
    
    @PostMapping("/resetAdminPwd")
    public void resetPwd() {
    	Person user = new Person();
    	user.setUsername("admin");
        user.setPassword(bCryptPasswordEncoder.encode("888888"));
        userRepository.update(user);
    }
    
    @PostMapping("/changePwd")
    public void changePwd(@RequestBody Person user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.update(user);
    }
}