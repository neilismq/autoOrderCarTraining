package com.bj.zzq.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    /**
     * 数字转大写
     *
     * @param numInWeek
     * @return
     */
    public static String numWeekToUpper(int numInWeek) {
        String numInWeekUpper = "";
        switch (numInWeek) {
            case 1: {
                numInWeekUpper = "日";
                break;
            }
            case 2: {
                numInWeekUpper = "一";
                break;
            }
            case 3: {
                numInWeekUpper = "二";
                break;
            }
            case 4: {
                numInWeekUpper = "三";
                break;
            }
            case 5: {
                numInWeekUpper = "四";
                break;
            }
            case 6: {
                numInWeekUpper = "五";
                break;
            }
            case 7: {
                numInWeekUpper = "六";
                break;
            }
            default:
                numInWeekUpper = "转换出错，请输入正确的星期数字(1-7)";
        }
        return numInWeekUpper;
    }

    public static String dateToWeek(String date) {
        Date date1 = strToDate(date);
        Calendar instance = Calendar.getInstance();
        instance.setTime(date1);
        int numInWeek = instance.get(Calendar.DAY_OF_WEEK);
        return numWeekToUpper(numInWeek);
    }
}
