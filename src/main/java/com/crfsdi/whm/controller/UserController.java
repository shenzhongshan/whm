package com.crfsdi.whm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
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
    private UserRepository userRepo;
	
	@Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @RequestMapping("/get/{id}")
    public Person load(@PathVariable("id") Long id) {
    	log.info("get user, id: {}", id);
    	return userRepo.load(id);
    }
    
    @PostMapping("/save")
    public Person save(@RequestBody Person user) {
    	log.info("save user: {}", user);
    	if(user.getId() != null) {
        	userRepo.update(user);
    	}else {
        	userRepo.save(user);
    	}
    	return user;
    }
    
    @PostMapping("/add")
    public Person add(@RequestBody Person user) {
    	log.info("add user: {}", user);
    	userRepo.save(user);
    	return user;
    }
    
    @PostMapping("/update")
    public void update(@RequestBody Person user) {
    	log.info("update user: {}",user);
    	userRepo.update(user);
    }
    
    @RequestMapping("/del/{id}")
    public void delete(@PathVariable("id") Long id) {
    	log.info("delete user, id: {}", id);
    	userRepo.delete(id);
    }
    
    @RequestMapping("/list/{page},{size}")
    public List<Person> list(@RequestBody Person user,@PathVariable("page") Long page, @PathVariable("size") Long size) {
    	log.info("list users, page:{},page size:{},username:{},staffName:{}",page,size,user.getUsername(),user.getStaffName());
    	String uname = user.getUsername();
    	String sname = user.getStaffName();
    	return userRepo.listByPage(uname!=null?uname:"",sname!=null?sname:"",page,size);
    }
    
    @PostMapping("/signup")
    public void signUp(@RequestBody Person user) {
        log.info("这里注册的都算系统内置用户");
    	user.setSys(1);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepo.save(user);
    }
    
    @PostMapping("/resetPwd")
    public void resetPwd(@RequestBody Person user) {
        user.setPassword(bCryptPasswordEncoder.encode("888888"));
        userRepo.update(user);
    }
    
    @RequestMapping("/resetAdminPwd")
    public void resetAdminPwd() {
    	Person user = new Person();
    	user.setUsername("admin");
        user.setPassword(bCryptPasswordEncoder.encode("888888"));
        userRepo.update(user);
    }
    
    @RequestMapping("/resetAdmin")
    public void resetAdmin() {
        Person admin = userRepo.findByUsername("admin");
        if(admin != null) {
        	Person user = new Person();
        	user.setUsername("admin");
            user.setPassword(bCryptPasswordEncoder.encode("888888"));
            userRepo.update(user);
        }else {
        	Person user = new Person();
        	user.setUsername("admin");
        	user.setStaffName("admin");
        	user.setSys(1);
            user.setPassword(bCryptPasswordEncoder.encode("888888"));
            userRepo.save(user);
        }
    }
    
    @PostMapping("/changePwd")
    public void changePwd(@RequestBody Person user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepo.update(user);
    }
}