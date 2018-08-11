package com.crfsdi.whm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crfsdi.whm.model.WorkAtendance;
import com.crfsdi.whm.repository.WorkAtendanceRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/wa")
public class WorkAtendanceController {
	@Autowired
    private WorkAtendanceRepository waRepo;
	  
    @RequestMapping("/list/{page},{size}")
    public List<WorkAtendance> list(@RequestBody WorkAtendance wa,@PathVariable("page") Long page, @PathVariable("size") Long size) {
    	log.info("list Work Atendances, page:{},page size:{},staffId:{},month:{}",page,size,wa.getStaffId(),wa.getMonth());
    	String staffId = wa.getStaffId();
    	return waRepo.listByPage(staffId!=null?staffId:"",wa.getMonth(),page,size);
    }
    
    @RequestMapping("/get/{id}")
    public WorkAtendance load(@PathVariable("id") Long id) {
    	log.info("get Work Atendance, id: {}", id);
    	return waRepo.load(id);
    }
    
    @PostMapping("/add")
    public WorkAtendance add(@RequestBody WorkAtendance wa) {
    	log.info("add Work Atendance: {}", wa);
    	waRepo.save(wa);
    	return wa;
    }
    
    @PostMapping("/update")
    public void update(@RequestBody WorkAtendance wa) {
    	log.info("update Work Atendance: {}",wa);
    	waRepo.update(wa);
    }
    
	@PostMapping("/save")
	public WorkAtendance save(@RequestBody WorkAtendance wa) {
		log.info("save Work Atendance: {}", wa);
		if (wa.getId() != null) {
			waRepo.update(wa);
		} else {
			waRepo.save(wa);
		}
		return wa;
	}
    
    @RequestMapping("/del/{id}")
    public void delete(@PathVariable("id") Long id) {
    	log.info("delete Work Atendance, id: {}", id);
    	waRepo.delete(id);
    }
    
}