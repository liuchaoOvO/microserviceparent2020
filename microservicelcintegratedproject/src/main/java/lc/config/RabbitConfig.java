package lc.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;


/**
 * @author liuchaoOvO on 2018/12/28
 * @description RabbitMQ 配置类
 */
@Configuration
public class RabbitConfig {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value ("${rabbitmq.host}")
    private String host;
    @Value ("${rabbitmq.port}")
    private int port;
    @Value ("${rabbitmq.username}")
    private String username;
    @Value ("${rabbitmq.password}")
    private String password;

    //交换机
    public static final String DirectExchange_A = "directExchange_A";
    public static final String TopicExchange_B = "topicExchange_B";
    public static final String FanoutExchange_C = "fanoutExchange_C";

    //路由键  用于把生产者的数据绑定到交换机上的
    public static final String DirectExchange_ROUTINGKEY = "DirectExchange_ROUTINGKEY";
    public static final String DirectExchange_SecKillROUTINGKEY = "DirectExchange_SecKillROUTINGKEY";
    public static final String TopicExchange_ROUTINGKEYSecond = "topic.second";
    public static final String TopicExchange_ROUTINGKEYThird = "topic.second.third";

    //绑定键  用于把交换机的消息绑定到队列
    public final static String TopicROUTINGKEYOne = "topic.*";
    public final static String TopicROUTINGKEYAll = "topic.#";

    //队列
    public static final String QUEUE_A = "QUEUE_A";
    public static final String QUEUE_B = "QUEUE_B";
    public static final String QUEUE_C = "topic.QUEUE_C";
    public static final String QUEUE_D = "topic.QUEUE_D";
    public static final String QUEUE_Fanout_A = "fanout.A";
    public static final String QUEUE_Fanout_B = "fanout.B";
    public static final String QUEUE_SecKillQueue = "QUEUE_SecKillQueue";

    @Bean
    public ConnectionFactory connectionFactory() {
        logger.info("rabbitmq的配置信息为host:[" + host + "]port:[" + port + "]username:[" + username + "]password:[" + password + "]");
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host, port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setPublisherConfirms(true);  //自动确认机制 false为手动确认
        return connectionFactory;
    }

    @Bean
    @Scope (ConfigurableBeanFactory.SCOPE_PROTOTYPE)     //必须是prototype类型
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    /**
     * 针对消费者配置
     * 1. 设置交换机类型
     * 2. 将队列绑定到交换机
     * FanoutExchange: 将消息分发到所有的绑定队列，无routingkey的概念
     * HeadersExchange ：通过添加属性key-value匹配
     * DirectExchange:按照routingkey分发到指定队列
     * TopicExchange:多关键字匹配
     */
    @Bean
    public DirectExchange defaultExchange() {
        return new DirectExchange(DirectExchange_A);
    }

    @Bean
    TopicExchange topicExchange() {
        return new TopicExchange(TopicExchange_B);
    }

    @Bean
    FanoutExchange fanoutExchange() {
        return new FanoutExchange(FanoutExchange_C);
    }

    /**
     * 获取队列A
     */
    @Bean
    public Queue queueA() {
        return new Queue(QUEUE_A, true); //队列持久
    }

    /**
     * 获取队列B
     */
    @Bean
    public Queue queueB() {
        return new Queue(QUEUE_B, true); //队列持久
    }

    /**
     * 获取Topic队列C
     */
    @Bean
    public Queue queueC() {
        return new Queue(QUEUE_C, true); //队列持久
    }

    /**
     * 获取Topic队列D
     */
    @Bean
    public Queue queueD() {
        return new Queue(QUEUE_D, true); //队列持久
    }

    @Bean
    public Queue queueFanoutA() {
        return new Queue(QUEUE_Fanout_A);
    }

    @Bean
    public Queue queueFanoutB() {
        return new Queue(QUEUE_Fanout_B);
    }

    @Bean
    public Queue queue_SecKillQueue() {
        return new Queue(QUEUE_SecKillQueue, true);
    }

    @Bean
    public Binding binding_QueueA_DirectExchange_ROUTINGKEY() {
        return BindingBuilder.bind(queueA()).to(defaultExchange()).with(RabbitConfig.DirectExchange_ROUTINGKEY);
    }

    @Bean
    public Binding binding_QUEUE_SecKillQueue_DirectExchange_SecKillROUTINGKEY() {
        return BindingBuilder.bind(queue_SecKillQueue()).to(defaultExchange()).with(RabbitConfig.DirectExchange_SecKillROUTINGKEY);
    }

    @Bean
    Binding binding_queueFanoutA_FanoutExchange() {
        return BindingBuilder.bind(queueFanoutA()).to(fanoutExchange());
    }

    @Bean
    Binding binding_queueFanoutB_FanoutExchange() {
        return BindingBuilder.bind(queueFanoutB()).to(fanoutExchange());
    }

    @Bean
    Binding binding_queueC_TopicExchange() {
        return BindingBuilder.bind(queueC()).to(topicExchange()).with(TopicROUTINGKEYOne);
    }

    @Bean
    Binding binding_queueD_TopicExchange() {
        return BindingBuilder.bind(queueD()).to(topicExchange()).with(TopicROUTINGKEYAll);
    }

    //json消息转换器   保证接收和发送的格式一致
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }
}
