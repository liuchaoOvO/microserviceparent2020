package lc.mqreceiver;

import lc.config.RabbitConfig;
import lc.entity.FanoutObj;
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
@RabbitListener(queues = RabbitConfig.QUEUE_Fanout_A,containerFactory = "rabbitListenerContainerFactory")
public class MsgReceiverQueue_Fanout_A
{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RabbitHandler
    public void processHandler(@Payload FanoutObj obj)
    {
        logger.info("QUEUE_Fanout_A--用户信息："+obj.toString());
    }
}

