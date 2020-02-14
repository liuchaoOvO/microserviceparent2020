package lc; /**
 * @author liuchaoOvO on 2019/3/15
 */
import com.codingapi.txlcn.tc.config.EnableDistributedTransaction;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("lc.dao")
@EnableEurekaClient
@EnableDistributedTransaction  //开启分布式事务
//@EnableCircuitBreaker          //开启断路器功能，进行容错管理
public class HystrixProviderApplication
{

    public static void main(String[] args) {

        SpringApplication.run(HystrixProviderApplication.class, args);
    }

}
