package com.sensetime.iva.whale.config;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.netflix.zuul.http.ServletInputStreamWrapper;
import com.sensetime.iva.whale.core.XssCleaner;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.util.StringUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @Description zuul 网关 处理xss
 * @Date 2022/1/19
 * @Created by helin
 */
//@Component
//@RefreshScope
public class XssZuulFilter extends ZuulFilter {

    private final static Logger log = LoggerFactory.getLogger(XssZuulFilter.class);
    private final XssCleaner xssCleaner;
    private final XssProperties xssProperties;

    public XssZuulFilter(XssCleaner xssCleaner,XssProperties xssProperties){
        this.xssCleaner = xssCleaner;
        this.xssProperties = xssProperties;
    }

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        //由于whale-common设置得是0，避免设置一样，故这里设置为1
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        if (validateUris(request)){
            //获取 RequestParam 类型参数
            try{
                Map<String, String[]> parameterMap = request.getParameterMap();
                if (parameterMap != null && !parameterMap.isEmpty()) {
                    //替换 RequestParam 参数
                    Map<String, List<String>> replaceParamMap = new HashMap<>();
                    Iterator iterator = parameterMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, String[]> var1 = (Map.Entry<String, String[]>) iterator.next();
                        String key = var1.getKey();
                        String[] values = var1.getValue();
                        if (values != null) {
                            for (int i = 0; i < values.length; i++) {
                                values[i] = xssCleaner.clean(values[i]);
                            }
                            replaceParamMap.put(key, Arrays.asList(values));
                        }
                    }

                    requestContext.setRequestQueryParams(replaceParamMap);
                }
                //处理 requestBody
                ServletInputStream inputStream = request.getInputStream();
                String bodyStr = IOUtils.toString(inputStream, "utf-8");

                if (StringUtils.hasText(bodyStr)) {
                    bodyStr = xssCleaner.clean(bodyStr);
                    final byte[] bodyBytes = bodyStr.getBytes(StandardCharsets.UTF_8);
                    requestContext.setRequest(new HttpServletRequestWrapper(request){
                        @Override
                        public ServletInputStream getInputStream() {
                            return new ServletInputStreamWrapper(bodyBytes);
                        }

                        @Override
                        public int getContentLength() {
                            return bodyBytes.length;
                        }

                        @Override
                        public long getContentLengthLong() {
                            return bodyBytes.length;
                        }

                    });
                }

            }catch (Exception e){
                e.printStackTrace();
                log.error("zuul xss error url:{},error info {}",request.getRequestURL(),e.getMessage());
            }
        }
        return null;
    }

    private boolean validateUris(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (!contextPath.equals("/")) {
            uri = uri.substring(contextPath.length());
        }
        if (uri.contains("\\.")) {
            uri = uri.substring(0, uri.lastIndexOf("\\."));
        }
        List<String> excludePatterns = xssProperties.getExcludePatterns();
        if (excludePatterns != null && !excludePatterns.isEmpty()) {
            for (int i = 0; i < excludePatterns.size(); i++) {
                if (uri.startsWith(excludePatterns.get(i))) {
                    log.debug ("url filter. {} start with {}", uri, excludePatterns.get(i));
                    return false;
                }
            }
        }

        return true;
    }
}
