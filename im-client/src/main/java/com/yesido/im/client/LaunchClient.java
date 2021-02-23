package com.yesido.im.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.yesido.im.client.request.RequestManager;
import com.yesido.im.ptoto.netty.NettyMessage;

/**
 * 客户端启动类
 * 
 * @author yesido
 * @date 2019年8月7日 下午4:27:21
 */
@EnableScheduling
@SpringBootApplication
public class LaunchClient {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(LaunchClient.class, args);
        login();

    }

    public static void login() throws IOException {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        String account = "manager";
        LoginTask task = new LoginTask(account);
        executor.execute(task);

        String from = account;
        String to = "admin";
        if ("admin".equals(from)) {
            to = "manager";
        }
        NettyClent client = NettyClent.getClient();
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String msg = in.readLine();
        while (msg.indexOf("end") == -1) {
            NettyMessage text = RequestManager.buildSimpleChatText(msg, from, to);
            // to = "2017101410364172225960500001";
            // NettyMessage text = RequestManager.buildGroupChatText(msg, from, to, SocketType.ANDROID);
            client.writeAndFlush(text);
            msg = in.readLine();
        }
    }
}


class LoginTask implements Runnable {

    private String account;

    public LoginTask(String account) {
        super();
        this.account = account;
    }

    @Override
    public void run() {
        NettyClent client = NettyClent.getClient();
        client.setAccount(account);
        client.setPassword("123456");
        client.connect();
    }
}
