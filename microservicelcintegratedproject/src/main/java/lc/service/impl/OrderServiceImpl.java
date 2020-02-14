package lc.service.impl;

import com.codingapi.txlcn.tc.annotation.DTXPropagation;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.codingapi.txlcn.tc.annotation.TxcTransaction;
import lc.dao.order.OrderDao;
import lc.dao.product.ProductDao;
import lc.dto.OrderInfo;
import lc.dto.SeckillOrder;
import lc.entity.GoodsVo;
import lc.entity.SysUser;
import lc.service.secKill.OrderService;
import lc.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author liuchaoOvO on 2019/5/23
 */
@Service
public class OrderServiceImpl implements OrderService
{
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private RedisUtil redisUtil;
    @Override
    @LcnTransaction(propagation = DTXPropagation.SUPPORTS)//分布式事务注解
    public OrderInfo createOrder(SysUser user, GoodsVo goodsVo) throws Exception
    {

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goodsVo.getId());
        orderInfo.setGoodsName(goodsVo.getGoodsName());
        orderInfo.setGoodsPrice(goodsVo.getGoodsPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(new Long(user.getId()));
        orderDao.insert(orderInfo);
            // 测试事务性： int v = 100/0;
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setGoodsId(goodsVo.getId());
        seckillOrder.setOrderId(orderInfo.getId());
        seckillOrder.setUserId(user.getId());
        orderDao.insertSeckillOrder(seckillOrder);

        redisUtil.set("seckill"+ "" + user.getId() + "_" + goodsVo.getId(), seckillOrder);

        return orderInfo;

    }

    @Override
    public SeckillOrder getOrderByUserIdGoodsId(String userId, long goodsId)
    {
        return (SeckillOrder) redisUtil.get("seckill"+"" + userId + "_" + goodsId);

    }
}
