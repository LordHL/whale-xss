package com.sensetime.iva.whale.core;

import java.lang.annotation.*;

/**
 * @Description 忽略 xss
 * @Date 2021/12/24
 * @Created by helin
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XssCleanIgnore {
}
