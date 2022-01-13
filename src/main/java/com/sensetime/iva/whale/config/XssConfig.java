package com.sensetime.iva.whale.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.sensetime.iva.whale.core.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description TODO
 * @Date 2021/12/24
 * @Created by helin
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({XssProperties.class})
@ConditionalOnProperty(
        value = {"whale.xss.enabled"},
        havingValue = "true",
        matchIfMissing = true
)
public class XssConfig implements WebMvcConfigurer {
    private final XssProperties xssProperties;

    public XssConfig(XssProperties xssProperties) {
        this.xssProperties = xssProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public XssCleaner xssCleaner() {
        return new DefaultXssCleaner();
    }

    /**
     * fastjson 作为消息转换器序列化时 使用 XssRequestControllerAdvice 处理xss攻击
     * @return
     */
    @Bean
    public XssRequestControllerAdvice xssRequestControllerAdvice(XssCleaner xssCleaner) {
        return new XssRequestControllerAdvice(xssCleaner);
    }

    @Bean
    public FormXssClean formXssClean(XssCleaner xssCleaner) {
        return new FormXssClean(xssCleaner);
    }

//    @Bean
//    public Jackson2ObjectMapperBuilderCustomizer xssJacksonCustomizer(XssCleaner xssCleaner) {
//        return (builder) -> builder.deserializerByType(String.class, new JacksonXssClean(xssCleaner));
//    }

    public void addInterceptors(InterceptorRegistry registry) {
        List<String> patterns = this.xssProperties.getPathPatterns();
        if (patterns.isEmpty()) {
            patterns.add("/**");
        }

        XssCleanInterceptor interceptor = new XssCleanInterceptor(this.xssProperties);
        registry.addInterceptor(interceptor)
                .addPathPatterns(patterns)
                .excludePathPatterns(this.xssProperties.getExcludePatterns())
                .order(Ordered.LOWEST_PRECEDENCE);
    }
}