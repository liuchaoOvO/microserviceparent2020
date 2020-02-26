package lc.entity;

import lc.util.UUIDUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author liuchaoOvO on 2019/5/22
 */
@Setter
@Getter
@ToString
@EqualsAndHashCode
public class SecOrder implements Serializable {
    private static final long serialVersionUID = 1724254862421035876L;

    private String id;
    private String userId;
    private String productId;
    private BigDecimal productPrice;
    private BigDecimal amount;


    public SecOrder(String productId) {
        String utilId = UUIDUtil.getUniqueKey();
        this.id = utilId;
        this.userId = "userId" + utilId;
        this.productId = productId;
    }

    public SecOrder() {
    }

    @Override
    public String toString() {
        return "SecOrder{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", productId='" + productId + '\'' +
                ", productPrice=" + productPrice +
                ", amount=" + amount +
                '}';
    }
}
