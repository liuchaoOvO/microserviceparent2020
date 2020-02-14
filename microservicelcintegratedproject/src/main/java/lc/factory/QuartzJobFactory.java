package lc.factory;

import lc.controller.MultiResolverType.MultiResolverTypeContrller;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author liuchaoOvO on 2019/4/18
 */
public class QuartzJobFactory implements Job
{
    private static final Logger logger = LoggerFactory.getLogger(MultiResolverTypeContrller.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException
    {
        //TODO 这里是定时任务逻辑
        logger.info("=================我是定时任务,每隔5秒执行一次==============");
    }


}
