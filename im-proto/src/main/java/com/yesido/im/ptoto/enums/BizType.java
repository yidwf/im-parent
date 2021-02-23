package com.yesido.im.ptoto.enums;

/**
 * 业务类型
 * 
 * @author yesido
 * @date 2019年8月29日 下午6:20:41
 */
public enum BizType {

    /** --群组通知推送-- **/
    GROUP_CREATE(101, "群组创建"),
    GROUP_REMOVE_MEMBER(102, "移除群成员"),
    GROUP_DELETE(103, "群组解散"),
    GROUP_MEMBER_EXIT(104, "群成员退群"),
    GROUP_MEMBER_INVITE(105, "邀请用户加入群聊"),
    /** --群组通知推送-- **/
    CHAT2(201, "聊天"),
    ;
    private int type;
    private String value;

    private BizType(int type, String value) {
        this.type = type;
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static BizType get(int type) {
        BizType[] values = BizType.values();
        for (BizType value : values) {
            if (type == value.getType()) {
                return value;
            }
        }
        return null;
    }
}
