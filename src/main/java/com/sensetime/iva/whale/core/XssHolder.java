package com.sensetime.iva.whale.core;

/**
 * @Description TODO
 * @Date 2021/12/24
 * @Created by helin
 */
public class XssHolder {
    private static final ThreadLocal<Boolean> TL = new ThreadLocal();

    public static boolean isEnabled() {
        return Boolean.TRUE.equals(TL.get());
    }

    public static void setEnable() {
        TL.set(Boolean.TRUE);
    }

    public static void remove() {
        TL.remove();
    }
}
