package com.yesido.websocket.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class BaseErrorController implements ErrorController {
    private static final Logger logger = LoggerFactory.getLogger(BaseErrorController.class);

    @Override
    public String getErrorPath() {
        logger.info("出错啦！进入自定义错误控制器");
        return "/error";
    }

    @RequestMapping("/error")
    public String errorPage() {
        return getErrorPath();
    }

    @RequestMapping("/404")
    public String pageNotFound() {
        return "/404";
    }

    @RequestMapping("/401")
    public String unauthorized() {
        return "/401";
    }


    /*@Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
    
        return new EmbeddedServletContainerCustomizer() {
    
            @Override
            public void customize(ConfigurableEmbeddedServletContainer container) {
                ErrorPage error401Page = new ErrorPage(HttpStatus.UNAUTHORIZED,
                        "/401");
                ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND,
                        "/404");
                ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error");
                container.addErrorPages(error401Page, error404Page, error500Page);
            }
        };
    }*/

}
