package com.crfsdi.whm.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.crfsdi.whm.model.WorkTimeSheet;
@Mapper
public interface WorkTimeSheetRepository extends BaseRepository<WorkTimeSheet>{
	List<WorkTimeSheet> listByPage(String staffId, Long month, Long page, Long size);
}