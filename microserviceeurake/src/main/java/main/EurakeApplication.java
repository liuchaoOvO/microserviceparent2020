package main;

/**
 * @author liuchaoOvO on 2019/3/15
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@Slf4j
@SpringBootApplication
@EnableEurekaServer
public class EurakeApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurakeApplication.class, args);
        log.info("eureka-server start success");
    }

}
