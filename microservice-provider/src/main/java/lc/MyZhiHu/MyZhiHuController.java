package lc.MyZhiHu;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liuchaoOvO on 2019/4/11
 * @description My 知乎 Controller
 */
@Controller
@RequestMapping (value = "/myzhihu/api")
public class MyZhiHuController {
    private final static Logger logger = LoggerFactory.getLogger(MyZhiHuController.class);

    @Autowired
    private RestTemplate restTemplate;


    @GetMapping (value = "/index")
    public String index() {
        logger.debug("myzhihu=====index");
        return "/myzhihu/index";
    }

    @GetMapping (value = "/acceptAuthorizationCode")
    public String acceptAuthorizationCode(@RequestParam String code) {
        logger.debug("myzhihu=====acceptAuthorizationCode");
        String getAccessTokenByCodeUrl = "http://microservice-lcintegratedproject/oauth/token?grant_type=authorization_code&code=" + code + "&client_id=sa" +
                "&client_secret=1&redirect_uri=http://localhost:7001/myzhihu/api/acceptAuthorizationCode";
        Map request = new HashMap();
        JSONObject accessTokeneJson = restTemplate.postForObject(getAccessTokenByCodeUrl, request, JSONObject.class);
        logger.debug("---accessTokeneJson:{}", accessTokeneJson);
        //根据access_token获取资源
        String accessSuffix = "";
        if (!accessTokeneJson.isEmpty() && accessTokeneJson.get("access_token") != null) {
            accessSuffix = "?access_token=" + accessTokeneJson.get("access_token");
        }
        String findSomeResourceWithTokenUrl = "http://microservice-lcintegratedproject/api/ping" + accessSuffix;
        String someResourceStr = restTemplate.getForObject(findSomeResourceWithTokenUrl, String.class);
        logger.debug("---someResourceStr:{}", someResourceStr);
        return "/myzhihu/index";
    }

}
