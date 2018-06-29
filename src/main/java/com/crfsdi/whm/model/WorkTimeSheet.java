package com.crfsdi.whm.model;

import java.util.Date;

import lombok.Data;

@Data
public class WorkTimeSheet {
	private Long id;
	private String staffId;
	private String month;
	private Long prjId;
	private String prjPhase;
	private String prjPosition;
	private Date startDate;
	private Date endDate;
	private Date createTime;
	private Double work_comfirm;
	private Double points;
}