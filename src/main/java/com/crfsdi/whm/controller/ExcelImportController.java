package com.crfsdi.whm.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
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
    public void project(@RequestParam Long month, @RequestParam("file") MultipartFile file) {
        log.info("importing porject... month:{}, OriginalFilename:{}, ContentType:{} ", month,file.getOriginalFilename(), file.getContentType());
        File infile = null;
        try {
            infile = createFile("porjects-" + month, file.getOriginalFilename());
			file.transferTo(infile);
			excelImExportService.importProjects(infile , month);
		} catch (IllegalStateException | IOException e) {
			log.warn("Import project error!", e);
		}
    }
    
    private File createFile(String prefix, String name) throws IOException {
    	final File tempFile = File.createTempFile(prefix, name);
    	log.info("临时文件本地路径：{}" ,tempFile.getCanonicalPath());
		return tempFile;
    }
    
    @PostMapping("/staff")
    public void staff(@RequestParam("file") MultipartFile file) {
        log.info("importing staff... OriginalFilename:{}, ContentType:{} ", file.getOriginalFilename(), file.getContentType());
        File infile = null;
        try {
            infile = createFile("staff-", file.getOriginalFilename());
			file.transferTo(infile);
			excelImExportService.importStaff(infile);
		} catch (IllegalStateException | IOException e) {
			log.warn("Import staff error!", e);
		}

    }
    
    @PostMapping("/wa")
    public void workAtendance(@RequestParam Long month, @RequestParam("file") MultipartFile file) {
        log.info("importing work atenddance... month:{}, OriginalFilename:{}, ContentType:{} ", month,file.getOriginalFilename(), file.getContentType());
        File infile = null;
        try {
            infile = createFile("workAtenddance-" + month, file.getOriginalFilename());
			file.transferTo(infile);
			excelImExportService.importWorkAtendanceByMonth(infile, month);
		} catch (IllegalStateException | IOException e) {
			log.warn("Import work atenddance error!", e);
		}

    }
    
    @RequestMapping("/excelReport/{year}")
    public void exportExcelReport(@PathVariable("year") Integer year, HttpServletResponse response) {
    	log.info("exportExcelReport, year: {} ", year);
        // 告诉浏览器用什么软件可以打开此文件
        response.setHeader("content-Type", "application/vnd.ms-excel");
        // 下载文件的默认名称
		response.setHeader("Content-Disposition", "attachment;filename=wst-report-"+year+".xlsx");

        File infile = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
    		infile = createFile("wst-report-", year+".xlsx");
    		excelImExportService.exportReportByYear(infile, year);
            bis = new BufferedInputStream(new FileInputStream(infile));
            bos = new BufferedOutputStream( response.getOutputStream());
            byte[] buff = new byte[2048];
            int bytesRead;
            // Simple read/write loop.
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (final IOException e) {
        	log.error("exportExcelReport error, {}", e);
        	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
			try {
				if (infile != null)
					infile.deleteOnExit();
				if (bis != null)
					bis.close();
				if (bos != null)
					bos.close();
			} catch (IOException e) {
				log.error("exportExcelReport close file, {}", e);
			}
        }
    }
}