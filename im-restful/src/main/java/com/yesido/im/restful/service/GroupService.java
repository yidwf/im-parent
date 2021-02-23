package com.yesido.im.restful.service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yesido.im.ptoto.enums.BizType;
import com.yesido.im.ptoto.model.entity.Group;
import com.yesido.im.ptoto.model.entity.GroupMember;
import com.yesido.im.ptoto.netty.NettyMessage;
import com.yesido.im.ptoto.utils.NettyMessageUtil;
import com.yesido.im.restful.exception.BizException;
import com.yesido.mongodb.base.QueryParam;
import com.yesido.mongodb.service.MongoDBService;
import com.yesido.rabbitmq.decalre.ImServerDeclare;
import com.yesido.rabbitmq.sender.RabbitSender;

/**
 * 群组service
 * 
 * @author yesido
 * @date 2019年8月27日 上午10:06:49
 */
@Service
public class GroupService {
    Logger logger = LoggerFactory.getLogger(GroupService.class);

    @Autowired
    private MongoDBService mongoDBService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private RabbitSender sender;

    public void saveGroup(Group group) {
        mongoDBService.save(group);
        // 通知成群成员
        List<GroupMember> members = group.getMembers().stream().filter(r -> !r.getAccount().equals(group.getOwner())).collect(Collectors.toList());
        String content = group.getOwnerDetail().getName() + "邀请你加入群聊";
        NettyMessage msg = NettyMessageUtil.buildGroupPushMsgSimple(BizType.GROUP_CREATE, group.getId(), content, members);
        sender.sendMessage(ImServerDeclare.EX_TOPIC_IM_SERVER, ImServerDeclare.RK_IM_SERVER_MSG_PUSH, msg);
    }

    /**
     * 获取成员加入的群
     */
    public List<Group> findMemberJoinGroup(String account) {
        QueryParam queryParam = new QueryParam();
        queryParam.addIsQueryParam("members.account", account);
        List<Group> result = mongoDBService.findList(Group.class, queryParam);
        return result;
    }

    /**
     * 获取群组详情
     */
    public Group findByGroupId(String groupId) {
        return mongoDBService.get(groupId, Group.class);
    }

    /**
     * 操作之前做检测
     * 
     * @param loginAccount 登录用户 || 操作用户
     * @param groupId 群id
     * @param mustOwner 必须是群主才能操作
     * @param mustGroupMember 必须是群内用户才能操作
     * @return Group
     */
    private Group checkBefore(String loginAccount, String groupId, boolean mustOwner, boolean mustGroupMember) {
        Group group = findByGroupId(groupId);
        if (group == null) {
            throw new BizException("群组不存在");
        }
        if (mustOwner && !group.getOwner().equals(loginAccount)) {
            throw new BizException("只有群主才能操作");
        }
        if (mustGroupMember) {
            Iterator<GroupMember> iterator = group.getMembers().iterator();
            GroupMember inviteMember = null;
            while (iterator.hasNext()) {
                GroupMember next = iterator.next();
                if (loginAccount.equals(next.getAccount())) {
                    inviteMember = next;
                }
            }
            if (inviteMember == null) {
                throw new BizException("不是群内用户不能操作");
            }
        }
        return group;
    }

    /**
     * 删除群组
     */
    public void deleteGroup(String groupId) {
        String owner = loginService.getLoginUserAccount();
        Group group = checkBefore(owner, groupId, true, false);

        logger.info("群主({})解散群组：{}", owner, JSONObject.toJSONString(group));
        mongoDBService.delete(groupId, Group.class);

        // 通知群成员
        String content = group.getOwnerDetail().getName() + "解散了群聊";
        List<GroupMember> members = group.getMembers().stream().filter(r -> !r.getAccount().equals(group.getOwner())).collect(Collectors.toList());
        NettyMessage msg = NettyMessageUtil.buildGroupPushMsgSimple(BizType.GROUP_DELETE, group.getId(), content, members);
        sender.sendMessage(ImServerDeclare.EX_TOPIC_IM_SERVER, ImServerDeclare.RK_IM_SERVER_MSG_PUSH, msg);
    }

    /**
     * 群主移除群成员
     * 
     * @param groupId 群组ID
     * @param account 群成员
     */
    public void removeMember(String groupId, String account) {
        String owner = loginService.getLoginUserAccount();
        if (owner.equals(account)) {
            throw new BizException("不能移除自己");
        }
        Group group = checkBefore(owner, groupId, true, false);
        Iterator<GroupMember> iterator = group.getMembers().iterator();
        GroupMember member = null;
        while (iterator.hasNext()) {
            GroupMember next = iterator.next();
            if (!account.equals(next.getAccount())) {
                continue;
            }
            member = next;
            iterator.remove();
        }
        if (member == null) {
            logger.info("[移除群成员]成员不是群内用户：{}-->{}", groupId, account);
            return;
        }
        logger.info("[移除群成员]群主({})移除了成员：{}-->{}", owner, groupId, JSONObject.toJSONString(member));
        saveGroup(group);

        // 通知群成员
        String content = group.getOwnerDetail().getName() + "将你移除群聊";
        List<GroupMember> members = Lists.newArrayList(member);
        NettyMessage msg = NettyMessageUtil.buildGroupPushMsgSimple(BizType.GROUP_DELETE, group.getId(), content, members);
        sender.sendMessage(ImServerDeclare.EX_TOPIC_IM_SERVER, ImServerDeclare.RK_IM_SERVER_MSG_PUSH, msg);
    }

    public void memberExitGroup(String groupId, String account) {
        String owner = loginService.getLoginUserAccount();
        if (owner.equals(account)) {
            throw new BizException("群主请先转让群主再退出");
        }
        Group group = findByGroupId(groupId);
        if (group == null) {
            throw new BizException("群组不存在");
        }
        Iterator<GroupMember> iterator = group.getMembers().iterator();
        GroupMember member = null;
        while (iterator.hasNext()) {
            GroupMember next = iterator.next();
            if (!account.equals(next.getAccount())) {
                continue;
            }
            member = next;
            iterator.remove();
        }
        if (member == null) {
            logger.info("[群成员退群]成员不是群内用户：{}-->{}", groupId, account);
            return;
        }
        logger.info("[群成员退群]成员退出了群聊：{}-->{}", groupId, JSONObject.toJSONString(member));
        saveGroup(group);

        // 通知群主
        String content = member.getName() + "退出了群聊";
        List<GroupMember> members = Lists.newArrayList(group.getOwnerDetail());
        NettyMessage msg = NettyMessageUtil.buildGroupPushMsgSimple(BizType.GROUP_MEMBER_EXIT, group.getId(), content, members);
        sender.sendMessage(ImServerDeclare.EX_TOPIC_IM_SERVER, ImServerDeclare.RK_IM_SERVER_MSG_PUSH, msg);
    }

    public void inviteMembers(String groupId, String inviteAccount, List<GroupMember> newMembers) {
        Group group = checkBefore(inviteAccount, groupId, false, true);

        Map<String, GroupMember> memberMap = newMembers.stream().collect(Collectors.toMap(GroupMember::getAccount, GroupMember -> GroupMember));
        List<GroupMember> groupMembers = group.getMembers();
        Iterator<GroupMember> iterator = group.getMembers().iterator();
        GroupMember inviteMember = null;
        while (iterator.hasNext()) {
            GroupMember next = iterator.next();
            if (inviteAccount.equals(next.getAccount())) {
                inviteMember = next;
            }
            if (memberMap.containsKey(next.getAccount())) {
                logger.info("[邀请群成员]成员已经是群内用户：{}-->{}", groupId, JSONObject.toJSONString(next));
                memberMap.remove(next.getAccount());
            }
        }
        groupMembers.addAll(memberMap.values());

        logger.info("[邀请群成员]用户({})邀请人加入群聊：{}-->{}", inviteAccount, groupId, memberMap.values());
        saveGroup(group);

        // 通知群主
        String content = inviteMember.getName() + "邀请人加入群聊";
        List<GroupMember> members = Lists.newArrayList(group.getOwnerDetail());
        NettyMessage msg = NettyMessageUtil.buildGroupPushMsgSimple(BizType.GROUP_MEMBER_INVITE, group.getId(), content, members);
        sender.sendMessage(ImServerDeclare.EX_TOPIC_IM_SERVER, ImServerDeclare.RK_IM_SERVER_MSG_PUSH, msg);
    }
}
