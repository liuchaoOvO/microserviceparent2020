package lc.service.quartz;


import lc.entity.QuartzTaskInformations;

import java.util.List;

public interface QuartzTaskInformationsService {
    String insert(QuartzTaskInformations quartzTaskInformations);

    List<QuartzTaskInformations> selectList(String taskNo, String currentPage);

    QuartzTaskInformations getTaskById(String id);

    String updateTask(QuartzTaskInformations quartzTaskInformations);

    QuartzTaskInformations getTaskByTaskNo(String taskNo);

    Integer updateStatusById(QuartzTaskInformations quartzTaskInformations);

    List<QuartzTaskInformations> getUnnfrozenTasks(String status);

    Integer updateModifyTimeById(QuartzTaskInformations quartzTaskInformations);

    Integer getTaskCountNum();
}
