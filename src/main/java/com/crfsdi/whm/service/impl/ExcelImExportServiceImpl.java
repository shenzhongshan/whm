package com.crfsdi.whm.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import com.crfsdi.whm.model.Person;
import com.crfsdi.whm.model.Project;
import com.crfsdi.whm.model.StaffMonthStatistics;
import com.crfsdi.whm.model.WorkAtendance;
import com.crfsdi.whm.model.WorkTimeSheet;
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
	public void importWorkAtendanceByMonth(File infile, Long month) {
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
	public void importProjects(File infile, Long month) {
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
			this.projectRepo.saveMore(rows);

	}

	@Override
	public void exportReportByYear(File outfile, Integer year) {
		Workbook wb;
		if (outfile.getName().endsWith(".xls")) {
			wb = new HSSFWorkbook();
		} else {
			wb = new XSSFWorkbook();
		}
		Sheet reportSheet = wb.createSheet("report");
		int rownum = 0;
		for (int i = 0; i < 12; i++) {// 1 to 12 month
			Long thisMonth = year * 100L + i + 1;
			Row monthRow = reportSheet.createRow(rownum++);
			Cell monthCell = monthRow.createCell(0);
			monthCell.setCellValue(thisMonth.toString().replaceFirst(year.toString(), year.toString()+"-"));
			List<StaffMonthStatistics> monthStatistics = this.wtsRepo.listStaffMonthStatistics(thisMonth, null);
			if (monthStatistics != null) {
				for (StaffMonthStatistics s : monthStatistics) {
					rownum = createStaffBlock(s, reportSheet,rownum);
				}
				rownum++;
			}
		}

		// 输出到文件
		writeOut(outfile, wb);

	}

	private void writeOut(File outfile, Workbook wb) {
		FileOutputStream out = null;
		try {
			if (!outfile.exists()) {
				outfile.createNewFile();
			}
			out = new FileOutputStream(outfile);
			wb.write(out);
		} catch (IOException e) {
			log.error("IO Error", e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				log.warn("file close", e);
			}
		}
	}
	
	private Integer createStaffBlock(StaffMonthStatistics s, Sheet sheet, Integer rownum) {
		Integer rn = rownum;
		Person p = s.getStaff();
		Row staffTitleRow = sheet.createRow(rn++);
		Row staffValueRow = sheet.createRow(rn++);
		createStaffCells(s, p, staffTitleRow, staffValueRow);
		
		Row WorkTimeSheetTitleRow = sheet.createRow(rn++);
		createWorkTimeSheetTitleCells(WorkTimeSheetTitleRow);
		List<WorkTimeSheet> list = s.getWorksheets();
		if(list != null && list.size()>0) {
			for(WorkTimeSheet w : list) {
				Row WorkTimeSheetRow = sheet.createRow(rn++);
				createWorkTimeSheetCells(w,WorkTimeSheetRow);
			}
		}
		return rn;
	}





	private void createWorkTimeSheetCells(WorkTimeSheet w, Row WorkTimeSheetRow ) {
		int c = 1;
		//("项目编号");
		WorkTimeSheetRow.createCell(c++).setCellValue(w.getPrjId());
		Project prj = w.getProject();
		//("项目名称");
		WorkTimeSheetRow.createCell(c++).setCellValue(prj.getName());
		//("项目阶段");
		WorkTimeSheetRow.createCell(c++).setCellValue(w.getPrjPhase());
		//("项目标准");
		WorkTimeSheetRow.createCell(c++).setCellValue(prj.getStandard());
		//("项目规模");
		WorkTimeSheetRow.createCell(c++).setCellValue(prj.getScale());
		//("项目类型");
		WorkTimeSheetRow.createCell(c++).setCellValue(prj.getType());
		//("本人任职");
		WorkTimeSheetRow.createCell(c++).setCellValue(w.getPrjPosition());
		//("本月起始日期");
		WorkTimeSheetRow.createCell(c++).setCellValue(w.getStartDate());
		//("本月结束日期");
		WorkTimeSheetRow.createCell(c++).setCellValue(w.getEndDate());
		//("工效认定");
		WorkTimeSheetRow.createCell(c++).setCellValue(w.getWorkComfirm());
		//("本项工作积点");
		WorkTimeSheetRow.createCell(c++).setCellValue(w.getPoints());
		
	}





	private void createWorkTimeSheetTitleCells(Row workTimeSheetTitleRow) {
		int c = 1;
		workTimeSheetTitleRow.createCell(c++).setCellValue("项目编号");

		workTimeSheetTitleRow.createCell(c++).setCellValue("项目名称");

		workTimeSheetTitleRow.createCell(c++).setCellValue("项目阶段");

		workTimeSheetTitleRow.createCell(c++).setCellValue("项目标准");

		workTimeSheetTitleRow.createCell(c++).setCellValue("项目规模");

		workTimeSheetTitleRow.createCell(c++).setCellValue("项目类型");

		workTimeSheetTitleRow.createCell(c++).setCellValue("本人任职");

		workTimeSheetTitleRow.createCell(c++).setCellValue("本月起始日期");

		workTimeSheetTitleRow.createCell(c++).setCellValue("本月结束日期");

		workTimeSheetTitleRow.createCell(c++).setCellValue("工效认定");

		workTimeSheetTitleRow.createCell(c++).setCellValue("本项工作积点");

	}





	private void createStaffCells(StaffMonthStatistics s, Person p, Row staffTitleRow, Row staffValueRow) {
		int c = 1;
		staffTitleRow.createCell(c).setCellValue("序号");
		staffValueRow.createCell(c++).setCellValue(s.getRanking());
		staffTitleRow.createCell(c).setCellValue("工号");
		staffValueRow.createCell(c++).setCellValue(p.getUsername());
		staffTitleRow.createCell(c).setCellValue("姓名");
		staffValueRow.createCell(c++).setCellValue(p.getStaffName());
		staffTitleRow.createCell(c).setCellValue("性别");
		staffValueRow.createCell(c++).setCellValue(p.gender());
		staffTitleRow.createCell(c).setCellValue("年龄");
		staffValueRow.createCell(c++).setCellValue(p.age());
		staffTitleRow.createCell(c).setCellValue("职务");
		staffValueRow.createCell(c++).setCellValue(CodeUtil.getJobTitle(p.getJobTitle()));
		staffTitleRow.createCell(c).setCellValue("职称");
		staffValueRow.createCell(c++).setCellValue(CodeUtil.getPosition(p.getPosition()));
		staffTitleRow.createCell(c).setCellValue("岗位层级");
		staffValueRow.createCell(c++).setCellValue(String.valueOf(p.getLevel()));
		staffTitleRow.createCell(c).setCellValue("本月工时识别率");
		staffValueRow.createCell(c++).setCellValue(s.getMonthOccurRate());
		staffTitleRow.createCell(c).setCellValue("本月工时填报率");
		staffValueRow.createCell(c++).setCellValue(s.getMonthOccurRate());
		staffTitleRow.createCell(c).setCellValue("本月总积点");
		staffValueRow.createCell(c++).setCellValue(s.getSumOfPoints());
		staffTitleRow.createCell(c).setCellValue("本月室排名");
		staffValueRow.createCell(c++).setCellValue(s.getRanking()+"/"+s.getCount());
	}





	private Sheet getImportSheet(File infile) {
		Sheet sheet = null;
		try {
			Workbook wb = WorkbookFactory.create(infile);
			sheet = wb.getSheetAt(0);
		} catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
             log.error("Get import sheet, file:{}, error:{}.", infile, e);
		}finally {
			infile.deleteOnExit();
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
