package com.yesido.im.ptoto.enums;

/**
 * 请求类型
 * 
 * @author yesido
 * @date 2017年5月16日 下午4:16:53
 */
public enum HeaderCode_Bak {
    /** --收到什么就发送什么，不做处理-- **/
    NOT_DEAL(100, "不做处理"),

    /** --登录请求-- **/
    LOGIN_REQ(101, "登录请求"),
    /** --登录响应-- **/
    LOGIN_RES(102, "登录响应"),
    /** --重复登录-- **/
    LOGIN_REPEAT(103, "重复登录"),
    /** --心跳请求-- **/
    HEART_BEAT_REQ(104, "心跳请求"),
    /** --心跳响应-- **/
    HEART_BEAT_RES(105, "心跳响应"),
    /** --单聊请求-- **/
    CHAT_SIMPLE_REQ(201, "单聊请求"),
    /** --单聊响应-- **/
    CHAT_SIMPLE_RES(202, "单聊响应"),
    /** --群聊请求-- **/
    CHAT_GROUP_REQ(203, "群聊请求"),
    /** --群聊响应-- **/
    CHAT_GROUPE_RES(204, "群聊响应"),

    /** --单点推送数据请求-- **/
    SIMPLE_PUSH_REQ(106, "单点推送数据请求"),
    /** --推送数据响应-- **/
    PUSH_RES(107, "推送数据响应"),


    /** --推送应答请求-- **/
    PUSH_ANSWER_REQ(108, "推送应答请求"),
    /** --写应答给发送人，表示请求已经处理-- **/
    REQUEST_ANSWER(200, "写应答给发送人，表示请求已经处理");

    private HeaderCode_Bak(int code, String value) {
        this.code = code;
        this.value = value;
    }

    private int code;
    private String value;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static HeaderCode_Bak get(int code) {
        HeaderCode_Bak[] values = HeaderCode_Bak.values();
        for (HeaderCode_Bak value : values) {
            if (code == value.getCode()) {
                return value;
            }
        }
        return null;
    }
}
