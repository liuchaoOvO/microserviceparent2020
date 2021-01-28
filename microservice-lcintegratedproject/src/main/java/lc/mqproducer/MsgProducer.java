package lc.mqproducer;

import lc.config.RabbitConfig;
import lc.entity.SeckillMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
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
public class MsgProducer implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {
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
        rabbitTemplate.setReturnCallback(this);
    }

    //直接 点对点
    public String mqSendUserObj(Object object) {
        try {
            String msgId = UUID.randomUUID().toString();
            CorrelationData correlationData = new CorrelationData(msgId);
            //把消息放入DirectExchange_ROUTINGKEY对应的队列当中去，对应的是队列A
            rabbitTemplate.setMandatory(true);
            rabbitTemplate.convertAndSend(RabbitConfig.DirectExchange_A, RabbitConfig.DirectExchange_ROUTINGKEY,
                    object, correlationData);
            return msgId;
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

    /**
     * 回调  只确认是否正确到达 Exchange 中
     * 消息发送到 Broker 后触发回调，确认消息是否到达 Broker 服务器，也就是只确认是否正确到达 Exchange 中
     * 消息的发送确认[只确认是否正确到达 Exchange 中,不能确认是否能到底消息队列queue中]
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        logger.info(" publish confirm 回调id:" + correlationData);
        if (ack) {
            logger.info("MQ回调 消息成功送达到交换机");
        } else {
            logger.info("MQ回调 消息送达到交换机失败:" + cause);
        }
    }

    /**
     * 通过实现 ReturnCallback 接口，启动消息失败返回，比如路由不到队列时触发回调
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        System.out.println("消息主体 message : " + message);
        System.out.println("消息主体 message : " + replyCode);
        System.out.println("描述：" + replyText);
        System.out.println("消息使用的交换器 exchange : " + exchange);
        System.out.println("消息使用的路由键 routing : " + routingKey);
    }
}
