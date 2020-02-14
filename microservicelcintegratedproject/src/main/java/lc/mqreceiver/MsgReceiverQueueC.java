package lc.mqreceiver;

import lc.config.RabbitConfig;
import lc.entity.TopicObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * @author liuchaoOvO on 2018/12/28
 */
@Component
@RabbitListener(queues = RabbitConfig.QUEUE_C,containerFactory = "rabbitListenerContainerFactory")
public class MsgReceiverQueueC
{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @RabbitHandler
    public void processHandler(@Payload TopicObj obj)
    {
        logger.info("QUEUE_C--客户端收到的信息："+obj.toString());
    }
}

