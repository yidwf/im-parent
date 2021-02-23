package com.yesido.im.ptoto.netty;

import java.util.List;

import com.yesido.im.ptoto.model.entity.GroupMember;

/**
 * 推送数据
 * 
 * @author yesido
 * @date 2019年9月3日 上午10:38:21
 */
public final class PushMessage extends NettyMessage {

    private String targetId; // 目标
    private List<GroupMember> members; // 推送用户

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public List<GroupMember> getMembers() {
        return members;
    }

    public void setMembers(List<GroupMember> members) {
        this.members = members;
    }

}
