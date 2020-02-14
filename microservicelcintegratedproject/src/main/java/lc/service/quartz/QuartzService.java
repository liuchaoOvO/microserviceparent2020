package lc.service.quartz;



import lc.dto.QuartzTaskRecordsVo;
import lc.entity.QuartzTaskErrors;
import lc.entity.QuartzTaskInformations;
import lc.entity.QuartzTaskRecords;
import org.quartz.SchedulerException;

import java.util.List;

public interface QuartzService {
    String addTask(QuartzTaskInformations quartzTaskInformations);

    List<QuartzTaskInformations> getTaskList(String taskNo, String currentPage);

    QuartzTaskInformations getTaskById(String id);

    String updateTask(QuartzTaskInformations quartzTaskInformations);

    String startJob(String taskNo) throws Exception;

    void initLoadOnlineTasks() throws Exception;


    QuartzTaskRecords addTaskRecords(String taskNo);

    Integer updateRecordById(Integer count, Long id);

    Integer updateModifyTimeById(QuartzTaskInformations quartzTaskInformations);

    Integer addTaskErrorRecord(String id, String errorKey, String errorValue);

    List<QuartzTaskRecordsVo> taskRecords(String taskNo);

    String runTaskRightNow(String taskNo);

    QuartzTaskErrors detailTaskErrors(String recordId);

    Integer getTaskCountNum();
}
