package com.crfsdi.whm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
    
    @PostMapping("/add")
    public Project add(@RequestBody Project prj) {
    	log.info("add project: {}",prj);
    	repo.save(prj);
    	return prj;
    }
    
    @PostMapping("/update")
    public void update(@RequestBody Project prj) {
    	log.info("update project: {}",prj);
    	repo.update(prj);
    }
    
    @RequestMapping("/del/{id}")
    public void delete(@PathVariable("id") Long id) {
    	log.info("delete project, id: {}", id);
    	repo.delete(id);
    }
    
    @RequestMapping("/list/{month}")
    public List<Project> list(@PathVariable("month") Long month) {
    	log.info("list project by month, month: {}", month);
    	return null;
    }
    
    @PostMapping("/comfirm/{month}")
    public void comfirm(@PathVariable("month") Long month) {

    }
    
}