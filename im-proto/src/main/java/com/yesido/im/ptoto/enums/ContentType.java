package com.yesido.im.ptoto.enums;

/**
 * 内容类型
 * 
 * @author yesido
 * @date 2019年8月7日 下午4:44:56
 */
public enum ContentType {

    TEXT(0, "文本"),
    IMAGE(1, "图片"),
    VEDIO(2, "语音"),
    FILE(3, "文件");

    private ContentType(int type, String value) {
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

    public static ContentType get(int type) {
        ContentType msgType = null;
        switch (type) {
            case 0:
                msgType = ContentType.TEXT;
                break;
            case 1:
                msgType = ContentType.IMAGE;
                break;
            case 2:
                msgType = ContentType.VEDIO;
                break;
            case 3:
                msgType = ContentType.FILE;
                break;
        }
        return msgType;
    }
}
