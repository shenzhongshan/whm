package com.crfsdi.whm.repository;

import java.util.List;

import com.crfsdi.whm.model.WorkTimeSheet;

public interface WorkTimeSheetRepository extends BaseRepository<WorkTimeSheet>{
    void savePreMore(List<WorkTimeSheet> objs);
    void savePre(WorkTimeSheet obj);
    void updatePre(WorkTimeSheet obj);
}