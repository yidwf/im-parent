package com.yesido.im.server.model;

/**
 * 登录用户
 * 
 * @author yesido
 */
public class LoginUser {

    private String account;
    private String loginIp;
    private Long logintime;

    public LoginUser() {
        super();
    }

    public LoginUser(String account, String loginIp, Long logintime) {
        super();
        this.account = account;
        this.loginIp = loginIp;
        this.logintime = logintime;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public Long getLogintime() {
        return logintime;
    }

    public void setLogintime(Long logintime) {
        this.logintime = logintime;
    }

}
