package com.crfsdi.whm.repository;

import com.crfsdi.whm.model.Project;

public interface ProjectRepository extends BaseRepository<Project>{
    Project findByPrjId(Long prjId);
}