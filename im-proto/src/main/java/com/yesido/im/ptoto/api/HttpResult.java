package com.yesido.im.ptoto.api;

/**
 * 响应结果
 * 
 * @author yesido
 * @date 2019年8月23日 上午11:38:49
 */
public class HttpResult {

    /** 响应码 **/
    private int code;
    /** 响应提示信息 **/
    private String msg;
    /** 响应数据 **/
    private Object data;

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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public HttpResult() {}

    public HttpResult(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 成功响应：无返回数据
     * 
     * @return HttpResult
     */
    public static HttpResult ok() {
        return ok(null);
    }

    /**
     * 成功响应：并返回数据
     * 
     * @param data 自定义数据
     * @return HttpResult
     */
    public static HttpResult ok(Object data) {
        return result(HttpCode.OK_0, data);
    }

    /**
     * 成功响应：并返回自定义提示信息
     * 
     * @param msg 自定义提示信息
     * @return HttpResult
     */
    public static HttpResult okWithMsg(String msg) {
        return result(HttpCode.OK_200, msg, null);
    }

    /**
     * 成功响应：并返回自定义提示信息和数据
     * 
     * @param msg 自定义提示信息
     * @param data 自定义数据
     * @return HttpResult
     */
    public static HttpResult okWithMsg(String msg, Object data) {
        return result(HttpCode.OK_200, msg, data);
    }

    /**
     * 失败响应
     * 
     * @param msg 错误提示信息
     * @return HttpResult
     */
    public static HttpResult fail(String msg) {
        return result(HttpCode.FAIL_400, msg);
    }

    /**
     * 失败响应
     * 
     * @param msg 错误提示信息
     * @param data 自定义数据
     * @return
     */
    public static HttpResult fail(String msg, Object data) {
        return result(HttpCode.FAIL_400, msg, data);
    }

    /**
     * 不存在资源
     */
    public static HttpResult notFound() {
        return result(HttpCode.NOT_FOUND, HttpCode.NOT_FOUND.getMsg(), null);
    }

    /**
     * 未授权
     */
    public static HttpResult unauthorized() {
        return result(HttpCode.UNAUTHORIZED, HttpCode.UNAUTHORIZED.getMsg(), null);
    }

    /**
     * 验证失败响应
     * 
     * @param msg 错误提示信息
     * @return HttpResult
     */
    public static HttpResult validatorError(String msg) {
        return result(HttpCode.VALIDATOR_ERROR_504, msg);
    }

    /**
     * 验证失败响应
     */
    public static HttpResult validatorError() {
        HttpCode httpCode = HttpCode.VALIDATOR_ERROR_504;
        return result(httpCode, httpCode.getMsg());
    }

    /**
     * 服务器异常响应
     * 
     * @param msg 错误提示信息
     * @return HttpResult
     */
    public static HttpResult serverError(String msg) {
        return result(HttpCode.SERVER_ERROR_500, msg);
    }

    /**
     * 服务器异常响应
     */
    public static HttpResult serverError() {
        return result(HttpCode.SERVER_ERROR_500, HttpCode.SERVER_ERROR_500.getMsg());
    }

    private static HttpResult result(HttpCode httpCode, String msg) {
        return result(httpCode, msg, null);
    }

    private static HttpResult result(HttpCode httpCode, Object data) {
        return result(httpCode, null, data);
    }

    private static HttpResult result(HttpCode httpCode, String msg, Object data) {
        return new HttpResult(httpCode.getCode(), msg, data);
    }

}
