package com.yesido.im.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import com.yesido.im.server.consts.RedisConst;
import com.yesido.im.server.listeners.redis.NotifyServerLogoutListener;

/**
 * redis消息广播
 * 
 * @author yesido
 * @date 2019年7月18日 下午3:13:50
 */
@Configuration
public class RedisContainerConfig {

    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(new MessageListenerAdapter(listenerAdapter), new PatternTopic(RedisConst.REDIS_CHANNEL_NOTIFY_SERVER_LOGOUT));
        // 可以添加多个 messageListener
        return container;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(NotifyServerLogoutListener receiver) {
        // 这个地方是给messageListenerAdapter 传入一个消息接受的处理器，利用反射的方法调用“handleMessage”
        // 也有好几个重载方法，这边默认调用处理器的方法叫handleMessage
        return new MessageListenerAdapter(receiver, "handleMessage");
    }

}
