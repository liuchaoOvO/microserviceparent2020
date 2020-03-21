package lc.dynamicupdateporperties.controller;

import lc.dynamicupdateporperties.service.DynamicUpdateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liuchaoOvO  2019/3/22 下午10:11
 */
@Slf4j
@RestController
public class DynamicUpdateController {
    /**
     * 注意观察：
     * 第一次请求test后，修改配置文件值
     * 10s过后，再次请求，将会看到打印
     * 出最新值
     */
    @Autowired
    DynamicUpdateService dynamicUpdateService;

    @RequestMapping (value = "/testDynamicUpdate", method = RequestMethod.GET)
    public void testDynamicUpdate() {
        dynamicUpdateService.minor();
        log.debug("DynamicUpdateController.testDynamicUpdate->result:{}", dynamicUpdateService.getIniValue("jdbc"));
    }

}
