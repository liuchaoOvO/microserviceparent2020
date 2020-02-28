package lc.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * @author liuchaoOvO on 2019/5/23
 */
@Setter
@Getter
@ToString
@EqualsAndHashCode
public class GoodsVo extends Goods {
    private Double seckill_price;
    private Integer stock_count;
    private Date start_date;
    private Date end_date;
    private Integer version;
}
