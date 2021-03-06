package com.crfsdi.whm.model;

import java.util.Date;

import lombok.Data;

@Data
public class WorkAtendance {
	private Long id;
	private Long month;
	private String staffId;
	private String staffName;
	private Double monthOccurRate;
	private Double monthFillRate;
    private Date createDate;
    private Date UpdateDate;

}
