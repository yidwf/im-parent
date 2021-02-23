package com.yesido.im.server.controller.rpc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yesido.im.server.consts.Constant;
import com.yesido.im.server.service.ServerService;
import com.yesido.rpc.proto.RPCResult;

/**
 * 
 * @author yesido
 * @date 2019年10月16日 下午5:31:24
 */
@RestController
@RequestMapping(Constant.RPC_URI_PREFIX + "/server")
public class RPCServerController {

    @Autowired
    private ServerService serverService;

    /**
     * 设置服务器停止
     */
    @RequestMapping(value = "/status/stop", method = RequestMethod.POST)
    public RPCResult<Boolean> stopSrever() {
        new Thread(() -> {
            serverService.stopSrever();
        }).start();
        return RPCResult.ok(Boolean.TRUE);
    }

    /**
     * 设置服务启动
     */
    @RequestMapping(value = "/status/start", method = RequestMethod.POST)
    public RPCResult<Boolean> preStopSrever() {
        new Thread(() -> {
            serverService.startSrever();
        }).start();
        return RPCResult.ok(Boolean.TRUE);
    }
}
