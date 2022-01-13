package com.sensetime.iva.whale.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description TODO
 * @Date 2021/12/24
 * @Created by helin
 */
@ConfigurationProperties("whale.xss")
public class XssProperties {
    /**
     * 开启xss
     */
    private boolean enabled = true;
    /**
     * 拦截的路由，默认为空
     */
    private List<String> pathPatterns = new ArrayList();
    /**
     * 放行的规则，默认为空
     */
    private List<String> excludePatterns = new ArrayList();

    public boolean isEnabled() {
        return this.enabled;
    }

    public List<String> getPathPatterns() {
        return this.pathPatterns;
    }

    public List<String> getExcludePatterns() {
        return this.excludePatterns;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setPathPatterns(List<String> pathPatterns) {
        this.pathPatterns = pathPatterns;
    }

    public void setExcludePatterns(List<String> excludePatterns) {
        this.excludePatterns = excludePatterns;
    }
}
