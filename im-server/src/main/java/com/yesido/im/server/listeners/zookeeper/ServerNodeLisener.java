package com.yesido.im.server.listeners.zookeeper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.yesido.im.server.model.ServiceNode;

/**
 * 服务器节点注册监听器
 * 
 * @author yesido
 * @date 2019年8月13日 下午4:07:21
 */
@Component
@Deprecated
public class ServerNodeLisener implements TreeCacheListener {
    Logger logger = LoggerFactory.getLogger(getClass());

    public static Map<String, ServiceNode> serviceNodes = new ConcurrentHashMap<>();

    @Override
    public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
        String data = null;
        ServiceNode node = null;
        String path = null;
        switch (event.getType()) {
            case NODE_ADDED:
                path = event.getData().getPath();
                if (path != null && path.equals("/services")) {
                    return;
                }
                data = new String(event.getData().getData());
                logger.info("新增节点：{}，路径path：{}，数据：{}", event.getType(), event.getData().getPath(), data);
                node = JSONObject.parseObject(data, ServiceNode.class);
                serviceNodes.put(node.getServerIp(), node);
                break;
            case NODE_UPDATED:
                data = new String(event.getData().getData());
                logger.info("修改节点：{}，路径path：{}，数据：{}", event.getType(), event.getData().getPath(), data);
                node = JSONObject.parseObject(data, ServiceNode.class);
                serviceNodes.put(node.getServerIp(), node);
                break;
            case NODE_REMOVED:
                data = new String(event.getData().getData());
                logger.info("删除节点：{}，路径path：{}，数据：{}", event.getType(), event.getData().getPath(), data);
                node = JSONObject.parseObject(data, ServiceNode.class);
                serviceNodes.remove(node.getServerIp());
                break;
            default:
                logger.debug("未处理事件：{}", event.getType(), JSONObject.toJSONString(event));
                break;
        }
    }
}
