package lc.multidatasourceconfig.config;

import com.zaxxer.hikari.HikariConfig;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author liuchaoOvO on 2019/10/22
 * Hikari连接池配置
 */
@Data
@Setter
@Getter
public class DatasourceProp extends HikariConfig {
    private String id;
}