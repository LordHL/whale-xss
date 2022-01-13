package com.sensetime.iva.whale.utils;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;

/**
 * @Description TODO
 * @Date 2021/12/24
 * @Created by helin
 */
public class ClassUtil extends ClassUtils {
    private ClassUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    @Nullable
    public static <A extends Annotation> A getAnnotation(HandlerMethod handlerMethod, Class<A> annotationType) {
        A annotation = handlerMethod.getMethodAnnotation(annotationType);
        if (null != annotation) {
            return annotation;
        } else {
            Class<?> beanType = handlerMethod.getBeanType();
            return AnnotatedElementUtils.findMergedAnnotation(beanType, annotationType);
        }
    }


}
