package com.crfsdi.whm.controller;

import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crfsdi.whm.model.Person;
import com.crfsdi.whm.model.StaffMonthStatistics;
import com.crfsdi.whm.model.WorkTimeSheet;
import com.crfsdi.whm.repository.WorkTimeSheetRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/wts")
public class WorkTimeSheetController {
	
	@Autowired
    private WorkTimeSheetRepository wtsRepo;
	  
    @RequestMapping("/list/{page},{size}")
    public List<WorkTimeSheet> list(@RequestBody WorkTimeSheet wts,@PathVariable("page") Long page, @PathVariable("size") Long size) {
    	log.info("list Work Timesheets, page:{},page size:{},staffId:{},month:{}",page,size,wts.getStaffId(),wts.getMonth());
    	String staffId = wts.getStaffId();
    	return wtsRepo.listByPage(staffId!=null?staffId:"",wts.getMonth(),page,size);
    }
    
    @RequestMapping("/listByCurrentUser/{month}")
    public List<WorkTimeSheet> list(@PathVariable("month") Long month) {
    	log.info("list Work Timesheets by current user, month:{}", month);
    	String staffId = Person.currentUsername();
    	return wtsRepo.listByPage(staffId, month, 0L, 1000L);
    }
    
    @RequestMapping("/get/{id}")
    public WorkTimeSheet load(@PathVariable("id") Long id) {
    	log.info("get Work Timesheet, id: {}", id);
    	return wtsRepo.load(id);
    }
    
    @PostMapping("/add")
    public WorkTimeSheet add(@RequestBody WorkTimeSheet wts) {
    	log.info("add Work Timesheet: {}", wts);
    	String staffId = Person.currentUsername();
    	if(StringUtils.isEmpty(wts.getStaffId())) {
    		wts.setStaffId(staffId);
    	}
    	wtsRepo.save(wts);
    	return wts;
    }
    
    @PostMapping("/update")
    public void update(@RequestBody WorkTimeSheet wts) {
    	log.info("update Work Timesheet: {}",wts);
    	wtsRepo.update(wts);
    }
    
    @PostMapping("/save")
    public WorkTimeSheet save(@RequestBody WorkTimeSheet wts) {
    	log.info("save Work Timesheet: {}", wts);
    	if(wts.getId()!= null) {
        	wtsRepo.update(wts);
    	}else {
        	String staffId = Person.currentUsername();
        	if(StringUtils.isEmpty(wts.getStaffId())) {
        		wts.setStaffId(staffId);
        	}
        	wtsRepo.save(wts);
    	}
    	return wts;
    }
    
    @RequestMapping("/del/{id}")
    public void delete(@PathVariable("id") Long id) {
    	log.info("delete Work Timesheet, id: {}", id);
    	wtsRepo.delete(id);
    }
    
    @RequestMapping("/comfirm/{month},{staffId}")
    public void comfirm(@PathVariable("month") Long month,@PathVariable("staffId") String  staffId) {
    	log.info("comfirm Work Timesheet, month: {}, staff id:{}", month, staffId);
    	wtsRepo.confirm(month, staffId);
    }
    
    @RequestMapping("/submit/{month}")
    public void submit(@PathVariable("month") Long month) {
    	String  staffId = Person.currentUsername();
		log.info("submit Work Timesheet, month: {}, staff id:{}", month, staffId);
    	wtsRepo.submit(month,staffId);
    }
    
    @RequestMapping("/report/{month},{staffId}")
    @PreAuthorize("'admin'==authentication.principal or hasRole('ADMIN')")
    public List<StaffMonthStatistics> report(@PathVariable("month") Long month,@PathVariable("staffId") String  staffId) {
    	log.info("report Work Timesheet, month: {}, staff id:{}", month, staffId);
    	return wtsRepo.listStaffMonthStatistics(month, staffId);
    }
    
    @RequestMapping("/eval/{id},{wrate}")
    @PreAuthorize("'admin'==authentication.principal or hasRole('ADMIN')")
    public Double report(@PathVariable("id") Long  id, @PathVariable("wrate") Double wrate) {
    	log.info("eval Work Timesheet, id: {}, wrate :{}", id, wrate);
    	WorkTimeSheet wst = wtsRepo.load(id);
    	wst.setCf(wrate*1.0D);
    	return wst.calcPoints();
    }  
}