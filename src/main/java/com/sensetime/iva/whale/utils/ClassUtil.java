package com.sensetime.iva.whale.utils;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public static void main(String[] args) {

        long currentTimeMillis = System.currentTimeMillis();
        System.out.println(currentTimeMillis);
        System.out.println(generateSign(currentTimeMillis + "","123456"));
    }
    private static String apiKey = "gns11529c136998cb6";

    public static String generateSign(String timestamp, String xNonce) {
        StringBuffer content = new StringBuffer();
        List<String> argList = new ArrayList<>();
        argList.add(apiKey);
        argList.add(timestamp);
        argList.add(xNonce);
        Collections.sort(argList);
        for (String arg : argList) {
            content.append(arg);
        }
        return encode(content.toString());
    }
    public static String encode(String str) {
        if (str == null) {
            return null;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            messageDigest.update(str.getBytes());
            return getFormattedText(messageDigest.digest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    private static String getFormattedText(byte[] bytes) {
        int len = bytes.length;
        StringBuilder buf = new StringBuilder(len * 2);
        // 把密文转换成十六进制的字符串形式
        for (int j = 0; j < len; j++) {
            buf.append(HEX[(bytes[j] >> 4) & 0x0f]);
            buf.append(HEX[bytes[j] & 0x0f]);
        }
        return buf.toString();
    }
    private static final char[] HEX = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
}
