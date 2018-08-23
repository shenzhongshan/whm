package com.crfsdi.whm.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.crfsdi.whm.model.Project;
@Mapper
public interface ProjectRepository extends BaseRepository<Project>{
    Project findByPrjId(@Param(value = "prjId") Long prjId);

	List<Project> listByMonth(@Param(value = "month") Long month);
	List<Project> listByMonth(@Param(value = "month") Long month, @Param(value = "status")Long status);

	void confirmByMonth(@Param(value = "month") Long month);

}