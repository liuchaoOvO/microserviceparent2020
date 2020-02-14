package lc.service.secKill;

import lc.dto.OrderInfo;
import lc.dto.SeckillOrder;
import lc.entity.GoodsVo;
import lc.entity.SysUser;

/**
 * @author liuchaoOvO on 2019/5/23
 */
public interface OrderService
{

    OrderInfo createOrder(SysUser user, GoodsVo goodsVo) throws Exception;

    SeckillOrder getOrderByUserIdGoodsId(String userId, long goodsId);
}
