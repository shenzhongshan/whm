package com.crfsdi.whm.model;

import java.util.List;

import lombok.Data;

@Data
public class StaffMonthStatistics {
	//eg.201807
	private Long month;
	//本月工时识别率
	private Double monthOccurRate;
    //本月工时填报率
	private Double monthFillRate;
	 //本月排名	
	 private Long ranking;
	 //本月总积点
	 private Double sumOfPoints;
	 //员工
     private Person staff;
     private List<WorkTimeSheet> worksheets;
     
     public int numOfProjects() {
    	 return (worksheets != null) ? worksheets.size() : 0;
     }
}
