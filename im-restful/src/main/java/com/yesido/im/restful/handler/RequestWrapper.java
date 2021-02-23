package com.yesido.im.restful.handler;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.lang3.StringUtils;

/**
 * ServletRequest包装类<br>
 * ServletRequest的getReader()和getInputStream()两个方法只能被调用一次，而且不能两个都调用<br>
 * 注解@RequestBody是流的形式读取，那么流读了一次就没有了，其他地方就不能再次读取<br>
 * 使用RequestWrapper类包装ServletRequest，将流保存为byte[]，byte数组允许被多次读取，而不会丢失内容,<br>
 * 然后将getReader()和getInputStream()方法的流的读取指向byte[],
 * 
 * @author yesido
 * @date 2019年8月28日 上午11:12:16
 */
public class RequestWrapper extends HttpServletRequestWrapper {
    private final String body;

    public RequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        this.body = readBody(request);
    }

    private String readBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader();) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line.trim());
            }
        } catch (IOException e) {
            throw e;
        }
        return sb.toString();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream input = new ByteArrayInputStream(body.getBytes());
        return new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return input.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener listener) {

            }
        };
    }

    public boolean isBodyEmpty() {
        return StringUtils.isEmpty(body);
    }

    public String getBody() {
        return body;
    }

}
