package lc.mqreceiver;

import com.rabbitmq.client.Channel;
import lc.config.RabbitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author liuchaoOvO on 2018/12/28
 */
@Component
public class MsgReceiverQueueA {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //@Payload SysUser obj
    @RabbitListener (queues = RabbitConfig.QUEUE_A, containerFactory = "rabbitListenerContainerFactory")
    public void processHandler(Channel channel, Message message) {
        logger.info("recieved message:{}", message);
        boolean success = false;
        try {
            //处理业务逻辑
            Object object = (new Jackson2JsonMessageConverter()).fromMessage(message);
            success = true;
        } catch (Exception e) {
            //.... 可以丢弃消息或重入队列
            success = false;
        }


        //回调成功确认消息
        if (success) {
            //成功确认消息
            try {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            } catch (IOException e) {
                logger.info("basicAck ex:" + e.getMessage());
                try {
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                } catch (IOException ee) {
                    logger.info("again basicAck ex:" + ee.getMessage());
                }
            }
        }

    }
}


