package lc.service.quartz;

import lc.util.ApplicationContextHolder;
import lc.util.RedisUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;


public class AutoJobJobListener implements JobListener {
    private static final Logger logger = LoggerFactory.getLogger(AutoJobJobListener.class);
    String name = null;
    String groupName = null;

    /**
     * @return groupName
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * @param groupName 要设置的 groupName
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的 name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 实现“即将执行的响应事件”
     *
     * @param jobexecutioncontext JobExecutionContext 上下文环境
     */
    public void jobToBeExecuted(JobExecutionContext jobexecutioncontext) {
        System.out.println("====AutoJobJobListener 监听器：AutoJobJobListener.jobToBeExecuted()====");
        Long taskId = Long.parseLong(jobexecutioncontext.getJobDetail().getKey().getName());
        try {
            RedisUtil redisUtil = (RedisUtil) ApplicationContextHolder.getBean("redisUtil");
            String preKey = "sys:AutoTask:" + taskId.toString();
            String hkey = preKey + ":RunID";
            boolean hkeyExist = redisUtil.exists(hkey);
            if (hkeyExist) {
                //产生执行令牌
                String newToken_id = "{" + UUID.randomUUID().toString() + "}".toUpperCase();
                // 使其控制在集群环境下，只有一个生产机在执行
                jobexecutioncontext.put("TOKEN_ID", newToken_id);
            } else {
                throw new Exception("任务" + taskId + "已在执行中");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重载“自动任务执行完成事件”
     *
     * @param context JobExecutionContext 上下文环境
     * @param ex      JobExecutionException 自动任务异常抛出
     */
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException ex) {
        logger.info("===AutoJobJobListener.jobWasExecuted===");
        // 获取job的名字
        Long taskId = Long.parseLong(context.getJobDetail().getKey().getName());
        String preKey = "sys:AutoTask:" + taskId.toString();
        String hkey = preKey + ":RunID";
        RedisUtil redisUtil = (RedisUtil) ApplicationContextHolder.getBean("redisUtil");
        boolean hkeyExist = redisUtil.exists(hkey);
        if (hkeyExist) {
            Object a = context.get("TOKEN_ID");
            if ("".equals(a) || a == null) {
                return;
            }
        }
        try {
            if (ex != null) {
                logger.info("===AutoJobJobListener.jobWasExecuted===错误执行的异常信息为：" + ex.getMessage());
            } else {
                logger.info("===AutoJobJobListener执行正常===");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            removeEnableExecuteJob(taskId);
        }
    }

    /**
     * 实现“执行被拒绝的响应事件”
     * 不做任何操作
     *
     * @param jobexecutioncontext JobExecutionContext 上下文环境
     */
    public void jobExecutionVetoed(JobExecutionContext jobexecutioncontext) {
        logger.info("===AutoJobJobListener 监听器：AutoJobJobListener.jobExecutionVetoed()===该任务:" + jobexecutioncontext.getJobDetail().getKey().getName() + "====");
    }


    private void removeEnableExecuteJob(Long taskId) {
        RedisUtil redisUtil = (RedisUtil) ApplicationContextHolder.getBean("redisUtil");
        String preKey = "sys:AutoTask:" + taskId.toString();
        String hkey = preKey + ":RunID";
        redisUtil.remove(hkey);
    }
}