package lc.controller;

import lc.entity.SysUser;
import lc.service.ConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author liuchaoOvO on 2019/3/15
 */
 @RestController
public class UserConsumerController {
      //  private static String REST_URL_PREFIX = "http://localhost:7002";
    /*直接根据微服务名调用，而不再是根据地址和端口了，运用了eureka的发现功能*/
    // private static String REST_URL_PREFIX = "http://microservicecloud-provider";

    // @Autowired
    //  private RestTemplate restTemplate;
    @Autowired
    private ConsumerService service;

        @RequestMapping(value="/consumer/add")
        public boolean addUser(SysUser user){
            Boolean flag = service.add(user);
            return flag;
        }

        @RequestMapping(value="/consumer/get/{id}")
        public SysUser get(@PathVariable("id") String id){
            SysUser user = service.get(id);
            return user;
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        @RequestMapping(value="/consumer/list")
        public List<SysUser> getList(){
            List list = service.getAll();
            return list;
        }

}
