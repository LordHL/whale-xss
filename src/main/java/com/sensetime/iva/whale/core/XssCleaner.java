package com.sensetime.iva.whale.core;

/**
 * @Description TODO
 * @Date 2021/12/24
 * @Created by helin
 */
public interface XssCleaner {
    /**
     *  清除Xss方法
     * @param var1
     * @return 清理后的数据
     */
    String clean(String var1);
}
