package lc.listner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * @author liuchaoOvO on 2019/4/22
 * @description 项目启动后打开1个页面
 *//*

@Component
public class InitThreadLoadPage implements Runnable
{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public void run() {
        startStreamTask();
    }
    */
/**
     * 项目启动后打开1个页面
     *//*

    public void startStreamTask() {
        logger.info("开始启动1个启动页面");
        try {
            Runtime.getRuntime().exec("cmd   /c   start   http://localhost:12580/");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
*/
