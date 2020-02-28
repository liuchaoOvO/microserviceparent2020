package lc.controller.opLog;

import lc.util.OpLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liuchaoOvO on 2019/4/11
 * @description OpLog Controller  验证@OpLog 注解切面功能
 */
@RestController
public class OpLogController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @OpLog (system = "系统", module = "平台模块", menu = "菜单名称", function = "opLog", content = "'主要内容：--' + #arg0.getRequestURL()")
    @RequestMapping (value = "/opLog", method = RequestMethod.GET)
    public boolean opLog() {
        logger.info("===opLog()====");
        return true;
    }
}
