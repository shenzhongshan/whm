package com.crfsdi.whm.service;

import java.io.File;

public interface ExcelImExportService {
   void importWorkAtendanceByMonth(File infile, Long month);
   void importStaff(File infile);
   void importProjects(File infile, Long month);
   void exportReportByYear(File infile, Integer year);
   
}
