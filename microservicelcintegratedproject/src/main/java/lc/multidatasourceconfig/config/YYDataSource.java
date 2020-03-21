package lc.multidatasourceconfig.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author liuchaoOvO on 2019/10/23
 */
@Data
@Setter
@Getter
public class YYDataSource implements Serializable {
    String key;
    String beanId;
}