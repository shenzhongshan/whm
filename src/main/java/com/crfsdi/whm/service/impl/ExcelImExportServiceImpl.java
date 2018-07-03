package com.crfsdi.whm.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import com.crfsdi.whm.model.Person;
import com.crfsdi.whm.model.Project;
import com.crfsdi.whm.model.WorkAtendance;
import com.crfsdi.whm.repository.ProjectRepository;
import com.crfsdi.whm.repository.UserRepository;
import com.crfsdi.whm.repository.WorkAtendanceRepository;
import com.crfsdi.whm.repository.WorkTimeSheetRepository;
import com.crfsdi.whm.service.ExcelImExportService;
import com.crfsdi.whm.utils.CodeUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ExcelImExportServiceImpl implements ExcelImExportService {

@Autowired
   private WorkAtendanceRepository waRepo;
   @Autowired
   private WorkTimeSheetRepository wtsRepo;
   @Autowired
   private UserRepository userRepo;
   @Autowired
   private ProjectRepository projectRepo;
   @Autowired
   private BCryptPasswordEncoder bCryptPasswordEncoder;

   @FunctionalInterface
   private interface ParseRow<T>{
	   T parse(Row row);
   }
	   
	@Override
	public void importWorkAtendanceByMonth(File infile, String month) {
		List<WorkAtendance> rows = parseRows(infile, (Row r)->{
		   log.info("Importing Work Atendance row: {}.", r.getRowNum());
		   WorkAtendance cells = new WorkAtendance();
		   cells.setMonth(month);
		   short minColIx = r.getFirstCellNum();
		   short maxColIx = r.getLastCellNum();
		   //序号,工号,姓名,正班人次识别率,工时填报率
		   for(short colIx = minColIx; colIx < maxColIx; colIx++) {
		     Cell c = r.getCell(colIx);
		     if(c == null) {
		       continue;
		     }
		     if(colIx == 1) {//工号
		    	 c.setCellType(CellType.STRING);
		    	 String userName = StringUtils.trim(c.getStringCellValue());
		    	 if(StringUtils.isEmpty(userName)) {//人员工号为空则忽略该行
		    		 log.warn("人员工号为空,忽略该行:{}",r.getRowNum());
		    		 return null;
		    	 }
		    	 cells.setStaffId(userName);
		     }
		     
		     if(colIx == 3) {//正班人次识别率
		    	 if(c.getCellTypeEnum()==CellType.NUMERIC) {
		    		 cells.setMonthOccurRate(c.getNumericCellValue());
		    	 }else {
		    		 String val = StringUtils.trim(c.getStringCellValue());
			    	 cells.setMonthOccurRate(Double.parseDouble(val));
		    	 }

		     }
		     
		     if(colIx == 4) {//工时填报率
		    	 if(c.getCellTypeEnum()==CellType.NUMERIC) {
		    		 cells.setMonthFillRate(c.getNumericCellValue());
		    	 }else {
		    		 String val = StringUtils.trim(c.getStringCellValue());
			    	 cells.setMonthFillRate(Double.parseDouble(val));
		    	 }
		     }
		     
		   }
		   log.info("Import row end: {}.", cells);
	       return cells;
		});
		this.waRepo.saveMore(rows);
	}





	@Override
	public void importStaff(File infile) {
		List<Person> rows = parseRows(infile, (Row r)->{
			   log.info("Importing Staff row: {}.", r.getRowNum());
			   Person person = new Person();
			   short minColIx = r.getFirstCellNum();
			   short maxColIx = r.getLastCellNum();
			   //序号,工号,姓名,性别,年龄,职务,职称,	岗位层级
			   for(short colIx = minColIx; colIx < maxColIx; colIx++) {
			     Cell c = r.getCell(colIx);
			     if(c == null) {
			       continue;
			     }
			     if(colIx == 1) {//工号
			    	 c.setCellType(CellType.STRING);
			    	 String userName = StringUtils.trim(c.getStringCellValue());
			    	 if(StringUtils.isEmpty(userName)) {//人员工号为空则忽略该行
			    		 log.warn("人员工号为空,忽略该行:{}",r.getRowNum());
			    		 return null;
			    	 }
			    	 person.setUsername(userName);
			     }
			     if(colIx == 2) {//姓名
			    	 person.setStaffName(StringUtils.trim(c.getStringCellValue()));
			     }
			     if(colIx == 3) {//性别
			    	 String val = StringUtils.trim(c.getStringCellValue());
			    	 String gender = null;
			    	 if("男".equals(val)) {
			    		 gender = "M";
			    	 }			    	
			    	 if("女".equals(val)) {
			    		 gender = "F";
			    	 }
			    	 person.setGender(gender);
			     }
			     
			     if(colIx == 4) {//出生日期
			    	 if(c.getCellTypeEnum()==CellType.NUMERIC) {
			    		 person.setBirthDate(c.getDateCellValue());
			    	 }else {
				    	 String val = StringUtils.trim(c.getStringCellValue());
				    	 person.setBirthDate(DateUtil.parseYYYYMMDDDate(val));
			    	 }

			     }
			     
			     if(colIx == 5) {//职务:PN001-主任,PN002-副主任,PN003-一般设计人员
			    	 String val = CodeUtil.convertPosition(c.getStringCellValue());
			    	 person.setPosition(val);
			     }
			     
			     if(colIx == 6) {//职称:JT001-教授级高级工程师,JT002-高级工程师任,JT003-工程师,JT004-助理工程师,JT005-见习生
			    	 String val = CodeUtil.convertJobTitle(c.getStringCellValue());
			    	 person.setJobTitle(val);
			     }
			     
			   }
			   log.info("Import row end: {}.", person);
		       return person;
			});
		
		for(Person p : rows) {
			try {
				Person dp = this.userRepo.findByUsername(p.getUsername());
				if(dp != null) {
					p.setPassword(null);//不更新密码字段
					this.userRepo.update(p);
				}else {
					p.setPassword(bCryptPasswordEncoder.encode(Person.DEFAULT_PASSWORD));//新人员设置缺省初始密码
					this.userRepo.save(p);
				}

			}catch(Exception e) {
				log.warn("保存或更新人员信息错误,{}", e.getMessage());
			}

		}
	}


	@Override
	public void importProjects(File infile, String month) {
		List<Project> rows = parseRows(infile, (Row r)->{
			   log.info("Importing Project row: {}.", r.getRowNum());
			   Project prj = new Project();
			   prj.setMonth(month);
			   short minColIx = r.getFirstCellNum();
			   short maxColIx = r.getLastCellNum();
			   //序号,项目名称,项目标准,项目规模,项目类型
			   for(short colIx = minColIx; colIx < maxColIx; colIx++) {
			     Cell c = r.getCell(colIx);
			     if(c == null) {
			       continue;
			     }
			     if(colIx == 1) {//项目名称
			    	 String prjName = StringUtils.trim(c.getStringCellValue());
			    	 if(StringUtils.isEmpty(prjName)) {//项目名称为空则忽略该行
			    		 log.warn("项目名称为空,忽略该行:{}",r.getRowNum());
			    		 return null;
			    	 }
			    	 prj.setName(prjName);
			     }
			     
			     if(colIx == 2) {//项目标准
			    	 prj.setStandard(StringUtils.trim(c.getStringCellValue()));
			     }
			     
			     if(colIx == 3) {//项目规模
			    	 if(c.getCellTypeEnum()==CellType.NUMERIC) {
			    		 prj.setScale(c.getNumericCellValue());
			    	 }else {
				    	 String val = StringUtils.trim(c.getStringCellValue());
				    	 prj.setScale(Double.parseDouble(val)); 
			    	 }

			     }
			     
			     if(colIx == 4) {//项目类型
			    	 prj.setType(StringUtils.trim(c.getStringCellValue()));
			     }
			     
			   }
			   log.info("Import row end: {}.", prj);
		       return prj;
			});
			this.projectRepo.savePreMore(rows);

	}

	@Override
	public void exportReportByYear(File outfile, int year) {
		// TODO Auto-generated method stub

	}
	
	private Sheet getImportSheet(File infile) {
		Sheet sheet = null;
		try {
			Workbook wb = WorkbookFactory.create(infile);
			sheet = wb.getSheetAt(0);
		} catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
             log.error("Get import sheet, file:{}, error:{}.", infile, e);
		}
		return sheet;
	}
	
	private <T> List<T> parseRows(File infile, ParseRow<T> pr) {
		Sheet sheet = getImportSheet(infile);
		List<T> rows = null;
		if(sheet != null) {
		    int rowStart = Math.min(15, sheet.getFirstRowNum());
		    int rowEnd = Math.max(1000, sheet.getLastRowNum());
		    rows = new ArrayList<T>();
		    //第一行为标题
		    for (int rowNum = rowStart + 1; rowNum < rowEnd; rowNum++) {
		       Row r = sheet.getRow(rowNum);
		       if (r == null) {
		          continue;
		       }
		       T robj = pr.parse(r);
		       if(robj != null) {
			       rows.add(pr.parse(r));
		       }
		    }
		}
		return rows;
	}

}
