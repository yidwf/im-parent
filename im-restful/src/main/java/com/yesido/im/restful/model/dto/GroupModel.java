package com.yesido.im.restful.model.dto;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Length;

/**
 * 群组模型
 * 
 * @author yesido
 * @date 2019年8月27日 上午11:47:57
 */
public class GroupModel {

    @Valid
    @NotNull(message = "群主信息不能为空")
    private GroupMemberModel owner;

    @Valid
    @NotEmpty(message = "群成员不能为空")
    @Size(min = 2, message = "群成员至少要两个")
    private List<GroupMemberModel> members;

    @Length(max = 10, message = "群名长度不能大于10")
    @NotBlank(message = "群名不能为空")
    private String groupName;

    private Integer groupType;

    private String groupIcon;

    public GroupMemberModel getOwner() {
        return owner;
    }

    public void setOwner(GroupMemberModel owner) {
        this.owner = owner;
    }

    public List<GroupMemberModel> getMembers() {
        return members;
    }

    public void setMembers(List<GroupMemberModel> members) {
        this.members = members;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getGroupType() {
        return groupType;
    }

    public void setGroupType(Integer groupType) {
        this.groupType = groupType;
    }

    public String getGroupIcon() {
        return groupIcon;
    }

    public void setGroupIcon(String groupIcon) {
        this.groupIcon = groupIcon;
    }

}
