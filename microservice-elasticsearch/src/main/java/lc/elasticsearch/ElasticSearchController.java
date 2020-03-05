package lc.elasticsearch;

import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author liuchaoOvO on 2020/3/5
 * @Description TODO
 */
@RestController
@RequestMapping ("/search")
public class ElasticSearchController {
    @Autowired
    private HighLevelClient client;

    @PostMapping ("/add")
    public String add() throws IOException {
        IndexRequest indexRequest1 = new IndexRequest("posts",//索引名称
                "doc",//类型名称
                "1");//文档ID
        String jsonString = "{" + "\"user\":\"kimchy\"," + "\"postDate\":\"2013-01-30\"," + "\"message\":\"trying out Elasticsearch\"" + "}";
        indexRequest1.source(jsonString, XContentType.JSON);
        IndexResponse indexResponse = client.getClient().index(indexRequest1, RequestOptions.DEFAULT);
        //返回的IndexResponse允许检索有关执行操作的信息，如下所示：
        String index = indexResponse.getIndex();
        String type = indexResponse.getType();
        String id = indexResponse.getId();
        long version = indexResponse.getVersion();
        if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
            //处理（如果需要）第一次创建文档的情况
        } else if (indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
            //处理（如果需要）文档被重写的情况
        }
        ReplicationResponse.ShardInfo shardInfo = indexResponse.getShardInfo();
        if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
            //处理成功分片数量少于总分片数量的情况
        }
        if (shardInfo.getFailed() > 0) {
            for (ReplicationResponse.ShardInfo.Failure failure : shardInfo.getFailures()) {
                String reason = failure.reason();//处理潜在的失败
            }
        }
        return "add success...";
    }
}
