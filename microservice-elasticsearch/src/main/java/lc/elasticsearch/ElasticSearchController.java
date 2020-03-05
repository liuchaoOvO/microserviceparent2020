package lc.elasticsearch;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liuchaoOvO on 2020/3/5
 * @Description TODO
 */
@Slf4j
@RestController
@RequestMapping ("/search")
public class ElasticSearchController {
    @Autowired
    private HighLevelClient client;

    @PostMapping ("/add")
    public String add() throws IOException {
        IndexRequest indexRequest1 = new IndexRequest("posts",//索引名称
                "doc",//类型名称
                "3");//文档ID
        //1、String Source
        String jsonString = "{" + "\"user\":\"kimchy\"," + "\"postDate\":\"2013-01-30\"," + "\"message\":\"trying out Elasticsearch\"" + "}";
        //2、Map Source
        Map<String, Object> jsonMap = new HashMap();
        jsonMap.put("user", "lcc");
        jsonMap.put("postDate", new Date());
        jsonMap.put("messagelcclc", "Map lcc trying out elasticsearch");

        //indexRequest1.source(jsonString, XContentType.JSON);
        indexRequest1.source(jsonMap);
        IndexResponse indexResponse = client.getClient().index(indexRequest1, RequestOptions.DEFAULT);
        //返回的IndexResponse允许检索有关执行操作的信息，如下所示：
        String index = indexResponse.getIndex();
        String type = indexResponse.getType();
        String id = indexResponse.getId();
        long version = indexResponse.getVersion();
        log.debug("add method indexResponse result--index:{},type:{},id:{},version:{}", index, type, id, version);
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
        return " es document data add success...";
    }

    @GetMapping ("get")
    @SneakyThrows
    public String get(@RequestParam String index, @RequestParam String type, @RequestParam String id) {
        GetRequest getRequest = new GetRequest();
        getRequest.index(index);
        getRequest.type(type);
        getRequest.id(id);
        GetResponse response = client.getClient().get(getRequest, RequestOptions.DEFAULT);
        System.out.println(response.getSourceAsString());
        return "get success,response result:" + response.getSourceAsString();
    }

    @DeleteMapping ("delete")
    @SneakyThrows
    public String delete(@RequestParam String index, @RequestParam String type, @RequestParam String id) {
        DeleteRequest deleteRequest = new DeleteRequest();
        deleteRequest.index(index);
        deleteRequest.type(type);
        deleteRequest.id(id);
        DeleteResponse response = client.getClient().delete(deleteRequest, RequestOptions.DEFAULT);
        String responseIndex = response.getIndex();
        String responseType = response.getType();
        String responseId = response.getId();
        long version = response.getVersion();
        log.debug("delete method DeleteResponse result--index:{},type:{},id:{},version:{}", responseIndex, responseType, responseId, version);
        return "delete success";
    }

    @PostMapping ("update")
    @SneakyThrows
    public String update(@RequestParam String index, @RequestParam String type, @RequestParam String id, @RequestBody Map map) {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index(index);
        updateRequest.type(type);
        updateRequest.id(id);
        updateRequest.doc(map);
        UpdateResponse response = client.getClient().update(updateRequest, RequestOptions.DEFAULT);
        String responseIndex = response.getIndex();
        String responseType = response.getType();
        String responseId = response.getId();
        long version = response.getVersion();
        log.debug("update method UpdateResponse result--index:{},type:{},id:{},version:{}", responseIndex, responseType, responseId, version);
        return "update success";
    }

}
