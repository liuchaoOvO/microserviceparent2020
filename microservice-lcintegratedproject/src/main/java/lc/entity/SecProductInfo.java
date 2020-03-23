package lc.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author liuchaoOvO on 2019/5/22
 */
@Setter
@Getter
@ToString
@EqualsAndHashCode
public class SecProductInfo {
    private String productId;
    private String stock;
}
