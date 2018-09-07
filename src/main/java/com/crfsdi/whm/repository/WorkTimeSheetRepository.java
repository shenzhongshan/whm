package com.crfsdi.whm.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.crfsdi.whm.model.StaffMonthStatistics;
import com.crfsdi.whm.model.WorkTimeSheet;
@Mapper
public interface WorkTimeSheetRepository extends BaseRepository<WorkTimeSheet>{
	List<WorkTimeSheet> listByPage(@Param("staffId") String staffId, @Param("month") Long month, @Param("page")Long page, @Param("size") Long size);

	void confirm(@Param("month") Long month, @Param("staffId") String staffId);

	void submit(@Param("month") Long month, @Param("staffId") String staffId);
	
	List<StaffMonthStatistics> listStaffMonthStatistics(@Param("month") Long month, @Param("staffId")String staffId);
}