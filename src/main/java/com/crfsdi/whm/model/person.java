package com.crfsdi.whm.model;

import java.util.Date;

import lombok.Data;

@Data
public class Person {

    private Long id;
    private String username;
    private String password;
    private String staffName;
    private String gender;
    private Date birthDate;
    private String position;
    private String jobTitle;
    private Integer level;
    private String sys;
}
