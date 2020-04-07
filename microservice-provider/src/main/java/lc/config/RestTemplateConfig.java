package lc.config;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author liuchaoOvO on 2020/4/4
 * @Description RestTemplateConfig
 */
@Configuration
public class RestTemplateConfig {
    private static final Logger log = LoggerFactory.getLogger(RestTemplateConfig.class);
    @Value ("${env.httpclient.max-total:500}")
    private Integer httpClientMaxTotal;
    @Value("${env.httpclient.max-per-route:200}")
    private Integer httpClientMaxPerRoute;


    @Bean
    @LoadBalanced
    RestTemplate restTemplate(RestTemplateBuilder builder, HttpClient httpClient) {
        return builder.requestFactory(() -> {
            return new HttpComponentsClientHttpRequestFactory(httpClient);
        }).build();
    }

    @Bean
    HttpClient cruxHttpClient() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(this.httpClientMaxTotal);
        connectionManager.setDefaultMaxPerRoute(this.httpClientMaxPerRoute);
        log.debug("http client pool,max-total:{}", this.httpClientMaxTotal);
        log.debug("http client pool,max-per-route:{}", this.httpClientMaxPerRoute);
        return HttpClientBuilder.create().setConnectionManager(connectionManager).build();
    }
}
