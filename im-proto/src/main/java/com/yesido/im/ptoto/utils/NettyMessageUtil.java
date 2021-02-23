package com.yesido.im.ptoto.utils;

import java.util.List;
import java.util.stream.Collectors;

import com.yesido.im.ptoto.enums.BizType;
import com.yesido.im.ptoto.enums.ContentType;
import com.yesido.im.ptoto.enums.HeaderCode;
import com.yesido.im.ptoto.model.entity.GroupMember;
import com.yesido.im.ptoto.netty.NettyMessage;
import com.yesido.im.ptoto.netty.PushMessage;

/**
 * 构造NettyMessage
 * 
 * @author yesido
 * @date 2019年8月29日 下午5:42:10
 */
public class NettyMessageUtil {

    public static NettyMessage newNettyMessage(HeaderCode code, ContentType type, String content) {
        NettyMessage msg = newNettyMessage(code);
        msg.setContent(content);
        msg.setContentType(type.getType());
        return msg;
    }

    public static NettyMessage newNettyMessage(HeaderCode code) {
        NettyMessage msg = new NettyMessage();
        msg.setCode(code.getCode());
        return msg;
    }

    /**
     * 群组通知
     * 
     * @param bizType 通知类型
     * @param groupId 群组id
     * @param content 提示信息
     * @param members 通知群成员
     * @return
     */
    public static PushMessage buildGroupPushMsg(BizType bizType, String groupId, String content, List<GroupMember> members) {
        PushMessage msg = new PushMessage();
        msg.setCode(HeaderCode.PUSH_MSG.getCode());
        msg.setBizType(bizType.getType());
        msg.setTargetId(groupId);
        msg.setMembers(members);
        msg.addAttachment("content", content);
        return msg;
    }

    /**
     * 群组通知（精简通知用户信息）
     * 
     * @param bizType 通知类型
     * @param groupId 群组id
     * @param content 提示信息
     * @param members 通知群成员
     * @return
     */
    public static PushMessage buildGroupPushMsgSimple(BizType bizType, String groupId, String content, List<GroupMember> members) {
        members = members.stream().map(r -> r.simple()).collect(Collectors.toList());
        return buildGroupPushMsg(bizType, groupId, content, members);
    }
}
