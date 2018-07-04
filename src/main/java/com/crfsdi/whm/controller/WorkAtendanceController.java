package com.crfsdi.whm.controller;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crfsdi.whm.model.Project;
import com.crfsdi.whm.service.ExcelImExportService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/wts")
public class WorkAtendanceController {
    @Autowired
	private ExcelImExportService excelImExportService;
	
    @PostMapping("/list")
    public void list(@RequestBody Project prj) {

    }
    @PostMapping("/update")
    public void updatPproject(@RequestBody Project prj) {

    }
    
    @PostMapping("/del")
    public void deletPproject() {

    }
    
    @PostMapping("/comfirm")
    public void deletPproject(@RequestBody List<Project> prjs) {

    }
    
}