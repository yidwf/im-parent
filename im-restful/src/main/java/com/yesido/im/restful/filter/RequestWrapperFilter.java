package com.yesido.im.restful.filter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.yesido.im.restful.handler.RequestWrapper;
import com.yesido.im.restful.utils.ServletRequestUtil;

/**
 * ServletRequest包装过滤器
 * 
 * @author yesido
 * @date 2019年8月28日 上午11:29:16
 */
@Configuration
public class RequestWrapperFilter implements Filter {
    Logger logger = LoggerFactory.getLogger(RequestWrapperFilter.class);
    @Value("${log.request.cost_time.error:3000}")
    private int errorTime;
    @Value("${log.request.cost_time.warn:1000}")
    private int warnTime;

    // 某些不需要打印耗时的url
    private Set<String> ignoreCosttimeLogUrls = new HashSet<>();

    @Value("${log.request.cost_time.ignore_urls:null}")
    public void setIgnoreCosttimeLogUrls(String ignoreUrls) {
        if (StringUtils.isBlank(ignoreUrls) || "null".equals(ignoreUrls.trim())) {
            return;
        }
        String[] urls = ignoreUrls.replaceAll("，", ",").split(",");
        for (String url : urls) {
            ignoreCosttimeLogUrls.add(url);
        }
    }


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String contentType = request.getContentType();
        long startTime = System.currentTimeMillis();
        if (request instanceof HttpServletRequest && "application/json".equals(contentType)) {
            // 注解@RequestBody是流的形式读取，那么流读了一次就没有了，其他地方就不能再次读取
            // 使用RequestWrapper类包装ServletRequest，将流保存为byte[]，byte数组允许被多次读取
            HttpServletRequest req = (HttpServletRequest) request;
            ServletRequest requestWrapper = new RequestWrapper(req);
            chain.doFilter(requestWrapper, response);
        } else {
            chain.doFilter(request, response);
        }
        long costTime = System.currentTimeMillis() - startTime;
        costTime(costTime, (HttpServletRequest) request);
    }

    private void costTime(long costTime, HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (noLog(uri)) {
            return;
        }
        String param = ServletRequestUtil.getParameter(request);
        if (costTime > errorTime) {
            logger.error("[耗时警告]耗时: {}毫秒，请求路径：{}，请求参数：{}", costTime, uri, param);
        } else if (costTime > warnTime) {
            logger.warn("[耗时警告]耗时: {}毫秒，请求路径：{}，请求参数：{}", costTime, uri, param);
        }
    }

    public boolean noLog(String uri) {
        return ignoreCosttimeLogUrls.contains(uri);
    }
}
