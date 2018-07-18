package com.crfsdi.whm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    
    @RequestMapping("/get/{id}")
    public WorkTimeSheet load(@PathVariable("id") Long id) {
    	log.info("get Work Timesheet, id: {}", id);
    	return wtsRepo.load(id);
    }
    
    @PostMapping("/add")
    public WorkTimeSheet add(@RequestBody WorkTimeSheet wts) {
    	log.info("add Work Timesheet: {}", wts);
    	wtsRepo.save(wts);
    	return wts;
    }
    
    @PostMapping("/update")
    public void update(@RequestBody WorkTimeSheet wts) {
    	log.info("update Work Timesheet: {}",wts);
    	wtsRepo.update(wts);
    }
    
    @RequestMapping("/del/{id}")
    public void delete(@PathVariable("id") Long id) {
    	log.info("delete Work Timesheet, id: {}", id);
    	wtsRepo.delete(id);
    }
    
    @PostMapping("/comfirm")
    public void comfirm(@RequestBody List<WorkTimeSheet> prjs) {

    }
    
}