package com.yesido.im.server.model.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private String id;

    private String createTime;

    private String account;

    private String password;

    private String name;

    private String phone;

    private String icon;

    private int sex;

    private int userType;

    private int status;

    private String remark;

    private String lastLoginTime;

    private Map<String, String> attachment;

    public User(String id, String account, int sex, Date date) {
        this.id = id;
        this.account = account;
        this.sex = sex;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public Map<String, String> getAttachment() {
        return attachment;
    }

    public void setAttachment(Map<String, String> attachment) {
        this.attachment = attachment;
    }

    public User(String id, String account, String password, String name) {
        super();
        this.id = id;
        this.account = account;
        this.password = password;
        this.name = name;
    }

    public User() {
        super();
    }

    public User addAttachment(String key, String value) {
        if (this.attachment == null) {
            this.attachment = new HashMap<>();
        }
        this.attachment.put(key, value);
        return this;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", account=" + account + ", password=" + password + ", name=" + name + ", phone="
                + phone + ", icon=" + icon + ", sex=" + sex + ", userType=" + userType + ", status=" + status
                + ", remark=" + remark + ", createTime=" + createTime + ", lastLoginTime=" + lastLoginTime
                + ", attachment=" + attachment + "]";
    }

}
