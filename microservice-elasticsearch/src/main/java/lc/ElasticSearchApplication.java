package lc; /**
 * @author liuchaoOvO on 2019/3/15
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class ElasticSearchApplication {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(ElasticSearchApplication.class);
        springApplication.run(args);
        log.info("lc.ElasticSearchApplication start success");
    }
}
