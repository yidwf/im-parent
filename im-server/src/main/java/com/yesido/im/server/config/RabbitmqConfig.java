package com.yesido.im.server.config;

import java.io.IOException;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.yesido.im.server.listeners.rabbitmq.MessageRouteListener;
import com.yesido.im.server.mq.decalre.QueueDeclare;

/**
 * rabbitmq配置类
 * 
 * @author yesido
 * @date 2019年7月25日 下午3:02:52
 */
@Configuration
public class RabbitmqConfig {

    @Autowired
    private ConnectionFactory connectionFactory;

    @Bean
    public SimpleMessageListenerContainer mqMessageContainer(MessageRouteListener listener) throws AmqpException, IOException {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        // String queueName = QueueDeclare.getMsgRouteQueue();
        // container.setQueueNames(queueName); // 使用这种但是不会自动创建queue
        container.setQueues(messageRouteQueue());
        // 将channel暴露给listener才能手动确认,AcknowledgeMode.MANUAL时必须为ture
        container.setExposeListenerChannel(true);
        // 消费者的最小数量
        // container.setConcurrentConsumers(1);

        // 消费者的最大数量,并发消费的时候需要设置,且>=concurrentConsumers
        // container.setMaxConcurrentConsumers(10);

        // 在单个请求中处理的消息个数，他应该大于等于事务数量
        container.setPrefetchCount(1);

        container.setAcknowledgeMode(AcknowledgeMode.MANUAL); // 设置确认模式为手工确认
        container.setMessageListener(listener); // 监听处理类
        return container;
    }

    /**
     * topic 交换机
     */
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(QueueDeclare.EX_TOPIC_IM_SERVER);
    }


    /**
     * 绑定队列到topic交换机上：路由模式，routingKey需要完整匹配才能接受
     */
    @Bean
    public Binding messageRouteQueueBinding() {
        return BindingBuilder.bind(messageRouteQueue()).to(topicExchange())
                .with(QueueDeclare.getMsgRouteRoutingKey());
    }

    @Bean
    public Queue messageRouteQueue() {
        // Queue queue = new Queue(QueueDeclare.getMsgRouteQueue());
        Queue queue = new Queue(QueueDeclare.getMsgRouteQueue(), true, false, false, null);
        return queue;
    }

}
