package com.yesido.im.server.model.entity;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.yesido.im.ptoto.enums.BizType;
import com.yesido.im.ptoto.enums.HeaderCode;
import com.yesido.lib.utils.BizUtil;

/**
 * 消息基础类
 * 
 * @author yesido
 * @date 2019年9月4日 上午10:07:29
 */
public class Message {

    /** ---请求类型：{@link HeaderCode}-- **/
    private int code;

    /** --具体业务类型：{@link BizType}-- **/
    private Integer bizType;

    /** --媒体类型：0=文本，1=图片，2=语音，3=文件-- **/
    private Integer contentType;

    private String content; // 内容

    private Map<String, String> attachment; // 附加信息

    private boolean isRead; // 是否已读

    private Long createtime; // 创建时间戳

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    public Long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Long createtime) {
        this.createtime = createtime;
    }

    public Integer getBizType() {
        return bizType;
    }

    public void setBizType(Integer bizType) {
        this.bizType = bizType;
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

    public Map<String, String> getAttachment() {
        return attachment;
    }

    public void setAttachment(Map<String, String> attachment) {
        this.attachment = attachment;
    }

    /**
     * json字符串
     */
    public String jsonString() {
        return JSONObject.toJSONString(this);
    }

    /**
     * 附加信息添加
     */
    public Message addAttachment(String key, Object value) {
        if (this.attachment == null) {
            this.attachment = new HashMap<>();
        }
        this.attachment.put(key, BizUtil.string(value));
        return this;
    }
}
