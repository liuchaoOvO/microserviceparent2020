package lc.mqproducer;

import lc.config.RabbitConfig;
import lc.entity.SeckillMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * @author liuchaoOvO on 2018/12/28
 */
@Component
public class MsgProducer implements RabbitTemplate.ConfirmCallback {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    //由于rabbitTemplate的scope属性设置为ConfigurableBeanFactory.SCOPE_PROTOTYPE，所以不能自动注入，需要构造方法注入的方式
    private RabbitTemplate rabbitTemplate;

    /**
     * 构造方法注入rabbitTemplate
     */
    @Autowired
    public MsgProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        rabbitTemplate.setConfirmCallback(this); //rabbitTemplate如果为单例的话，那回调就是最后设置的内容
    }

    //直接 点对点
    public String mqSendUserObj(Object object) {
        try {
            CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
            //把消息放入DirectExchange_ROUTINGKEY对应的队列当中去，对应的是队列A
            rabbitTemplate.convertAndSend(RabbitConfig.DirectExchange_A, RabbitConfig.DirectExchange_ROUTINGKEY, object, correlationId);
            return correlationId.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    //直接 点对点 发消息到ProviderService 服务的远程监听+ 死信队列逻辑
    public String mqSendPoint2PointToRomoteService(Map map) {
        try {
            logger.debug("mqSendPoint2PointToRomoteService begin...");
            CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
            rabbitTemplate.convertAndSend(RabbitConfig.TopicExchange_B, RabbitConfig.TopicExchange_DLROUTINGKEY,
                    map, correlationId);
            return correlationId.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "mqSendPoint2PointToRomoteService fail---";
        }
    }

    //话题
    public String sendTopicMsg(String content) {
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        //把消息放入TopicExchange_ROUTINGKEYThird对应的队列当中去，对应的是队列D  因为topic.second.third 满足表达式topic.# 而不满足topic.*
        rabbitTemplate.convertAndSend(RabbitConfig.TopicExchange_B, RabbitConfig.TopicExchange_ROUTINGKEYThird, content, correlationId);
        return correlationId.toString();
    }

    //话题
    public String mqSendTopicAll(Object object) {
        try {
            CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
            //把消息放入TopicExchange_ROUTINGKEYThird对应的队列当中去，对应的是队列D  因为topic.second.third 满足表达式topic.# 而不满足topic.*
            rabbitTemplate.convertAndSend(RabbitConfig.TopicExchange_B, RabbitConfig.TopicExchange_ROUTINGKEYThird, object, correlationId);
            return correlationId.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    //话题
    public String mqSendTopicOne(Object object) {
        try {
            CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
            //把消息放入TopicExchange_ROUTINGKEYSecond对应的队列当中去，对应的是队列C和D  因为topic.second 满足表达式topic.# 和 topic.*
            rabbitTemplate.convertAndSend(RabbitConfig.TopicExchange_B, RabbitConfig.TopicExchange_ROUTINGKEYSecond, object, correlationId);
            return correlationId.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    //广播
    public String mqSendFanoutObj(Object object) {
        try {
            CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
            //把消息放入FanoutExchange_C对应的队列当中去，对应的是队列QUEUE_Fanout_A和QUEUE_Fanout_B
            rabbitTemplate.convertAndSend(RabbitConfig.FanoutExchange_C, null, object, correlationId);
            return correlationId.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * 回调
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        logger.info(" 回调id:" + correlationData);
        if (ack) {
            logger.info("MQ回调 消息成功消费");
        } else {
            logger.info("MQ回调 消息消费失败:" + cause);
        }
    }

    public String sendSecKillMsg(SeckillMessage message) {
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        try {
            rabbitTemplate.convertAndSend(RabbitConfig.DirectExchange_A, RabbitConfig.DirectExchange_SecKillROUTINGKEY, message, correlationId);
            return "sendSecKill发送成功，" + correlationId.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "sendSecKill发送失败，" + correlationId.toString();
        }
    }
}
