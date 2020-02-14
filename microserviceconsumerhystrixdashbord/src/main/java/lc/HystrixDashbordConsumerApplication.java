package lc;

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.annotation.Bean;

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
