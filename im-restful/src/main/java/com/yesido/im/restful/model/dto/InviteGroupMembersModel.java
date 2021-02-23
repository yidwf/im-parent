package com.yesido.im.restful.model.dto;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * 邀请群成员模型
 * 
 * @author yesido
 * @date 2019年9月3日 上午11:57:56
 */
public class InviteGroupMembersModel {

    @NotBlank(message = "groupId不能为空")
    private String groupId;

    @Valid
    @NotEmpty(message = "members不能为空")
    private List<GroupMemberModel> members;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public List<GroupMemberModel> getMembers() {
        return members;
    }

    public void setMembers(List<GroupMemberModel> members) {
        this.members = members;
    }

}
