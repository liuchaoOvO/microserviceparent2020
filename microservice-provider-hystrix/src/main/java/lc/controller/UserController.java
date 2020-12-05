package lc.controller;

/**
 * @author liuchaoOvO on 2019/3/15
 */

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lc.entity.SysUser;
import lc.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService service;
    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    @RequestMapping(value="/add", method=RequestMethod.POST)
    public boolean addUser(@RequestBody SysUser user){
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        boolean flag = service.addUser(user);
        return flag;
    }

    @RequestMapping(value="/get/{id}", method=RequestMethod.GET)
    /*
    * 测试后备模式(fallback)
    * 一旦服务调用失败，就调用hystrixGetUser方法
    */
    @HystrixCommand(fallbackMethod="hystrixGetUser",//指定服务降级处理方法；
            threadPoolKey = "circuitBreakerPool",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000"),
                    // 请求最小次数
                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "4"),
                    // 失败率阈值
                    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
                    // 熔断断开后,间隔7s,就尝试访问一次服务,查看服务是否已经恢复
                    @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "7000"),
                    // 时间窗口,从监听到第一次失败开始计时
                    @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "15000"),
                    // 在时间窗口中,收集统计信息的次数。在15s的窗口中每隔3s就收集一次,共5次。
                    @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "5")
            }
    )
    public SysUser getUser(@PathVariable("id") String id){
        SysUser user = service.getUser(id);
        if(user == null){
            logger.error("microservicecloud-provider  server ->getUser method throw RuntimeException:不存在id=" + id);
            throw new RuntimeException("不存在id=" + id + "对应的用户信息");
        }
        System.out.println("microservice-provider微服务在响应客户端请求……");
        System.out.println("user : " + user);
        return user;
    }

    @RequestMapping(value="/getUser/list", method=RequestMethod.GET)
    public List<SysUser> getUsers(){
        List<SysUser> users = service.getUsers();
        return users;
    }

    // 服务调用失败 请求结果找不到时，执行--->1、服务熔断
    public SysUser hystrixGetUser(@PathVariable("id") String id){
        logger.info("microservicecloud-provider  server ->hystrixGetUser method come in id=" + id);
        SysUser user = new SysUser(id, "hystrixGetUser==不存在该用户", "0");
        return user;
    }
}
