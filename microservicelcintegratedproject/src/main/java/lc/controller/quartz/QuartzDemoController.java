package lc.controller.quartz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * @ClassName IndexController
 * @Description 首页跳转controller
 * @Author  liuchaoOvO
 * @Date    2019/1/4
 * Version  1.0
 */
@Controller
public class QuartzDemoController
{
    private static final Logger logger = LoggerFactory.getLogger(QuartzDemoController.class);

    @RequestMapping(value = "/testquarzt", method = RequestMethod.POST)
    @ResponseBody
    public String listTasks() {
        try {
            logger.info("quartz demo test  secucess ====post==/testquarzt");
        } catch (Exception e) {
            logger.error("首页跳转发生异常exceptions-->" + e.toString());
        }
        return "haha  成功测试了 自动任务 请求为/testquarzt";
    }

}
