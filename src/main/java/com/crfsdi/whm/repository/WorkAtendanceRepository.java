package com.crfsdi.whm.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.crfsdi.whm.model.WorkAtendance;
@Mapper
public interface WorkAtendanceRepository extends BaseRepository<WorkAtendance>{
	List<WorkAtendance> listByPage(@Param("staffId") String staffId, @Param("month") Long month, @Param("page") Long page, @Param("size") Long size);
}