package lc.service.impl;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import lc.dto.OrderInfo;
import lc.dto.SeckillOrder;
import lc.entity.GoodsVo;
import lc.entity.SysUser;
import lc.service.feignClient.ConsumerService;
import lc.service.secKill.OrderService;
import lc.service.secKill.ProductService;
import lc.service.secKill.SecKillService;
import lc.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author liuchaoOvO on 2019/5/22
 */
@Service
@Slf4j
public class SecKillServiceImpl implements SecKillService {
    //FeignClient 调用
    @Autowired
    private ConsumerService consumerService;

    @Autowired
    ProductService productService;
    @Autowired
    OrderService orderService;
    @Autowired
    RedisUtil redisUtil;

    @Override
    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return productService.getGoodsVoByGoodsId(goodsId);
    }

    @Override
    @LcnTransaction //分布式事务注解
    //保证这三个操作，减库存 下订单 写入秒杀订单是一个事务
    public OrderInfo seckill(SysUser user, GoodsVo goodsVo) throws Exception {
        OrderInfo orderInfo = null;
        //减库存
        boolean success = productService.reduceStock(goodsVo);
        if (success) {
            //下订单 写入秒杀订单
            orderInfo = orderService.createOrder(user, goodsVo);
            // 测试事务性： int v = 100/0;
        } else {
            setGoodsOver(goodsVo.getId());

        }
        // feign 调用远程  测试分布式服务的事务性
        SysUser sysUser = new SysUser();
        sysUser.setId("1");
        sysUser.setUsername("test_seckill()");
        sysUser.setPassword("1");
        consumerService.add(sysUser);
        return orderInfo;
    }

    @Override
    public long getSeckillResult(String userId, long goodsId) {
        SeckillOrder order = orderService.getOrderByUserIdGoodsId(userId, goodsId);
        if (order != null) {
            return order.getOrderId();
        } else {
            boolean isOver = getGoodsOver(goodsId);
            if (isOver) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    private void setGoodsOver(Long goodsId) {
        redisUtil.set("go" + "" + goodsId, true);
    }

    private boolean getGoodsOver(long goodsId) {
        return redisUtil.exists("go" + "" + goodsId);
    }
}
