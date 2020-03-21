package lc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author liuchaoOvO on 2019/3/15
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients (basePackages = "lc.service")
public class HystrixFeignConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(HystrixFeignConsumerApplication.class, args);
    }

}
