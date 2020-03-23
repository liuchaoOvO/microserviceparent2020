package lc.mqreceiver;

import com.rabbitmq.client.Channel;
import lc.config.RabbitConfig;
import lc.dto.OrderInfo;
import lc.entity.GoodsVo;
import lc.entity.SeckillMessage;
import lc.service.secKill.SecKillService;
import lc.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author liuchaoOvO on 2018/12/28
 */
@Slf4j
@Component
public class MsgReceiverSecKillQueue {
    @Autowired
    private SecKillService secKillService;
    @Autowired
    private RedisUtil redisUtil;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RabbitListener (queues = RabbitConfig.QUEUE_SecKillQueue, containerFactory = "rabbitListenerContainerFactory")
    public void processSecKill(Channel channel, Message message) {
        boolean success = false;
        logger.debug("processSecKill-receive message:{}", message);
        //处理业务逻辑
        SeckillMessage obj = (SeckillMessage) (new Jackson2JsonMessageConverter()).fromMessage(message);
        lc.entity.SysUser user = obj.getUser();
        long goodsId = obj.getGoodsId();
        try {
            GoodsVo goodsVo = secKillService.getGoodsVoByGoodsId(goodsId);
            int stock = goodsVo.getStock_count();
            if (stock <= 0) {
                return;
            }
            //判断重复秒杀
            Object order = redisUtil.get("" + user.getId() + "_" + goodsId);
            if (order != null) {
                return;
            }
            try {
                //减库存 下订单 写入秒杀订单
                OrderInfo orderInfo = secKillService.seckill(user, goodsVo);
                logger.debug("orderInfo:{}", orderInfo.toString());
                success = true;
            } catch (Exception e) {
                logger.error("减库存 下订单 写入秒杀订单过程存在错误:{}", e.getMessage());
                // 往redis 写入一个空值 证明该数据错误，方便后面查询该秒杀结果时，给出秒杀失败的结果
                redisUtil.set("seckill" + "" + user.getId() + "_" + goodsVo.getId(), null);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            // 往redis 写入一个空值 证明该数据错误，方便后面查询该秒杀结果时，给出秒杀失败的结果
            redisUtil.set("seckill" + "" + user.getId() + "_" + goodsId, null);
        } finally {
            //先都按照消息已被确认处理，后面会根据上面的处理逻辑给出补偿机制（消息重发或发给其他消息队列等方案）
            success = true;
            //回调成功确认消息
            if (success) {
                try {
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                } catch (IOException e) {
                    log.info("basicAck ex:" + e.getMessage());
                    try {
                        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                    } catch (IOException ee) {
                        log.info("again basicAck ex:" + ee.getMessage());
                    }
                }
            }
        }
    }
}



