package lc;

import lc.elasticsearch.HighLevelClient;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import java.util.Map;

/**
 * @author liuchaoOvO on 2020/3/5
 * @Description TODO
 */
public class EsOperateDemoTest {
    public static void main(String[] args) throws Exception {
        HighLevelClient highLevelClient = new HighLevelClient();
        RestHighLevelClient client = highLevelClient.getClient();
        SearchRequest searchRequest = new SearchRequest("book");
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        SearchHits hits = searchResponse.getHits();
        TotalHits totalHits = hits.getTotalHits();
        for (SearchHit hit : hits) {
            String index = hit.getIndex(); //获取文档的index
            String type = hit.getType(); //获取文档的type
            String id = hit.getId(); //获取文档的id
            Map<String, Object> sourceMap = hit.getSourceAsMap();
            String sourceString = hit.getSourceAsString(); //获取文档内容，转换为json字符串。

            System.out.println(id + "$$$$$$$$$$" + index + "$$$$$$$$$$$$$$$$$" + type + "&&&&&&&&&&&&&&&&" + sourceString);
        }

        highLevelClient.closeRestHighLevelClient();

    }
}