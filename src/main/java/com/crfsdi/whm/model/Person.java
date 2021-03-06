package com.crfsdi.whm.model;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.Data;

@Data
public class Person {
	public static final String DEFAULT_PASSWORD = "888888";
    private Long id;
    private String username;
    private String password;
    private String staffName;
    private String gender;
    private Date birthDate;
    private String position;
    private String jobTitle;
    private Integer level = 0;
    private Integer sys = 0;
    private Integer status = 1;
    private String openId;
    private Date createDate;
    private Date UpdateDate;
    private List<Role> roles;
    
    public int age() {
    	if(this.getBirthDate()==null) {
    		return 0;
    	}else {
    		Calendar ca = Calendar.getInstance();
    		ca.setTime(this.getBirthDate());
    		return Calendar.getInstance().get(Calendar.YEAR)- ca.get(Calendar.YEAR);
    	}
    }
    
    public String gender() {
    	if(this.getGender()==null) {
    		return "";
    	}else {
    		return "M".equalsIgnoreCase(this.getGender())?"男":"女";
    	}
    	
    }
    
    public static String currentUsername() {
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	return (auth != null) ? auth.getName() : null;
    }
}
