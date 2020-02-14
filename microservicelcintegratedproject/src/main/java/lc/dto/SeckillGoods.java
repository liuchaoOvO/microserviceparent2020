package lc.dto;

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
public class SeckillGoods
{   private Long id;
    private Long goodsId;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;
    private int version;
}
