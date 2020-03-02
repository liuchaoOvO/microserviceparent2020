package lc; /**
 * @author liuchaoOvO on 2019/3/15
 */

import com.codingapi.txlcn.tc.config.EnableDistributedTransaction;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Slf4j
@SpringBootApplication
@MapperScan ("lc.dao")
@EnableEurekaClient
@EnableFeignClients (basePackages = "lc.service.feignClient")
//@EnableCircuitBreaker
@EnableSwagger2
@EnableDistributedTransaction // 开启分布式事务
public class LCintegrateProApplication {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(LCintegrateProApplication.class);
        //在启动类中增加监听器监听自定义程序
        //springApplication.addListeners(new ApplicationStartup());
        springApplication.run(args);
        log.info("LCintegrateProApplication start success");
    }
}
