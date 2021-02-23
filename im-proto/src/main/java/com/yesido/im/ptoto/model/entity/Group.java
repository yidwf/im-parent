package com.yesido.im.ptoto.model.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 群组
 * 
 * @author yesido
 * @date 2019年8月27日 下午3:08:45
 */
@Document(collection = "group")
public class Group {

    @Id
    private String id; // 群ID

    private String name; // 群名

    @Indexed
    private String owner; // 群主

    private GroupMember ownerDetail;

    private Integer memberCount; // 群人数

    private List<GroupMember> members; // 群成员，含群主

    private int type; // 群组类型, 1=普通聊天群

    private String createTime;

    private long createTimeMillis; //创建时间毫秒值

    private String icon; // 群图标

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public GroupMember getOwnerDetail() {
        return ownerDetail;
    }

    public void setOwnerDetail(GroupMember ownerDetail) {
        this.ownerDetail = ownerDetail;
    }

    public Integer getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(Integer memberCount) {
        this.memberCount = memberCount;
    }

    public List<GroupMember> getMembers() {
        return members;
    }

    public void setMembers(List<GroupMember> members) {
        this.members = members;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public long getCreateTimeMillis() {
        return createTimeMillis;
    }

    public void setCreateTimeMillis(long createTimeMillis) {
        this.createTimeMillis = createTimeMillis;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

}
