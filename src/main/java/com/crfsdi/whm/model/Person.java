package com.crfsdi.whm.model;

import java.util.Date;
import java.util.List;

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
    private Integer level;
    private Integer sys = 0;
    private Integer status = 1;
    private Date createDate;
    private Date UpdateDate;
    private List<Role> roles;
}