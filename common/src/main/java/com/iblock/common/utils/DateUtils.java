package com.iblock.common.utils;

import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by baidu on 16/2/14.
 */
public class DateUtils {

    public static String format(Date time, String format) {
        if (time == null) {
            return null;
        }
        if (StringUtils.isBlank(format)) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        return new SimpleDateFormat(format).format(time);
    }

    public static Date parse(String s, String format) throws ParseException {
        if (s == null) {
            return null;
        }
        if (StringUtils.isBlank(format)) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        return new SimpleDateFormat(format).parse(s);
    }

    public static Long formatStr(String format) throws ParseException {
        if (format == null) {
            return null;
        }
        if (StringUtils.isBlank(format)) {
            format = "yyyy-MM-dd HH:mm";
        }
        return new SimpleDateFormat(format).parse(format).getTime();
    }

}
