package com.yesido.im.ptoto.netty;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.yesido.im.ptoto.enums.BizType;
import com.yesido.im.ptoto.enums.HeaderCode;
import com.yesido.im.ptoto.enums.SocketType;

/**
 * 消息协议
 * 
 * @author yesido
 * @date 2018年9月1日
 */
public class NettyMessage {
    private String id;

    /** ---请求类型：{@link HeaderCode}-- **/
    private int code;

    /** --连接类型：{@link SocketType}-- **/
    private Integer socketType;

    /** --具体业务类型：{@link BizType}-- **/
    private Integer bizType;

    /** --创建时间：时间戳-- **/
    private Long createtime;

    /** --附加信息-- **/
    private Map<String, String> attachment;

    /** --密码或者私钥-- **/
    private String privateCode;

    /** --内容类型：0=文本，1=图片，2=语音，3=文件-- **/
    private Integer contentType;

    /** --消息内容-- **/
    private String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Integer getSocketType() {
        return socketType;
    }

    public void setSocketType(Integer socketType) {
        this.socketType = socketType;
    }

    public Integer getBizType() {
        return bizType;
    }

    public void setBizType(Integer bizType) {
        this.bizType = bizType;
    }

    public Long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Long createtime) {
        this.createtime = createtime;
    }

    public Map<String, String> getAttachment() {
        return attachment;
    }

    public void setAttachment(Map<String, String> attachment) {
        this.attachment = attachment;
    }

    public String getPrivateCode() {
        return privateCode;
    }

    public void setPrivateCode(String privateCode) {
        this.privateCode = privateCode;
    }

    public Integer getContentType() {
        return contentType;
    }

    public void setContentType(Integer contentType) {
        this.contentType = contentType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public NettyMessage addAttachment(String key, Object value) {
        if (this.attachment == null) {
            this.attachment = new HashMap<String, String>();
        }
        this.attachment.put(key, string(value));
        return this;
    }

    private String string(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return (String) value;
        } else {
            return JSONObject.toJSONString(value);
        }
    }

    public String toJsonString() {
        return JSONObject.toJSONString(this);
    }

    public String getAttachmentValue(String key) {
        if (attachment != null) {
            return attachment.get(key);
        }
        return null;
    }

    public int getAttachmentIntValue(String key) {
        Object val = getAttachmentValue(key);
        if (val == null) {
            return 0;
        }
        return Integer.valueOf(val.toString());
    }
}
