/*
package lc.multidatasourceconfig.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

*/
/**
 * @author liuchaoOvO on 2019/10/22
 *//*

@Slf4j
@Service
@ConfigurationProperties (prefix = "datasource.config", ignoreUnknownFields = true)
public class YYDataSourceConfig {
    */
/**
     * 配置项列表
     *//*

    //数据源配置映射类
    private List<DatasourceProp> datasources = new ArrayList<>();
    //多数据源配置映射类
    private MultiDataSourceProp multiDataSource=new MultiDataSourceProp();

    public List<DatasourceProp> getDatasources() {
        return datasources;
    }

    public void setDatasources(List<DatasourceProp> datasources) {
        this.datasources = datasources;
    }

    public MultiDataSourceProp getMultiDataSource() {
        return multiDataSource;
    }

    public void setMultiDataSource(MultiDataSourceProp multiDataSource) {
        this.multiDataSource = multiDataSource;
    }
}
*/
