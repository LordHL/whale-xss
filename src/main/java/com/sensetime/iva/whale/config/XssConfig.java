package com.sensetime.iva.whale.config;

import com.sensetime.iva.whale.core.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
//    @Bean
//    @ConditionalOnMissingBean
    public XssRequestControllerAdvice xssRequestControllerAdvice(XssCleaner xssCleaner) {
        return new XssRequestControllerAdvice(xssCleaner);
    }

    /**
     * 处理 requestParam请求参数
     * @param xssCleaner
     * @return
     */
//    @Bean
//    @ConditionalOnMissingBean
    public FormXssClean formXssClean(XssCleaner xssCleaner) {
        return new FormXssClean(xssCleaner);
    }

    @Bean
    @ConditionalOnMissingBean
    public XssZuulFilter xssZuulFilter(XssCleaner xssCleaner,XssProperties xssProperties){
        return new XssZuulFilter(xssCleaner,xssProperties);
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