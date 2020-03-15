package lc.mqreceiver;

import lc.config.RabbitConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author liuchaoOvO on 2018/12/28
 */
@Slf4j
@Component
public class DeadLetterQueueHandler {
    @RabbitListener (queues = RabbitConfig.LIND_DEAD_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void deadLetterQueueHandler(Map obj) {
        log.debug("DeadLetterQueue---:{}", obj.toString());
    }

}

