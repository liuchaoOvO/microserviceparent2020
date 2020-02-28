package lc; /**
 * @author liuchaoOvO on 2019/3/15
 */

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan ("lc.dao")
//@EnableEurekaClient
public class ProviderDynamicUpdatePorperties {

    public static void main(String[] args) {

        SpringApplication.run(ProviderDynamicUpdatePorperties.class, args);
    }

}
