package com.crfsdi.whm.model;

import java.util.Date;

import lombok.Data;

@Data
public class Project {
    private Long id;
    private Long prjId = 0L;
    private Long month;
    private String name;
    private String standard;
    private String type;
    private Double scale;
    private Double co;//金额(亿元)
    private Double le;//长度(km)
    private Double te;//地形等级
    private Integer status = 0;
    private Date createDate;
    private Date UpdateDate;
}
