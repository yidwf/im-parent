package com.yesido.im.restful.model.dto;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 群成员模型
 * 
 * @author yesido
 * @date 2019年8月27日 上午11:48:13
 */
public class GroupMemberModel {

    @NotBlank(message = "账号不能为空")
    private String account;

    @NotBlank(message = "昵称不能为空")
    private String name;

    private String icon;

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

    public static void main(String[] args) {
        GroupMemberModel groupMember = new GroupMemberModel();
        groupMember.setAccount("admin");
        groupMember.setName("admin");
        groupMember.setIcon("default");
        System.out.println(JSONObject.toJSONString(groupMember));

        GroupMemberModel groupMember0 = new GroupMemberModel();
        groupMember0.setAccount("manager");
        groupMember0.setName("manager");
        groupMember0.setIcon("default");

        List<GroupMemberModel> list = new ArrayList<>();

        GroupMemberModel groupMember2 = new GroupMemberModel();
        groupMember2.setAccount("yesido");
        groupMember2.setName("yesido");
        groupMember2.setIcon("default");
        list.add(groupMember0);
        list.add(groupMember2);

        System.out.println(JSONArray.toJSONString(list));

    }
}
