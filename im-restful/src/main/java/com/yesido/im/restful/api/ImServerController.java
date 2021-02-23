package com.yesido.im.restful.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yesido.im.ptoto.api.HttpResult;
import com.yesido.im.restful.consts.Constant;
import com.yesido.zookeeper.balance.BalanceService;
import com.yesido.zookeeper.model.ServerData;

/**
 * 服务器连接
 * 
 * @author yesido
 * @date 2019年8月23日 上午11:17:44
 */
@RestController
@RequestMapping(Constant.HTTP_URI_PREFIX + "/im_server")
public class ImServerController {

    @Autowired
    private BalanceService balanceService;

    /**
     * 获取im连接服务器地址
     */
    @RequestMapping(value = "/conn_host/get")
    public HttpResult getServerHost() {
        ServerData result = balanceService.nextServer();
        if (result == null) {
            return HttpResult.fail("当前无服务器地址");
        }
        return HttpResult.ok(result);
    }
}
