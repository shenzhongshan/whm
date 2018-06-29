package com.crfsdi.whm.repository;

import java.util.List;

import com.crfsdi.whm.model.Project;

public interface ProjectRepository extends BaseRepository<Project>{
    Project findByPrjId(Long prjId);
    void savePreMore(List<Project> objs);
    void savePre(Project obj);
    void updatePre(Project obj);
}