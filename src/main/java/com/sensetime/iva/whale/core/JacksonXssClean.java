package com.sensetime.iva.whale.core;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @Description 处理 jackson作为消息转换器时 xss攻击
 * @Date 2021/12/24
 * @Created by helin
 */
public class JacksonXssClean extends JsonDeserializer<String> {

    private static final Logger log = LoggerFactory.getLogger(JacksonXssClean.class);

    private final XssCleaner xssCleaner;

    public JacksonXssClean(XssCleaner xssCleaner) {
        this.xssCleaner = xssCleaner;
    }

    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String text = jsonParser.getValueAsString();
        if (text == null) {
            return null;
        } else if (XssHolder.isEnabled()) {
            String value = this.xssCleaner.clean(text);
            log.debug("Jackson property value:{} cleaned up by whale-xss, current value is:{}.", text, value);
            return value;
        } else {
            return text;
        }
    }
}
