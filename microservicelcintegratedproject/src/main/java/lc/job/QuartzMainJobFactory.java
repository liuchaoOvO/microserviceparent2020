package lc.job;


import lc.entity.QuartzTaskInformations;
import lc.entity.QuartzTaskRecords;
import lc.service.impl.QuartzServiceImpl;
import lc.service.quartz.QuartzService;
import lc.util.ApplicationContextHolder;
import lc.util.CommonUtil;
import lc.util.HttpClientUtil;
import lc.util.ResultEnum;
import org.apache.commons.lang.StringUtils;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName    QuartzMainJobFactory
 * @Description 定时任务的主要执行逻辑，实现Job接口
 * @Author       liuchaoOvO
 * @Date         2019/1/7
 * Version       1.0
 */
//代理job
@DisallowConcurrentExecution
public class QuartzMainJobFactory implements Job {

    private static Logger logger = LoggerFactory.getLogger(QuartzMainJobFactory.class);

    private AtomicInteger atomicInteger;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        atomicInteger = new AtomicInteger(0);
        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        String id = jobDataMap.getString("id");
        String taskNo = jobDataMap.getString("taskNo");
        String executorNo = jobDataMap.getString("executorNo");
        String sendType = jobDataMap.getString("sendType");
        String url = jobDataMap.getString("url");
        String executeParameter = jobDataMap.getString("executeParameter");
        logger.info("定时任务被执行:taskNo={},executorNo={},sendType={},url={},executeParameter={}", taskNo, executorNo, sendType, url, executeParameter);
        QuartzService quartzService = (QuartzServiceImpl) ApplicationContextHolder.getBean("quartzServiceImpl");
        QuartzTaskRecords records = null;
        try {
            //保存定时任务的执行记录
            records = quartzService.addTaskRecords(taskNo);
            if (null == records || !ResultEnum.INIT.name().equals(records.getTaskstatus())) {
                logger.info("taskNo={}保存执行记录失败", taskNo);
                return;
            }
            //http 请求方式 调用
            if (ResultEnum.HTTP.getMessage().equals(sendType)) {
                try {
                    String result = HttpClientUtil.doPost(url, "text/json", executeParameter);
                    logger.info("taskNo={},sendtype={}执行结果result{}", taskNo, sendType, result);
                    if (StringUtils.isEmpty(result)) {
                        throw new RuntimeException("taskNo=" + taskNo + "http方式返回null");
                    }
                } catch (Exception ex) {
                    logger.error("");
                    throw ex;
                }
            }
            //kafka 请求方式调用（暂时未实现） 后续可拓展调整成其他mq 方式
            else if (ResultEnum.KAFKA.getMessage().equals(sendType)) {
                try {
                    String message = new StringBuffer(taskNo).append(":").append(id).append(":").append(executeParameter).toString();
                } catch (Exception ex) {
                    logger.error("");
                    throw ex;
                }
            }
        } catch (Exception ex) {
            logger.error("");
            atomicInteger.incrementAndGet();
            quartzService.addTaskErrorRecord(records.getId().toString(), taskNo + ":" + ex.getMessage(), CommonUtil.getExceptionDetail(ex));
        }
        quartzService.updateRecordById(atomicInteger.get(), records.getId());
        QuartzTaskInformations quartzTaskInformation = new QuartzTaskInformations();
        quartzTaskInformation.setId(Long.parseLong(id));
        quartzTaskInformation.setLastmodifytime(System.currentTimeMillis());
        quartzService.updateTask(quartzTaskInformation);
    }
}
