package com.yesido.im.restful.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yesido.im.ptoto.api.HttpResult;
import com.yesido.im.ptoto.model.entity.Group;
import com.yesido.im.ptoto.model.entity.GroupMember;
import com.yesido.im.restful.consts.Constant;
import com.yesido.im.restful.model.dto.GroupMemberModel;
import com.yesido.im.restful.model.dto.GroupModel;
import com.yesido.im.restful.model.dto.InviteGroupMembersModel;
import com.yesido.im.restful.service.GroupService;
import com.yesido.im.restful.service.LoginService;
import com.yesido.im.restful.utils.date.DateUtils;
import com.yesido.lib.utils.IDUtil;

/**
 * 群组相关
 * 
 * @author yesido
 * @date 2019年8月27日 上午10:05:41
 */
@Validated
@RestController
@RequestMapping(Constant.HTTP_URI_PREFIX + "/group")
public class GroupController {
    @Autowired
    private GroupService groupService;
    @Autowired
    private LoginService loginService;

    /**
     * 创建群组
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public HttpResult createGroup(@RequestBody @Validated GroupModel model) {
        Group group = new Group();
        group.setCreateTime(DateUtils.format(new Date()));
        group.setCreateTimeMillis(System.currentTimeMillis());
        group.setId(IDUtil.nextId());
        group.setName(model.getGroupName());
        group.setType(model.getGroupType() == null ? 0 : model.getGroupType());
        group.setIcon(model.getGroupIcon());
        List<GroupMember> members = new ArrayList<GroupMember>();

        // 解析群主
        GroupMemberModel ownerModel = model.getOwner();
        String owner = ownerModel.getAccount();
        GroupMember ownerMember = toGroupMember(ownerModel);
        group.setOwner(owner);
        group.setOwnerDetail(ownerMember);
        members.add(ownerMember);

        // 解析群成员
        List<GroupMemberModel> memberModels = model.getMembers();
        for (GroupMemberModel memberModel : memberModels) {
            if (owner.equals(memberModel.getAccount())) {
                continue;
            }
            GroupMember member = toGroupMember(memberModel);
            members.add(member);
        }
        if (members.size() < 3) {
            return HttpResult.fail("群成员至少要两个");
        }
        group.setMembers(members);
        group.setMemberCount(members.size());
        groupService.saveGroup(group);
        return HttpResult.ok(group);
    }

    private GroupMember toGroupMember(GroupMemberModel model) {
        String createTime = DateUtils.format(new Date());
        long createTimeMillis = System.currentTimeMillis();
        GroupMember member = new GroupMember(model.getAccount(), model.getName(), model.getIcon());
        member.setJoinTime(createTime);
        member.setJoinTimeMillis(createTimeMillis);
        return member;
    }

    /**
     * 获取一个用户参与的群组
     */
    @RequestMapping(value = "/member_join/list", method = RequestMethod.POST)
    public HttpResult getMemberJoinGroup(@NotBlank(message = "member不能为空") String member) {
        return HttpResult.ok(groupService.findMemberJoinGroup(member));
    }

    /**
     * 获取群组详情
     */
    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    public HttpResult detail(@NotBlank(message = "groupId不能为空") String groupId) {
        Group group = groupService.findByGroupId(groupId);
        if (group == null) {
            return HttpResult.fail("群组不存在：" + groupId);
        }

        return HttpResult.ok(groupService.findByGroupId(groupId));
    }

    /**
     * 删除群组
     * 
     * @param groupId 群组ID
     * @return
     */
    @RequestMapping(value = "/owner/delete", method = RequestMethod.POST)
    public HttpResult delete(@NotBlank(message = "groupId不能为空") String groupId) {
        groupService.deleteGroup(groupId);
        return HttpResult.ok();
    }

    /**
     * 移除群成员
     * 
     * @param groupId 群组ID
     * @param account 成员账号
     * @return
     */
    @RequestMapping(value = "/remove/member", method = RequestMethod.POST)
    public HttpResult removeMember(@NotBlank(message = "groupId不能为空") String groupId,
            @NotBlank(message = "member不能为空") String member) {
        groupService.removeMember(groupId, member);
        return HttpResult.ok();
    }

    /**
     * 成员退群
     * 
     * @param groupId 群组ID
     * @param account 成员账号
     */
    @RequestMapping(value = "/member/exit", method = RequestMethod.DELETE)
    public HttpResult memberExitGroup(@NotBlank(message = "groupId不能为空") String groupId) {
        String member = loginService.getLoginUserAccount();
        if (StringUtils.isBlank(member)) {
            return HttpResult.unauthorized();
        }
        groupService.memberExitGroup(groupId, member);
        return HttpResult.ok();
    }

    /**
     * 邀请群成员
     * 
     * @param groupId
     * @param members
     */
    @RequestMapping(value = "/invite/members", method = RequestMethod.POST)
    public HttpResult inviteMembers(@RequestBody @Validated InviteGroupMembersModel model) {
        String inviteAccount = loginService.getLoginUserAccount();
        if (StringUtils.isBlank(inviteAccount)) {
            return HttpResult.unauthorized();
        }
        List<GroupMemberModel> memberModels = model.getMembers();
        List<GroupMember> newMembers = memberModels.stream().map(r -> toGroupMember(r)).collect(Collectors.toList());
        groupService.inviteMembers(model.getGroupId(), inviteAccount, newMembers);
        return HttpResult.ok();
    }

}

