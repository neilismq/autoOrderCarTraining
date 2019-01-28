package com.bj.zzq.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: zhaozhiqiang
 * @Date: 2019/1/28
 * @Description:
 */
public class DateUtils {
    private static SimpleDateFormat format_yyyy_mm_dd = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat format_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static Date strToDate(String dateStr) {
        try {
            return format_yyyy_mm_dd.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String dateToStr(Date dateStr) {
        return format_yyyy_mm_dd.format(dateStr);
    }

    public static Date strToTime(String dateStr) {
        try {
            return format_time.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String timeToStr(Date dateStr) {
        return format_time.format(dateStr);
    }
}
