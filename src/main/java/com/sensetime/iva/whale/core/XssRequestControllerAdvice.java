package com.sensetime.iva.whale.core;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

/**
 * @Description TODO
 * @Date 2022/1/12
 * @Created by helin
 */
@RestControllerAdvice
public class XssRequestControllerAdvice implements RequestBodyAdvice {
    private static final Logger log = LoggerFactory.getLogger(XssRequestControllerAdvice.class);
    private final XssCleaner xssCleaner;

    public XssRequestControllerAdvice(XssCleaner xssCleaner){
        this.xssCleaner = xssCleaner;
    }
    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return methodParameter.hasParameterAnnotation(RequestBody.class);
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        return new HttpInputMessage() {
            @Override
            public InputStream getBody() throws IOException {
                String bodyStr = IOUtils.toString(inputMessage.getBody(),"utf-8");
                log.info("data before xss not processed：{}",bodyStr);
                if (StringUtils.hasText(bodyStr) && XssHolder.isEnabled()) {
                    bodyStr = xssCleaner.clean(bodyStr);
                }
                return IOUtils.toInputStream(bodyStr,"utf-8");
            }

            @Override
            public HttpHeaders getHeaders() {
                return null;
            }
        };
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }
}
