package com.crfsdi.whm.service;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.crfsdi.whm.WhmApplication;
@RunWith(SpringRunner.class)
@SpringBootTest(classes=WhmApplication.class)
public class ExcelImExportServiceTest {
	
    @Autowired
	private ExcelImExportService excelImExportService;

	@Test
	public void testImportWorkAtendanceByMonth() {
		File infile = new File("D:\\wh_sy\\exceltest\\wa.xlsx");
		excelImExportService.importWorkAtendanceByMonth(infile, 201807L);
	}

	@Test
	public void testImportStaff() {
		File infile = new File("D:\\wh_sy\\exceltest\\staff.xlsx");
		excelImExportService.importStaff(infile);
	}

	@Test
	public void testImportProjects() {
		File infile = new File("D:\\wh_sy\\exceltest\\projects.xlsx");
		excelImExportService.importProjects(infile, 201807L);
	}

	@Test
	public void testExportReportByYear() {
		File infile = new File("E:\\ws_szs\\exceltest\\report.xlsx");
		excelImExportService.exportReportByYear(infile, 2018);
	}

}
