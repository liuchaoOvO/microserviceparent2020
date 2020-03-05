package lc.elasticsearch;

import lc.elasticsearch.EsClientBuilders;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.sniff.Sniffer;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author liuchaoOvO on 2020/3/5
 * @Description TODO
 */
@Component
public class HighLevelClient {
    private static RestClientBuilder restClientBuilder = EsClientBuilders.getClientBulider();

    // 实例化客户端
    private static RestHighLevelClient restHighLevelClient;
    // 嗅探器实例化
    private static Sniffer sniffer;

    /**
     * 开启client，sniffer
     * @return
     */
    public RestHighLevelClient getClient() {
        restHighLevelClient = new RestHighLevelClient(restClientBuilder);
        //十秒刷新并更新一次节点
        sniffer = Sniffer.builder(restHighLevelClient.getLowLevelClient())
                .setSniffAfterFailureDelayMillis(10000)
                .build();

        return restHighLevelClient;
    }

    /**
     *
     * @throws Exception
     * 关闭sniffer client
     */
    public void closeRestHighLevelClient() throws Exception {
        if (null != restHighLevelClient) {
            try {
                sniffer.close();
                restHighLevelClient.close();
            } catch (IOException e) {
                throw new Exception("RestHighLevelClient Client close exception", e);
            }
        }
    }
}