package com.yesido.im.ptoto.enums;

/**
 * 请求类型
 * 
 * @author yesido
 * @date 2018年9月23日
 */
public enum HeaderCode {

    /** --服务不可用-- **/
    SERVER_UNAVAILABLE(1),
    /** --登录-- **/
    LOGIN(101),
    /** --登出-- **/
    LOGOUT(102),
    /** --重复登录-- **/
    LOGIN_REPEAT(103),
    /** --心跳-- **/
    HEARTBEAT(104),
    /** --消息应答-- **/
    PUSH_MSG_ANSWER(105),
    /** --消息推送-- **/
    PUSH_MSG(106),
    /** --单聊-- **/
    CHAT_SIMPLE(201),

    /** --未知-- **/
    UN_KNOW(-1);

    HeaderCode(int code) {
        this.code = code;
    }

    private int code;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static HeaderCode get(int code) {
        HeaderCode[] values = HeaderCode.values();
        for (HeaderCode value : values) {
            if (code == value.getCode()) {
                return value;
            }
        }
        return null;
    }
}
