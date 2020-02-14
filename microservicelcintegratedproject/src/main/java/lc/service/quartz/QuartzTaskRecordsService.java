package lc.service.quartz;


import lc.entity.QuartzTaskRecords;

import java.util.List;

public interface QuartzTaskRecordsService {

    long addTaskRecords(QuartzTaskRecords quartzTaskRecords);

    Integer updateTaskRecords(QuartzTaskRecords quartzTaskRecords);

    List<QuartzTaskRecords> listTaskRecordsByTaskNo(String taskNo);

}
