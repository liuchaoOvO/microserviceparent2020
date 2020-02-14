package com;

import com.codingapi.txlcn.tm.config.EnableTransactionManagerServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@Slf4j
@SpringBootApplication
@EnableDiscoveryClient
@EnableTransactionManagerServer
public class TxManagerApplication {
    // 访问地址：http://127.0.0.1:7970/admin/index.html#/    后台密码：codingapi
    public static void main(String[] args) {
        SpringApplication.run(TxManagerApplication.class, args);
        log.info("tx-manager start success");
    }

}
