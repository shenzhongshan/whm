package com.crfsdi.whm.model;

import java.util.Calendar;
import java.util.Date;

import lombok.Data;

@Data
public class WorkTimeSheet {
	private Long id;
	private String staffId;
	private Long month;
	private Long prjId;
	private String prjPhase;
	private String prjPosition;
	private Date startDate;
	private Date endDate;
	private Date createTime;
	private Date updateTime;
	private Double workComfirm;//填入的工效比例
	//个人绩效工天 = 项目延续天数 *（长度系数 A + 投资系数 B + 地形系 数 C-2） * 项目 类别系数 D*岗位系数 E * 工效比例 F
	private Double points;//绩效工天
	private Integer status;
	private Project project;
	
	
	//工效比例 F
	public Double getCf() {
		return this.workComfirm;
	}
	//工效比例 F
	public void setCf(Double cf) {
		this.workComfirm = cf;
	}
	
	public Double getPoints() {
		if(this.points!=null && this.points > Double.MIN_VALUE) {
			return this.points;
		}
		return this.getDays() * getCoef();
	}
	
	public double getCoef() {
		return (this.getCa() + this.getCb() + this.getCc() - 2) * this.getCd() * this.getCe() * this.getCf();
	}
	public long getDays() {
		Calendar caStart = Calendar.getInstance();
		caStart.setTime(this.getStartDate());
		Calendar caEnd = Calendar.getInstance();
		caEnd.setTime(this.getEndDate());
		return (caEnd.getTimeInMillis() - caStart.getTimeInMillis())/(24 * 3600 * 1000);
	}
	//长度系数 A
	public Double getCa(){
		return 0D; //CoefUtil.getCa(this.getPrjPosition(),this.getProject().getLe());
	}
	//投资系数 B
	public Double getCb(){
		return 0D;
	}
	//地形系 数 C
	public Double getCc(){
		return 0D;
	}
	//类别系数 D
	public Double getCd(){
		return 0D;
	}
	//岗位系数 E
	public Double getCe(){
		return 0D;
	}
	
}
