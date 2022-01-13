package com.sensetime.iva.whale.core;

import com.sensetime.iva.whale.utils.XssUtil;

/**
 * @Description 默认的xss清理器
 * @Date 2021/12/24
 * @Created by helin
 */
public class DefaultXssCleaner implements XssCleaner {
    public String clean(String var1) {
        return XssUtil.cleanXss(var1);
    }
}
