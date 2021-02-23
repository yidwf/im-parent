package com.yesido.im.ptoto.api;

/**
 * 响应码
 * 
 * @author yesido
 * @date 2019年8月23日 上午11:27:42
 */
public enum HttpCode {
    OK_0(0, "成功！"),
    OK_200(200, "成功！"), // 需要提示自定义信息
    FAIL_400(400, "失败！"),
    UNAUTHORIZED(401, "token失效或无权访问"),
    NOT_FOUND(404, "不存在的资源"),
    VALIDATOR_ERROR_504(504, "参数不合法！"),
    SERVER_ERROR_500(500, "网络繁忙，请稍后！"); // 服务器错误

    private int code;
    private String msg;

    private HttpCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
