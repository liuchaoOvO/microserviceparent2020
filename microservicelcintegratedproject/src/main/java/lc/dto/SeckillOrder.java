package lc.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author liuchaoOvO on 2019/5/23
 */
@Setter
@Getter
@ToString
@EqualsAndHashCode
public class SeckillOrder
{   private Long id;
    private String userId;
    private Long  orderId;
    private Long goodsId;
}
