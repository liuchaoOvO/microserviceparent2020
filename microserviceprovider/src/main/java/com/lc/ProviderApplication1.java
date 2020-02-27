package com.lc; /**
 * @author liuchaoOvO on 2019/3/15
 */

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@MapperScan ("com.lc.dao")
@EnableEurekaClient
public class ProviderApplication1 {

    public static void main(String[] args) {

        SpringApplication.run(ProviderApplication1.class, args);
    }

}
