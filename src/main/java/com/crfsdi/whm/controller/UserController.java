package com.crfsdi.whm.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
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

	
	
    @RequestMapping("/getCurrentUser")
    public Person getCurrentUser() {
    	String  username = Person.currentUsername();
    	log.info("get current user: {}", username);
    	return userRepo.findByUsername(username);
    }
	
    @RequestMapping("/get/{id}")
    public Person load(@PathVariable("id") Long id) {
    	log.info("get user, id: {}", id);
    	return userRepo.load(id);
    }
    
    @PostMapping("/save")
    @PreAuthorize("'admin'==authentication.principal or hasRole('ADMIN')")
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
    @PreAuthorize("'admin'==authentication.principal or hasRole('ADMIN')")
    public Person add(@RequestBody Person user) {
    	log.info("add user: {}", user);
    	userRepo.save(user);
    	return user;
    }
    
    @PostMapping("/update")
    @PreAuthorize("'admin'==authentication.principal or hasRole('ADMIN')")
    public void update(@RequestBody Person user) {
    	log.info("update user: {}",user);
    	userRepo.update(user);
    }
    
    @RequestMapping("/del/{id}")
    @PreAuthorize("'admin'==authentication.principal or hasRole('ADMIN')")
    public void delete(@PathVariable("id") Long id) {
    	log.info("delete user, id: {}", id);
    	userRepo.delete(id);
    }
    
    @RequestMapping("/list/{page},{size}")
    @PreAuthorize("'admin'==authentication.principal or hasRole('ADMIN')")
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
    @PreAuthorize("'admin'==authentication.principal or hasRole('ADMIN')")
    public void resetPwd(@RequestBody Person user) {
        user.setPassword(bCryptPasswordEncoder.encode(Person.DEFAULT_PASSWORD));
        userRepo.update(user);
    }
    
    @RequestMapping("/resetAdminPwd")
    public String resetAdminPwd(HttpServletRequest request) {
    	if(!isLocalIp(request)) {
    		return "No permit!";
    	}
    	Person user = new Person();
    	user.setUsername("admin");
    	user.setSys(1);
        user.setPassword(bCryptPasswordEncoder.encode(Person.DEFAULT_PASSWORD));
        userRepo.update(user);
        log.info("resetAdminPwd success!");
        return "resetAdminPwd success!";
    }
    
    @RequestMapping("/grantAsAdmin/{staffNo}")
    @PreAuthorize("'admin'==authentication.principal")
    public String setAsAdmin(HttpServletRequest request, @PathVariable("staffNo") String staffno) {
        userRepo.setAdminRole(staffno);
        log.info("Grant As Admin success!");
        return "Grant As Admin success!";
    }
    
    @RequestMapping("/cancelAsAdmin/{staffNo}")
    @PreAuthorize("'admin'==authentication.principal")
    public String delAdminRole(HttpServletRequest request, @PathVariable("staffNo") String staffno) {
        userRepo.delAdminRole(staffno);
        log.info("cancel As Admin success!");
        return "cancel As Admin success!";
    }
    


	@RequestMapping("/resetAdmin")
    public String resetAdmin(HttpServletRequest request) {
    	if(!isLocalIp(request)) {
    		return "No permit!";
    	}
        Person admin = userRepo.findByUsername("admin");
        if(admin != null) {
        	Person user = new Person();
        	user.setUsername("admin");
        	user.setSys(1);
            user.setPassword(bCryptPasswordEncoder.encode(Person.DEFAULT_PASSWORD));
            userRepo.update(user);
        }else {
        	Person user = new Person();
        	user.setUsername("admin");
        	user.setStaffName("admin");
        	user.setSys(1);
            user.setPassword(bCryptPasswordEncoder.encode(Person.DEFAULT_PASSWORD));
            userRepo.save(user);
        }
        log.info("resetAdmin success!");
        return "resetAdmin success!";
    }
    
    @PostMapping("/changePwd")
    public void changePwd(@RequestBody Person user) {
    	String  username = Person.currentUsername();
    	Person user0 = new Person();
    	user0.setUsername(username);
    	user0.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepo.update(user0);
    }
    
    private boolean isLocalIp(HttpServletRequest request) {
		try {
			return "localhost".equalsIgnoreCase(getIpAddr(request)) || "0:0:0:0:0:0:0:1".equalsIgnoreCase(getIpAddr(request));
		} catch (Exception e) {
			log.warn("isLocalIp: {}", e.getMessage());
			return false;
		}
	}
    
    private static final String getIpAddr(final HttpServletRequest request)  
            throws Exception {  
        if (request == null) {  
            throw (new Exception("getIpAddr method HttpServletRequest Object is null"));  
        }  
        String ipString = request.getHeader("x-forwarded-for");  
        if (StringUtils.isEmpty(ipString) || "unknown".equalsIgnoreCase(ipString)) {  
            ipString = request.getHeader("Proxy-Client-IP");  
        }  
        if (StringUtils.isEmpty(ipString) || "unknown".equalsIgnoreCase(ipString)) {  
            ipString = request.getHeader("WL-Proxy-Client-IP");  
        }  
        if (StringUtils.isEmpty(ipString) || "unknown".equalsIgnoreCase(ipString)) {  
            ipString = request.getRemoteHost(); 
        }  
      
        // 多个路由时，取第一个非unknown的ip  
        final String[] arr = ipString.split(",");  
        for (final String str : arr) {  
            if (!"unknown".equalsIgnoreCase(str)) {  
                ipString = str;  
                break;  
            }  
        }  
        log.info("getIpAddr, ip: {}", ipString);
        return ipString;  
    } 
}