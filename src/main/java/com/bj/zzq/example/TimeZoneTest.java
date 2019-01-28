package com.bj.zzq.example;

import java.util.TimeZone;

/**
 * @Author: zhaozhiqiang
 * @Date: 2019/1/28
 * @Description:
 */
public class TimeZoneTest {
    public static void main(String[] args){
        String[] availableIDs = TimeZone.getAvailableIDs();
        for (int i = 0; i < availableIDs.length; i++) {
            System.out.println(availableIDs[i]);
        }
    }
}
