package com.crfsdi.whm.repository;

import org.apache.ibatis.annotations.Mapper;

import com.crfsdi.whm.model.Project;
@Mapper
public interface ProjectRepository extends BaseRepository<Project>{
    Project findByPrjId(Long prjId);
}