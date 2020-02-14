package lc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author liuchaoOvO on 2019/3/21
 */
@SpringBootApplication
@EnableConfigServer
@EnableEurekaClient
public class ConfigServerApp {

    public static void main(String[] args) {

        SpringApplication.run(ConfigServerApp.class, args);
    }

}
