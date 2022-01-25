package com.sensetime.iva.whale.config;

import com.sensetime.iva.whale.core.XssCleaner;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @Description 重写 requestBody 参数
 * @Date 2022/1/18
 * @Created by helin
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private static final Logger log = LoggerFactory.getLogger(XssHttpServletRequestWrapper.class);
    private final XssCleaner xssCleaner;

    public XssHttpServletRequestWrapper(HttpServletRequest request, XssCleaner xssCleaner) {
        super(request);
        this.xssCleaner = xssCleaner;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        String bodyStr = IOUtils.toString(super.getInputStream(), "utf-8");
        log.info("zuul body in");
        if (StringUtils.hasText(bodyStr)) {
            bodyStr = xssCleaner.clean(bodyStr);
            log.info("zuul  filter bodyStr : {}", bodyStr);
        }
        final byte[] bodyBytes = bodyStr.getBytes(StandardCharsets.UTF_8);
        return new ServletInputStream() {
            private int lastIndexRetrieved = -1;
            private ReadListener readListener = null;

            @Override
            public boolean isFinished() {
                return (lastIndexRetrieved == bodyBytes.length-1);
            }

            @Override
            public boolean isReady() {
                return isFinished();
            }
            @Override
            public void setReadListener(ReadListener readListener) {
                this.readListener = readListener;
                if (!isFinished()) {
                    try {
                        readListener.onDataAvailable();
                    } catch (IOException e) {
                        readListener.onError(e);
                    }
                } else {
                    try {
                        readListener.onAllDataRead();
                    } catch (IOException e) {
                        readListener.onError(e);
                    }
                }
            }

            @Override
            public int read() throws IOException {
                int i;
                if (!isFinished()) {
                    i = bodyBytes[lastIndexRetrieved+1];
                    lastIndexRetrieved++;
                    if (isFinished() && (readListener != null)) {
                        try {
                            readListener.onAllDataRead();
                        } catch (IOException ex) {
                            readListener.onError(ex);
                            throw ex;
                        }
                    }
                    return i;
                } else {
                    return -1;
                }
            }
        };
    }
}
