package com.crfsdi.whm.model;

import java.util.Calendar;
import java.util.Date;

import lombok.Data;

@Data
public class WorkTimeSheet {
	private static final String[] TYPE_PRES = {"院控铁路","自揽铁路","自揽公路","自揽市政","科研业建"};
	private static final String[] POSITION_PRES = {"总体","副总体","专业设计负责人","技术队长","内业组长"};
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
		if(!check()) {
			return 0D;
		}
		String prjType = this.getProject().getType();
		if(prjType.startsWith(TYPE_PRES[0])
				&& this.getPrjPosition().startsWith(POSITION_PRES[0])
				&& this.getPrjPosition().startsWith(POSITION_PRES[1])
				&& this.getPrjPosition().startsWith(POSITION_PRES[2])
				&& this.getPrjPosition().startsWith(POSITION_PRES[3])
				&& this.getPrjPosition().startsWith(POSITION_PRES[4])
				) {
			if(this.getProject().getLe()<=100) {
				return 1.0D;
			}else if(this.getProject().getLe()<=300) {
				return 1.0D + (this.getProject().getLe()-100)* 0.1 / 100;
			}else if(this.getProject().getLe()<=500) {
				return 1.2D + (this.getProject().getLe()-100)* 0.1 / 200;
			}else {
				return 1.3D;
			}
			
		}else {
			
		}
		return 0D; 

	}
	private boolean check() {
		return this.getProject()!=null 
				&& this.getProject().getType()!=null 
				&& this.getPrjPosition()!=null;
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
