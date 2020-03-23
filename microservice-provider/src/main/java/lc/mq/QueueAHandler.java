package lc.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author liuchaoOvO on 2018/12/28
 */
@Component
public class QueueAHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RabbitListener (queues = "QUEUE_Provider_A", containerFactory = "rabbitListenerContainerFactory")
    public void processHandler(Map obj) {
        logger.debug("QueueAHandler#processHandler----send log info...");
        logger.info("lind.queue------Object信息:obj{}", obj.toString());
    }
}

