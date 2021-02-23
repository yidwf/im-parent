package com.yesido.im.restful.startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.yesido.lib.utils.BizUtil;
import com.yesido.lib.utils.IDUtil;
import com.yesido.zookeeper.balance.BalanceService;

/**
 * 启动执行
 * 
 * @author yesido
 * @date 2019年8月23日 下午3:40:21
 */
@Component
@Order(value = 1)
public class StartRunner implements CommandLineRunner {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private BalanceService balanceService;

    @Override
    public void run(String... args) throws Exception {
        balanceService.start();

        long processId = Math.abs(BizUtil.getLocalIp().hashCode()) % ((1 << 10) - 1);
        IDUtil.init(processId);
        logger.info("全局ID生成器初始化，分配进程ID：{}，测试生成：{}", processId, IDUtil.nextLongId());
    }
}
