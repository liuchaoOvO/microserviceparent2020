package lc.controller;

import lc.utils.IniUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liuchaoOvO  2019/3/22 下午10:11
 *
 */
@RestController
public class ConfigController {
    /**
     * 注意观察：
     * 第一次请求test后，修改配置文件值
     * 10s过后，再次请求，将会看到打印
     * 出最新值
     */
    @RequestMapping (value = "/test", method = RequestMethod.GET)
    public void test() {
        System.out.println("---------" + IniUtil.getIniValue("jdbc") + "-------");
    }

}
