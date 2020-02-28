package lc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

/**
 * @author liuchaoOvO on 2019/3/19
 */
@SpringBootApplication
@EnableHystrixDashboard
public class HystrixDashbordConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(HystrixDashbordConsumerApplication.class, args);
    }


}
