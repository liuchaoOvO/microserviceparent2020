package lc.service.quartz;


import lc.entity.QuartzTaskErrors;

public interface QuartzTaskErrorsService {
    Integer addTaskErrorRecord(QuartzTaskErrors quartzTaskErrors);

    QuartzTaskErrors detailTaskErrors(String recordId);
}
