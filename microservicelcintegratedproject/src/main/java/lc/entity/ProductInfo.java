package lc.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author liuchaoOvO on 2019/5/22
 */
@Setter
@Getter
@ToString
@EqualsAndHashCode
public class ProductInfo {
    private String productId;
    /**
     * 产品名
     */
    private String productName;
    /**
     * 单价
     */
    private BigDecimal productPrice;
    /**
     * 库存
     */
    private Integer productStock;
    /**
     * 产品描述
     */
    private String productDescription;
    /**
     * 小图
     */
    private String productIcon;
    /**
     * 商品状态 0正常 1下架
     */
    private Integer productStatus = 0;
    /**
     * 类目编号
     */
    private Integer categoryType;

    /**
     * 创建日期
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;

    public ProductInfo(String productId) {
        this.productId = productId;
        this.productPrice = new BigDecimal(3.2);
    }

    public ProductInfo() {
    }
}
