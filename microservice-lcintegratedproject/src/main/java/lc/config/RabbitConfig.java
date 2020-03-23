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
    @Value ("${spring.rabbitmq.host}")
    private String host;
    @Value ("${spring.rabbitmq.port}")
    private int port;
    @Value ("${spring.rabbitmq.username}")
    private String username;
    @Value ("${spring.rabbitmq.password}")
    private String password;
    /**
     * 调用正常队列过期时间 单位为微秒.
     */
    @Value ("${tq.makecall.expire:60000}")
    private long makeCallExpire;

    //交换机
    public static final String DirectExchange_A = "directExchange_A";
    public static final String TopicExchange_B = "topicExchange_B";
    public static final String FanoutExchange_C = "fanoutExchange_C";
    public static final String TopicExchange_DeadLetter = "topicExchange_DeadLetter"; //死信交换机名称

    //路由键  用于把生产者的数据绑定到交换机上的
    public static final String DirectExchange_ROUTINGKEY = "DirectExchange_ROUTINGKEY";
    public static final String DirectExchange_SecKillROUTINGKEY = "DirectExchange_SecKillROUTINGKEY";
    public static final String TopicExchange_ROUTINGKEYSecond = "topic.second";
    public static final String TopicExchange_ROUTINGKEYThird = "topic.second.third";
    public static final String TopicExchange_DLROUTINGKEY = "lind.queue";

    //绑定键  用于把交换机的消息绑定到队列
    public final static String TopicROUTINGKEYOne = "topic.*";  //表达式适合topic.开头的下一级
    public final static String TopicROUTINGKEYAll = "topic.#";  //表达式适合topic.开头的所有

    //队列
    public static final String QUEUE_A = "QUEUE_A";
    public static final String QUEUE_B = "QUEUE_B";
    public static final String QUEUE_ProviderService_A = "QUEUE_Provider_A";
    public static final String QUEUE_C = "topic.QUEUE_C";
    public static final String QUEUE_D = "topic.QUEUE_D";
    public static final String QUEUE_Fanout_A = "fanout.A";
    public static final String QUEUE_Fanout_B = "fanout.B";
    public static final String QUEUE_SecKillQueue = "QUEUE_SecKillQueue";

    public static final String LIND_DEAD_QUEUE = "lind.queue.dead";

    @Bean
    public ConnectionFactory connectionFactory() {
        logger.info("rabbitmq的配置信息为host:[" + host + "]port:[" + port + "]username:[" + username + "]password:[" + password + "]");
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host, port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setPublisherConfirms(true);  //自动确认机制:只确认是否正确到达 Exchange 中 false为手动确认
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
     * HeadersExchange : 通过添加属性key-value匹配
     * DirectExchange: 按照routingkey分发到指定队列
     * TopicExchange: 多关键字匹配
     */
    @Bean
    public DirectExchange defaultExchange() {
        return new DirectExchange(DirectExchange_A);
    }

    @Bean
    TopicExchange topicExchange() {
        return new TopicExchange(TopicExchange_B);
    }

    /**
     * 广播交换机.
     */
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


    /**
     * 创建死信交换机.
     */
    @Bean
    public TopicExchange lindExchangeDl() {
        return (TopicExchange) ExchangeBuilder.topicExchange(TopicExchange_DeadLetter).durable(true).build();
    }

    /**
     * 创建普通队列.
     */
    @Bean
    public Queue lindQueue() {
        return QueueBuilder.durable(QUEUE_ProviderService_A)
                .withArgument("x-dead-letter-exchange", TopicExchange_DeadLetter)//设置死信交换机
                .withArgument("x-message-ttl", makeCallExpire)
                .withArgument("x-dead-letter-routing-key", LIND_DEAD_QUEUE)//设置死信routingKey
                .build();
    }

    /**
     * 创建死信队列.
     */
    @Bean
    public Queue lindDelayQueue() {
        return QueueBuilder.durable(LIND_DEAD_QUEUE).build();
    }

    /**
     * 绑定死信队列.
     */
    @Bean
    public Binding bindDeadBuilders() {
        return BindingBuilder.bind(lindDelayQueue()).to(lindExchangeDl()).with(LIND_DEAD_QUEUE);
    }

    /**
     * 绑定普通队列.
     *
     * @return
     */
    @Bean
    public Binding bindBuilders() {
        return BindingBuilder.bind(lindQueue()).to(topicExchange()).with(TopicExchange_DLROUTINGKEY);
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
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);  // 手动确认模式
        return factory;
    }
}
