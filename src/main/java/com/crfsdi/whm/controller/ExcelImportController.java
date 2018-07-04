package com.crfsdi.whm.controller;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crfsdi.whm.service.ExcelImExportService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/imp")
public class ExcelImportController {
    @Autowired
	private ExcelImExportService excelImExportService;
	


    @PostMapping("/prj")
    public void project(@RequestParam String month) {
        log.info("importing porject... month:{}", month);
        File infile = null;
		excelImExportService.importProjects(infile , month);
    }
    
    @PostMapping("/staff")
    public void staff() {
        log.info("importing staff... ");
        File infile = null;
		excelImExportService.importStaff(infile);
    }
    
    @PostMapping("/wa")
    public void workAtendance(@RequestParam String month) {
        log.info("importing work atenddance... month:{}", month);
        File infile = null;
		excelImExportService.importWorkAtendanceByMonth(infile, month);
    }
}