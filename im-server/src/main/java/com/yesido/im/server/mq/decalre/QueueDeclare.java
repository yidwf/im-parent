package com.yesido.im.server.mq.decalre;

import com.yesido.im.server.consts.Parameter;
import com.yesido.rabbitmq.decalre.ImServerDeclare;

/**
 * rabbitmq 常量定义 <br/>
 * 
 * <pre>
 * org.springframework.amqp.rabbit.annotation.Queue 参数说明：
 * value：队列名称
 * durable：队列是否持久化到数据库，true=表示持久化，服务崩溃也不会丢失队列，false=非持久化，服务重启队列丢失
 * autoDelete：设置为true的话若没有消费者订阅该队列，队列将被删除
 * </pre>
 * 
 * @author yesido
 * @date 2019年7月19日 上午10:35:51
 */
public class QueueDeclare extends ImServerDeclare {

    public static String getMsgRouteRoutingKey() {
        return Q_IM_SERVER_MSG_ROUTE + "." + Parameter.getServerIp();
    }

    public static String getMsgRouteQueue() {
        return Q_IM_SERVER_MSG_ROUTE + "." + Parameter.getServerIp();
    }

    public static String getMsgRouteRoutingKey(String ip) {
        return Q_IM_SERVER_MSG_ROUTE + "." + ip;
    }

    public static String getMsgRouteQueue(String ip) {
        return Q_IM_SERVER_MSG_ROUTE + "." + ip;
    }
}
