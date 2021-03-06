package com.crfsdi.whm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crfsdi.whm.model.Project;
import com.crfsdi.whm.repository.ProjectRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/prj")
public class ProjectsController {
    @Autowired
	private ProjectRepository repo;
	

    @RequestMapping("/{id}")
    public Project load(@PathVariable("id") Long id) {
    	log.info("get project, id: {}", id);
    	return repo.load(id);
    }
    
    @PostMapping("/save")
    @PreAuthorize("'admin'==authentication.principal or hasRole('ADMIN')")
    public Project save(@RequestBody Project prj) {
    	log.info("save project: {}",prj);
    	if(prj.getId() != null) {
        	repo.update(prj);
    	}else {
        	repo.save(prj);
    	}
    	return prj;
    }
    
    @PostMapping("/add")
    @PreAuthorize("'admin'==authentication.principal or hasRole('ADMIN')")
    public Project add(@RequestBody Project prj) {
    	log.info("add project: {}",prj);
    	repo.save(prj);
    	return prj;
    }
    
    @PostMapping("/update")
    @PreAuthorize("'admin'==authentication.principal or hasRole('ADMIN')")
    public void update(@RequestBody Project prj) {
    	log.info("update project: {}",prj);
    	repo.update(prj);
    }
    
    @RequestMapping("/del/{id}")
    @PreAuthorize("'admin'==authentication.principal or hasRole('ADMIN')")
    public void delete(@PathVariable("id") Long id) {
    	log.info("delete project, id: {}", id);
    	repo.delete(id);
    } 
    
    @RequestMapping("/list/{month}")
    public List<Project> list(@PathVariable("month") Long month) {
    	log.info("list project by month, month: {}", month);
    	List<Project> prjs = repo.listByMonth(month);
    	return prjs;
    }
    
    @RequestMapping("/listAvailable/{month}")
    public List<Project> listAvailable(@PathVariable("month") Long month) {
    	log.info("list available project by month, month: {}", month);
    	List<Project> prjs = repo.listByMonth(month,1L);
    	return prjs;
    }
    
    @PostMapping("/confirm/{month}")
    @PreAuthorize("'admin'==authentication.principal or hasRole('ADMIN')")
    public void confirm(@PathVariable("month") Long month) {
    	log.info("comfirm project by month, month: {}", month);
    	repo.confirmByMonth(month);
    }
    
}