package lc.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liuchaoOvO on 2019/2/28
 */
@RefreshScope //开启更新功能
@RestController
public class HelloController {

    @Value ("${parameter}")
    private String profile;

    @GetMapping ("/info")
    public String hello() {
        return profile;
    }
}