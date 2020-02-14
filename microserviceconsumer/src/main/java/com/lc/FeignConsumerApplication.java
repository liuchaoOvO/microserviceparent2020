package com.lc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author liuchaoOvO on 2019/3/15
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients(basePackages="com.lc.service")
public class FeignConsumerApplication
{

    public static void main(String[] args) {
        SpringApplication.run(FeignConsumerApplication.class, args);
    }

}
