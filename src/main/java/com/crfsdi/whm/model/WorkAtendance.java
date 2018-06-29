package com.crfsdi.whm.model;

import lombok.Data;

@Data
public class WorkAtendance {
	private Long id;
	private String month;
	private String staffId;
	private Double monthOccurRate;
	private Double monthFillRate;

}
