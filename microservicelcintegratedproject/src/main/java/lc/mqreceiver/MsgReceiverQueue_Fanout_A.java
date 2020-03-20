package lc.mqreceiver;

import com.rabbitmq.client.Channel;
import lc.config.RabbitConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author liuchaoOvO on 2018/12/28
 */
@Slf4j
@Component
public class MsgReceiverQueue_Fanout_A {

    @RabbitListener (queues = RabbitConfig.QUEUE_Fanout_A, containerFactory = "rabbitListenerContainerFactory")
    public void processHandler(Channel channel, Message message) {
        log.info("QUEUE_Fanout_A-- recieved message:{}", message);
        boolean success = false;
        try {
            //处理业务逻辑
            Object object = (new Jackson2JsonMessageConverter()).fromMessage(message);
            System.out.println("QUEUE_Fanout_A--处理业务逻辑 object " + object);
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
                log.error("basicAck ex:{}", e.getMessage());
                try {
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                } catch (IOException ee) {
                    log.error("again basicAck ex:{}", ee.getMessage());
                }
            }
        }
    }
}

