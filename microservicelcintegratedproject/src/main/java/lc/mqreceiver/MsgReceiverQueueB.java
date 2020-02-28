package lc.mqreceiver;

import lc.config.RabbitConfig;
import lc.entity.SysUser;
import lc.service.user.UserService;
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
@RabbitListener (queues = RabbitConfig.QUEUE_B, containerFactory = "rabbitListenerContainerFactory")
public class MsgReceiverQueueB {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    //处理User的Service
    @Autowired
    private UserService service;

    @RabbitHandler
    public void processRegist(@Payload SysUser obj) {
        try {
            logger.info("processObj()接收处理队列A当中的消息：" + obj.toString());
            boolean flag = service.addUser(obj);
            if (flag == true) {
                logger.info("处理队列A的数据：" + obj.toString() + ",成功。");
            } else {
                logger.info("处理队列A的数据：" + obj.toString() + ",失败。");
            }
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
    }
}


