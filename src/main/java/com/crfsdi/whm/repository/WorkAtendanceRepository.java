package com.crfsdi.whm.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.crfsdi.whm.model.WorkAtendance;
@Mapper
public interface WorkAtendanceRepository extends BaseRepository<WorkAtendance>{
	List<WorkAtendance> listByPage(String staffId, Long month, Long page, Long size);
}