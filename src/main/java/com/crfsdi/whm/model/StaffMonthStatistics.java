package com.crfsdi.whm.model;

import java.util.List;

import lombok.Data;

@Data
public class StaffMonthStatistics {
	//eg.201807
	private Long month;
	//本月工时识别率
	private double monthOccurRate;
    //本月工时填报率
	private double monthFillRate;
	 //本月排名	
	 private long ranking;
	 //排名总数
	 private long count;
	 //本月总积点
	 private double sumOfPoints;
	 //员工
     private Person staff;
     private List<WorkTimeSheet> worksheets;
     
     public int numOfProjects() {
    	 return (worksheets != null) ? worksheets.size() : 0;
     }
}
