package com.crfsdi.whm.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
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
		for(WorkAtendance wa : rows) {
			try {
				List<WorkAtendance> list = this.waRepo.listByPage(wa.getStaffId(), month, 0L, 999L);
				if(list != null && !list.isEmpty()) {
					WorkAtendance dwa = list.get(0);
					dwa.setMonthFillRate(wa.getMonthFillRate());
					dwa.setMonthOccurRate(wa.getMonthOccurRate());
					this.waRepo.update(dwa);
				}else {
					this.waRepo.save(wa);
				}

			}catch(Exception e) {
				log.warn("保存或更新考勤信息错误,{}", e.getMessage());
			}

		}
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
			     
			     if(colIx == 3) {//项目长度km
			    	 if(c.getCellTypeEnum()==CellType.NUMERIC) {
			    		 prj.setLe(c.getNumericCellValue());
			    	 }else {
				    	 String val = StringUtils.trim(c.getStringCellValue());
				    	 prj.setLe(Double.parseDouble(val)); 
			    	 }

			     }
			     if(colIx == 4) {//项目投资(亿元)
			    	 if(c.getCellTypeEnum()==CellType.NUMERIC) {
			    		 prj.setCo(c.getNumericCellValue());
			    	 }else {
				    	 String val = StringUtils.trim(c.getStringCellValue());
				    	 prj.setCo(Double.parseDouble(val)); 
			    	 }

			     }
			     if(colIx == 5) {//项目地形等级
			    	 if(c.getCellTypeEnum()==CellType.NUMERIC) {
			    		 prj.setTe(c.getNumericCellValue());
			    	 }else {
				    	 String val = StringUtils.trim(c.getStringCellValue());
				    	 prj.setTe(Double.parseDouble(val)); 
			    	 }

			     }
			     if(colIx == 6) {//项目类型
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
		Map<String, CellStyle> styles = createStyles(wb);
		Sheet reportSheet = wb.createSheet("report");
		reportSheet.setFitToPage(true);
		reportSheet.setHorizontallyCenter(true);
		reportSheet.setColumnWidth(0, 8*256);
		reportSheet.setColumnWidth(1, 16*256);
		reportSheet.setColumnWidth(2, 20*256);
		reportSheet.setColumnWidth(3, 20*256);
		reportSheet.setColumnWidth(4, 20*256);
		reportSheet.setColumnWidth(5, 20*256);
		reportSheet.setColumnWidth(6, 20*256);
		reportSheet.setColumnWidth(7, 20*256);
		reportSheet.setColumnWidth(8, 16*256);
		reportSheet.setColumnWidth(9, 16*256);
		reportSheet.setColumnWidth(10, 10*256);
		reportSheet.setColumnWidth(11, 10*256);
		reportSheet.setColumnWidth(12, 10*256);
		int rownum = 0;
		for (int i = 0; i < 12; i++) {// 1 to 12 month
			Long thisMonth = year * 100L + i + 1;
			Row monthRow = reportSheet.createRow(rownum++);
			Cell monthCell = monthRow.createCell(0);
			monthCell.setCellValue(thisMonth.toString().replaceFirst(year.toString(), year.toString()+"-"));
			List<StaffMonthStatistics> monthStatistics = this.wtsRepo.listStaffMonthStatistics(thisMonth, null);
			
			Row staffTitleRow = reportSheet.createRow(rownum++);
			createStaffTitle(staffTitleRow,styles);
			Row WorkTimeSheetTitleRow = reportSheet.createRow(rownum++);
			createWorkTimeSheetTitleCells(WorkTimeSheetTitleRow,styles);
			
			if (monthStatistics != null) {
				for (StaffMonthStatistics s : monthStatistics) {
					rownum = createStaffBlock(s, reportSheet,rownum,styles);
				}
				rownum++;
			}
		}

		// 输出到文件
		writeOut(outfile, wb);

	}

	private Map<String, CellStyle> createStyles(Workbook wb) {
        Map<String, CellStyle> styles = new HashMap<>();
        CellStyle style;
        Font titleFont = wb.createFont();
        titleFont.setFontHeightInPoints((short)12);
        titleFont.setBold(true);
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(titleFont);
        styles.put("staffTitle", style);

        Font monthFont = wb.createFont();
        monthFont.setFontHeightInPoints((short)10);
        //monthFont.setColor(IndexedColors.WHITE.getIndex());
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(monthFont);
        style.setWrapText(true);
        styles.put("worksheetHeader", style);

        Font stringFont = wb.createFont();
        stringFont.setFontHeightInPoints((short)10);
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setWrapText(true);
        style.setFont(stringFont);
        styles.put("cellString", style);
        
        Font staffValueFont = wb.createFont();
        staffValueFont.setFontHeightInPoints((short)10);
        staffValueFont.setBold(true);
        style = wb.createCellStyle();
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SPARSE_DOTS);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setWrapText(true);
        style.setFont(staffValueFont);
        styles.put("cellStringStaff", style);

        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setDataFormat(wb.createDataFormat().getFormat("yyyy-MM-dd"));
        style.setFont(stringFont);
        styles.put("date", style);

        return styles;
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
	
	private Integer createStaffBlock(StaffMonthStatistics s, Sheet sheet, Integer rownum, Map<String, CellStyle> styles) {
		Integer rn = rownum;
		Person p = s.getStaff();

		
		Row staffValueRow = sheet.createRow(rn++);
		createStaffCells(s, p, staffValueRow, styles);
		List<WorkTimeSheet> list = s.getWorksheets();
		if(list != null && list.size()>0) {
			for(WorkTimeSheet w : list) {
				Row WorkTimeSheetRow = sheet.createRow(rn++);
				createWorkTimeSheetCells(w,WorkTimeSheetRow,styles);
			}
		}
		return rn;
	}





	private void createWorkTimeSheetCells(WorkTimeSheet w, Row WorkTimeSheetRow, Map<String, CellStyle> styles ) {
		int c = 1;
		Cell cell;
		//("项目编号");
		cell = WorkTimeSheetRow.createCell(c++);
		setCellVaue(cell,w.getPrjId());
		cell.setCellStyle(styles.get("cellString"));
		Project prj = w.getProject();
		//("项目名称");
		cell = WorkTimeSheetRow.createCell(c++);
		setCellVaue(cell,prj!=null?prj.getName():null);
		cell.setCellStyle(styles.get("cellString"));
		//("项目阶段");
		cell = WorkTimeSheetRow.createCell(c++);
		setCellVaue(cell,w.getPrjPhase());
		cell.setCellStyle(styles.get("cellString"));
		//("项目标准");
		cell = WorkTimeSheetRow.createCell(c++);
		setCellVaue(cell,prj!=null?prj.getStandard():null);
		cell.setCellStyle(styles.get("cellString"));
		//("项目规模");
		cell = WorkTimeSheetRow.createCell(c++);
		setCellVaue(cell,prj!=null?prj.getScale():null);
		cell.setCellStyle(styles.get("cellString"));
		//("项目类型");
		cell = WorkTimeSheetRow.createCell(c++);
		setCellVaue(cell,prj!=null?prj.getType():null);
		cell.setCellStyle(styles.get("cellString"));
		//("本人任职");
		cell = WorkTimeSheetRow.createCell(c++);
		setCellVaue(cell,w.getPrjPosition());
		cell.setCellStyle(styles.get("cellString"));
		//("本月起始日期");
		Cell csd = WorkTimeSheetRow.createCell(c++);
		csd.setCellStyle(styles.get("date"));
		csd.setCellValue(w.getStartDate());
		//("本月结束日期");
		Cell ced = WorkTimeSheetRow.createCell(c++);
		ced.setCellStyle(styles.get("date"));
		ced.setCellValue(w.getEndDate());
		//("工效认定");
		cell = WorkTimeSheetRow.createCell(c++);
		setCellVaue(cell,w.getWorkComfirm());
		cell.setCellStyle(styles.get("cellString"));
		//("本项工作积点");
		cell = WorkTimeSheetRow.createCell(c++);
		setCellVaue(cell,w.getPoints());
		cell.setCellStyle(styles.get("cellString"));
		
	}
	
	private void setCellVaue(Cell cell,Object val) {
		if(val == null) {
			return;
		}
		if(val instanceof String) {
			cell.setCellValue((String)val);
		}else if(val instanceof Double) {
			cell.setCellValue((Double)val);
		}else if(val instanceof Integer) {
			cell.setCellValue((Integer)val);
		}else if(val instanceof Long) {
			cell.setCellValue((Long)val);
		}else if(val instanceof Date) {
			cell.setCellValue((Date)val);
		}else {
			cell.setCellValue((String)val);
		}

	}





	private void createWorkTimeSheetTitleCells(Row workTimeSheetTitleRow, Map<String, CellStyle> styles) {
		int c = 1;
		Cell cell;
		cell = workTimeSheetTitleRow.createCell(c++);
		cell.setCellStyle(styles.get("worksheetHeader"));
		cell.setCellValue("项目编号");
		cell = workTimeSheetTitleRow.createCell(c++);
		cell.setCellStyle(styles.get("worksheetHeader"));
		cell.setCellValue("项目名称");
		cell = workTimeSheetTitleRow.createCell(c++);
		cell.setCellStyle(styles.get("worksheetHeader"));
		cell.setCellValue("项目阶段");
		cell = workTimeSheetTitleRow.createCell(c++);
		cell.setCellStyle(styles.get("worksheetHeader"));
		cell.setCellValue("项目标准");
		cell = workTimeSheetTitleRow.createCell(c++);
		cell.setCellStyle(styles.get("worksheetHeader"));
		cell.setCellValue("项目规模");
		cell = workTimeSheetTitleRow.createCell(c++);
		cell.setCellStyle(styles.get("worksheetHeader"));
		cell.setCellValue("项目类型");
		cell = workTimeSheetTitleRow.createCell(c++);
		cell.setCellStyle(styles.get("worksheetHeader"));
		cell.setCellValue("本人任职");
		cell = workTimeSheetTitleRow.createCell(c++);
		cell.setCellStyle(styles.get("worksheetHeader"));
		cell.setCellValue("本月起始日期");
		cell = workTimeSheetTitleRow.createCell(c++);
		cell.setCellStyle(styles.get("worksheetHeader"));
		cell.setCellValue("本月结束日期");
		cell = workTimeSheetTitleRow.createCell(c++);
		cell.setCellStyle(styles.get("worksheetHeader"));
		cell.setCellValue("工效认定");
		cell = workTimeSheetTitleRow.createCell(c++);
		cell.setCellStyle(styles.get("worksheetHeader"));
		cell.setCellValue("本项工作积点");

	}


	private void createStaffTitle(Row staffTitleRow,Map<String, CellStyle> styles) {
		int c = 1;
		Cell cell;
		cell = staffTitleRow.createCell(c++);
		cell.setCellValue("序号");
		cell.setCellStyle(styles.get("staffTitle"));

		cell = staffTitleRow.createCell(c++);
		cell.setCellValue("工号");
		cell.setCellStyle(styles.get("staffTitle"));

		cell = staffTitleRow.createCell(c++);
		cell.setCellValue("姓名");
		cell.setCellStyle(styles.get("staffTitle"));

		cell = staffTitleRow.createCell(c++);
		cell.setCellValue("性别");
		cell.setCellStyle(styles.get("staffTitle"));

		cell = staffTitleRow.createCell(c++);
		cell.setCellValue("年龄");
		cell.setCellStyle(styles.get("staffTitle"));

		cell = staffTitleRow.createCell(c++);
		cell.setCellValue("职务");
		cell.setCellStyle(styles.get("staffTitle"));

		cell = staffTitleRow.createCell(c++);
		cell.setCellValue("职称");
		cell.setCellStyle(styles.get("staffTitle"));

		cell = staffTitleRow.createCell(c++);
		cell.setCellValue("岗位层级");
		cell.setCellStyle(styles.get("staffTitle"));

		cell = staffTitleRow.createCell(c++);
		cell.setCellValue("本月工时识别率");
		cell.setCellStyle(styles.get("staffTitle"));

		cell = staffTitleRow.createCell(c++);
		cell.setCellValue("本月工时填报率");
		cell.setCellStyle(styles.get("staffTitle"));

		cell = staffTitleRow.createCell(c++);
		cell.setCellValue("本月总积点");
		cell.setCellStyle(styles.get("staffTitle"));

		cell = staffTitleRow.createCell(c++);
		cell.setCellValue("本月室排名");
		cell.setCellStyle(styles.get("staffTitle"));
	}


	private void createStaffCells(StaffMonthStatistics s, Person p, Row staffValueRow, Map<String, CellStyle> styles) {
		int c = 1;
		Cell cell;
		 
		cell = staffValueRow.createCell(c++);
		setCellVaue(cell,s.getRanking());
		cell.setCellStyle(styles.get("cellStringStaff"));

		cell = staffValueRow.createCell(c++);
		setCellVaue(cell,p.getUsername());
		cell.setCellStyle(styles.get("cellStringStaff"));

		cell = staffValueRow.createCell(c++);
		setCellVaue(cell,p.getStaffName());
		cell.setCellStyle(styles.get("cellStringStaff"));

		cell = staffValueRow.createCell(c++);
		setCellVaue(cell,p.gender());
		cell.setCellStyle(styles.get("cellStringStaff"));

		cell = staffValueRow.createCell(c++);
		setCellVaue(cell,p.age());
		cell.setCellStyle(styles.get("cellStringStaff"));

		cell = staffValueRow.createCell(c++);
		setCellVaue(cell,CodeUtil.getJobTitle(p.getJobTitle()));
		cell.setCellStyle(styles.get("cellStringStaff"));

		cell = staffValueRow.createCell(c++);
		setCellVaue(cell,CodeUtil.getPosition(p.getPosition()));
		cell.setCellStyle(styles.get("cellStringStaff"));

		cell = staffValueRow.createCell(c++);
		setCellVaue(cell,String.valueOf(p.getLevel()));
		cell.setCellStyle(styles.get("cellStringStaff"));

		cell = staffValueRow.createCell(c++);
		setCellVaue(cell,s.getMonthOccurRate());
		cell.setCellStyle(styles.get("cellStringStaff"));

		cell = staffValueRow.createCell(c++);
		setCellVaue(cell,s.getMonthOccurRate());
		cell.setCellStyle(styles.get("cellStringStaff"));

		cell = staffValueRow.createCell(c++);
		setCellVaue(cell,s.getSumOfPoints());
		cell.setCellStyle(styles.get("cellStringStaff"));

		cell = staffValueRow.createCell(c++);
		setCellVaue(cell,s.getRanking()+"/"+s.getCount());
		cell.setCellStyle(styles.get("cellStringStaff"));
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
