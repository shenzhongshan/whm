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
		excelImExportService.importWorkAtendanceByMonth(infile, "201807");
	}

	@Test
	public void testImportStaff() {
		File infile = new File("D:\\wh_sy\\exceltest\\staff.xlsx");
		excelImExportService.importStaff(infile);
	}

	@Test
	public void testImportProjects() {
		File infile = new File("D:\\wh_sy\\exceltest\\projects.xlsx");
		excelImExportService.importProjects(infile, "201807");
	}

	@Test
	public void testExportReportByYear() {
		fail("Not yet implemented");
	}

}
