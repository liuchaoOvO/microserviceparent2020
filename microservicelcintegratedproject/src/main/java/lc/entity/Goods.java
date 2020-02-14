package lc.entity;

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
public class Goods
{   private Long id;
    private String goodsName;
    private String goodsTitle;
    private String goodsImg;
    private String goodsDetail;
    private Double goodsPrice;
    private Integer goodsStock;
}
