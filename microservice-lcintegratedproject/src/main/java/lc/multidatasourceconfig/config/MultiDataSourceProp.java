package lc.multidatasourceconfig.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @author liuchaoOvO on 2019/10/23
 */
@Data
@Setter
@Getter
public class MultiDataSourceProp implements Serializable {
    private String id;

    private String dbType;

    private String defaultDataSource;

    private List<YYDataSource> dataSources;

}
