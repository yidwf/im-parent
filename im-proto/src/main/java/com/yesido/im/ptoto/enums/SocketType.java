package com.yesido.im.ptoto.enums;

/**
 * 连接类型
 * 
 * @author yesido
 * @date 2017年5月16日 下午4:16:16
 */
public enum SocketType {

    WEBSOCKET(0, "websocket"),
    SOCKET(1, "socket"),

    // WEB(11, "web"),
    // IOS(12, "ios"),
    // ANDROID(21, "android")
    ;

    private SocketType(int type, String value) {
        this.type = type;
        this.value = value;
    }

    private int type;
    private String value;

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

    public static SocketType getSocketType(int type) {
        SocketType[] types = SocketType.values();
        for (SocketType socketType : types) {
            if (type == socketType.getType()) {
                return socketType;
            }
        }
        return null;
    }

}
