package com.bj.zzq.utils;

import java.util.UUID;

/**
 * @Author: zhaozhiqiang
 * @Date: 2019/2/1
 * @Description:
 */
public class IdUtils {
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
