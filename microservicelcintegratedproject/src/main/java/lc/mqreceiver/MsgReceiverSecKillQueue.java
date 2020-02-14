package lc.mqreceiver;

import lc.config.RabbitConfig;
import lc.dto.OrderInfo;
import lc.entity.GoodsVo;
import lc.entity.SeckillMessage;
import lc.service.secKill.SecKillService;
import lc.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * @author liuchaoOvO on 2018/12/28
 */
@Component
@RabbitListener(queues = RabbitConfig.QUEUE_SecKillQueue,containerFactory = "rabbitListenerContainerFactory")
public class MsgReceiverSecKillQueue
{
    @Autowired
    private SecKillService secKillService;
    @Autowired
    private RedisUtil redisUtil;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @RabbitHandler
    public void processSecKill(@Payload SeckillMessage obj)
    {
        try
        {
            logger.info("receive message:" + obj.toString());
            lc.entity.SysUser user = obj.getUser();
            long goodsId = obj.getGoodsId();
            GoodsVo goodsVo = secKillService.getGoodsVoByGoodsId(goodsId);
            int stock = goodsVo.getStock_count();
            if (stock <= 0)
            {
                return;
            }
            //判断重复秒杀
            Object order = redisUtil.get("" + user.getId() + "_" + goodsId);
            if (order != null)
            {
                return;
            }
            try {
                //减库存 下订单 写入秒杀订单
                OrderInfo orderInfo = secKillService.seckill(user, goodsVo);
                logger.info("orderInfo:" + orderInfo.toString());
            }catch (Exception e){
                logger.info(e.getMessage());
            }
        }catch (Exception e){
            logger.debug(e.getMessage());
        }
    }

}

