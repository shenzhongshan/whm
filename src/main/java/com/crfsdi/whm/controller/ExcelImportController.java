package com.crfsdi.whm.controller;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.crfsdi.whm.service.ExcelImExportService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/imp")
public class ExcelImportController {
    @Autowired
	private ExcelImExportService excelImExportService;
	


    @PostMapping("/prj")
    public void project(@RequestParam String month, @RequestParam("file") MultipartFile file) {
        log.info("importing porject... month:{}, OriginalFilename:{}, ContentType:{} ", month,file.getOriginalFilename(), file.getContentType());
        File infile = null;
        try {
            infile = createFile("porjects-" + month, file.getOriginalFilename());
			file.transferTo(infile);
			excelImExportService.importProjects(infile , month);
		} catch (IllegalStateException | IOException e) {
			log.warn("Import project error!", e);
		}finally {
			if(infile.exists()) {
				try {
					infile.delete();
				}catch(Exception e) {
					//
				}
			}
		}
    }
    
    private File createFile(String prefix, String name) throws IOException {
    	final File tempFile = File.createTempFile(prefix, name);
    	log.info("临时文件本地路径：{}" ,tempFile.getCanonicalPath());
		return tempFile;
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