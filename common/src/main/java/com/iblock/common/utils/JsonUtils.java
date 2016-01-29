package com.iblock.common.utils;

import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * JSON转换工具类
 * Created by yuqihong on 15/6/9.
 */
public class JsonUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public JsonUtils() {
    }

    public static String toStr(Object model) throws IOException {
        return MAPPER.writeValueAsString(model);
    }

    public static <T> T fromStr(String content, Class<T> clazz) throws IOException {
        return MAPPER.readValue(content, clazz);
    }

    public static Map<String, Object> fromStrToMap(String content) throws IOException {
        return (Map) fromStr(content, Map.class);
    }

    static {
        MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    }
}
