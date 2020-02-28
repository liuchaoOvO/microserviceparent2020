package lc.service.quartz;

import lc.util.ApplicationContextHolder;
import lc.util.RedisUtil;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author liuchaoOvO on 2019/8/29
 */

public class AutoJobTriggerListener implements TriggerListener {
    private static final Logger logger = LoggerFactory.getLogger(AutoJobTriggerListener.class);


    @Override
    public String getName() {
        return "autoJobTriggerListener";
    }

    /**
     * (1)
     * Trigger被激发 它关联的job即将被运行
     * Called by the Scheduler when a Trigger has fired, and it's associated JobDetail is about to be executed.
     */
    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext jobExecutionContext) {

    }

    /**
     * (2)
     * Trigger被激发 它关联的job即将被运行,先执行(1)，在执行(2) 如果返回TRUE 那么任务job会被终止
     * Called by the Scheduler when a Trigger has fired, and it's associated JobDetail is about to be executed
     */
    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext jobExecutionContext) {
        // 获取job的名字  autotask_id
        Long taskId = Long.parseLong(jobExecutionContext.getJobDetail().getKey().getName());
        // 集群 分布式 开关位置  enableExecuteJob()
        if (!enableExecuteJob(taskId)) {
            logger.info("===Trigger监听器：AutoJobTriggerListener.vetoJobExecution()===自动任务id为：" + taskId + "===");
            return true;
        }
        return false;
    }

    /**
     * (3) 当Trigger错过被激发时执行,比如当前时间有很多触发器都需要执行，但是线程池中的有效线程都在工作，
     * 那么有的触发器就有可能超时，错过这一轮的触发。
     * Called by the Scheduler when a Trigger has misfired.
     */

    @Override
    public void triggerMisfired(Trigger trigger) {
        logger.info("Trigger监听器：AutoJobTriggerListener.triggerMisfired()");
    }

    /**
     * (4) 任务完成时触发
     * Called by the Scheduler when a Trigger has fired, it's associated JobDetail has been executed
     * and it's triggered(xx) method has been called.
     */
    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext jobExecutionContext, Trigger.CompletedExecutionInstruction completedExecutionInstruction) {
        logger.info("===Trigger监听器：AutoJobTriggerListener.triggerComplete()===" + jobExecutionContext.getJobDetail().getKey().getName() + "自动任务任务触发完成===");
    }

    private boolean enableExecuteJob(Long taskId) {
        boolean flag = false;
        try {
            RedisUtil redisUtil = (RedisUtil) ApplicationContextHolder.getBean("redisUtil");
            String preKey = "sys:AutoTask:" + taskId.toString();
            String hkey = preKey + ":RunID";
            boolean bExist = redisUtil.exists(hkey);
            if (!bExist) {
                // 第一时间抢占锁，设置60秒存活时间(默认一个自动任务的执行时间小于60秒)
                boolean runLock = redisUtil.set(hkey, "");
                logger.info("自动任务runLock状态：" + runLock);
                if (runLock) {
                    flag = true;
                } else {
                    flag = false;
                }
            } else {
                // 已经存在正在执行的对应hkey的自动任务
                flag = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("==<<==自动任务id为：" + taskId + ",flag:" + flag + "==>>==");
        return flag;
    }
}
