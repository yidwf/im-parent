package com.yesido.im.ptoto.model.entity;

import org.springframework.data.mongodb.core.index.Indexed;


/**
 * 群成员信息
 * 
 * @author yesido
 * @date 2019年8月27日 下午3:09:01
 */
public class GroupMember {

    @Indexed
    private String account;

    private String name;

    private String icon;

    private String joinTime;

    private Long joinTimeMillis;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(String joinTime) {
        this.joinTime = joinTime;
    }

    public Long getJoinTimeMillis() {
        return joinTimeMillis;
    }

    public void setJoinTimeMillis(Long joinTimeMillis) {
        this.joinTimeMillis = joinTimeMillis;
    }

    public GroupMember() {
        super();
    }

    public GroupMember(String account, String name, String icon) {
        super();
        this.account = account;
        this.name = name;
        this.icon = icon;
    }

    public GroupMember simple() {
        GroupMember simple = new GroupMember();
        simple.setAccount(account);
        simple.setName(name);
        return simple;
    }

}
