package lc.controller.mq;

import lc.entity.FanoutObj;
import lc.entity.SysUser;
import lc.entity.TopicObj;
import lc.mqproducer.MsgProducer;
import lc.util.UUIDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * @author liuchaoOvO on 2019/4/11
 * @description RabbitMQ Controller
 */
@Controller
@RequestMapping (value = "/mq")
public class MQController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private MsgProducer msgProducer;
    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    //验证直接模式
    @RequestMapping (value = "/mqSendUserObj", method = RequestMethod.POST)
    @ResponseBody
    public void mqSendUserObj(SysUser sysUser) {
        //uuid 设置主键
        sysUser.setId(UUIDUtil.uuid());
        sysUser.setStatus(1);
        sysUser.setLast_ver(new Date());
        sysUser.setPassword(bCryptPasswordEncoder.encode(sysUser.getPassword()));
        String correlationId = msgProducer.mqSendUserObj(sysUser);
        if (correlationId != "") {
            logger.info("发送到mq的请求成功：" + correlationId + "/mqSendMsg");
        } else {
            logger.info("发送到mq的请求失败");
        }
    }

    //验证广播模式
    @RequestMapping (value = "/mqSendFanoutObj", method = RequestMethod.POST)
    @ResponseBody
    public void mqSendFanoutObj(FanoutObj object)  {
        String correlationId = msgProducer.mqSendFanoutObj(object);
        if (correlationId != "") {
            logger.info("发送到mq的请求成功：" + correlationId + "/mqSendFanoutObj");
        } else {
            logger.info("发送到mq的请求失败");
        }
    }

    //验证话题模式
    @RequestMapping (value = "/mqSendTopicObj", method = RequestMethod.POST)
    @ResponseBody
    public void mqSendTopicObj(TopicObj object) {
        String correlationId = msgProducer.mqSendTopicObj(object);
        if (correlationId != "") {
            logger.info("发送到mq的请求成功：" + correlationId + "/mqSendTopicObj");
        } else {
            logger.info("发送到mq的请求失败");
        }
    }

    @RequestMapping (value = "/mqSendTopMsg", method = RequestMethod.POST)
    public void mqSendTopMsg(String msg)  {
        String correlationId = msgProducer.sendTopicMsg(msg);
        if (correlationId != "") {
            System.out.println("发送到mq的请求成功：" + correlationId + "/mqSendTopMsg");
        } else {
            System.out.println("发送到mq的请求失败");
        }
    }

    @RequestMapping (value = "/mqUI", method = RequestMethod.GET)
    public String mqUI()  {
        return "/mq/mqUI";
    }
}
