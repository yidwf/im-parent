package com.yesido.im.server.startup;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.yesido.im.server.consts.Parameter;
import com.yesido.im.server.context.ConnectionHolder;
import com.yesido.im.server.netty.NettyServer;
import com.yesido.im.server.service.ServerService;
import com.yesido.lib.utils.IDUtil;

@Component
@Order(value = 1)
public class StartRunner implements CommandLineRunner {
    Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private NettyServer nettyServer;
    @Autowired
    private ServerService serverService;

    @Override
    public void run(String... arg0) throws Exception {
        logger.info("springboot启动...");
        nettyServer.start();
        serverService.initServer();
        long processId = Math.abs(Parameter.getServerIp().hashCode()) % ((1 << 10) - 1);
        IDUtil.init(processId);
        logger.info("全局ID生成器初始化，分配进程ID：{}，测试生成：{}", processId, IDUtil.nextLongId());
        logger.info("本机域名：{}", Parameter.getServerIp());
    }

    @Scheduled(cron = "0 0/5 * * * ?")
    public void showInfo() {
        long currId = ConnectionHolder.instance().getAtomicLong().get();
        Map<String, Long> owners = ConnectionHolder.instance().getOwners();
        logger.info("最新sessionId：{}，当前连接数：{}", currId, owners.size());
    }

}
