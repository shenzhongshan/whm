package com.crfsdi.whm.service;

import java.io.File;

public interface ExcelImExportService {
   void importWorkAtendanceByMonth(File infile, String month);
   void importStaff(File infile);
   void importProjects(File infile, String month);
   void exportReportByYear(File infile, int year);
   
}
