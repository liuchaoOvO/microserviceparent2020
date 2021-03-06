package lc.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author liuchaoOvO on 2019/4/1
 * @description 数据源配置类
 */
@Configuration
@PropertySource ("classpath:application.yml")
public class DataSourceConfiguration {
    @Value ("${spring.datasource.driverClassName}")
    private String driver;
    @Value ("${spring.datasource.url}")
    private String url;
    @Value ("${spring.datasource.username}")
    private String username;
    @Value ("${spring.datasource.password}")
    private String password;
    @Value ("${spring.datasource.maxActive}")
    private int maxActive;
    @Value ("${spring.datasource.minIdle}")
    private int minIdle;
    @Value ("${spring.datasource.maxWait}")
    private long maxWait;

    @Bean
    public DruidDataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setMaxActive(maxActive);
        dataSource.setMinIdle(minIdle);
        dataSource.setMaxWait(maxWait);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setTestOnBorrow(true);
        return dataSource;
    }
}


