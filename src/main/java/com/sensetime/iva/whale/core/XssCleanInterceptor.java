package com.sensetime.iva.whale.core;

import com.sensetime.iva.whale.config.XssProperties;
import com.sensetime.iva.whale.utils.ClassUtil;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description TODO
 * @Date 2021/12/24
 * @Created by helin
 */
public class XssCleanInterceptor extends HandlerInterceptorAdapter {

    private final XssProperties xssProperties;

    public XssCleanInterceptor(XssProperties xssProperties) {
        this.xssProperties = xssProperties;
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        } else if (!this.xssProperties.isEnabled()) {
            return true;
        } else {
            HandlerMethod handlerMethod = (HandlerMethod)handler;
            XssCleanIgnore xssCleanIgnore = ClassUtil.getAnnotation(handlerMethod, XssCleanIgnore.class);
            if (xssCleanIgnore == null) {
                XssHolder.setEnable();
            }
            return true;
        }
    }

    public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        XssHolder.remove();
    }

}
