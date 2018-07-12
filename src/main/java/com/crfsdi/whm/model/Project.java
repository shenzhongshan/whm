package com.crfsdi.whm.model;

import java.util.Date;

import lombok.Data;

@Data
public class Project {
    private Long id;
    private Long prjId;
    private String month;
    private String name;
    private String standard;
    private String type;
    private Double scale;
    private Integer status;
    private Date createDate;
    private Date UpdateDate;
}
