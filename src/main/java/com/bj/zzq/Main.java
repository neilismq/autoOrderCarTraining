package com.bj.zzq;


import com.bj.zzq.core.Order;
import com.bj.zzq.core.OrderInfo;
import com.bj.zzq.utils.ConfProperties;
import org.quartz.SchedulerException;

import java.io.*;

/**
 * @Author: zhaozhiqiang
 * @Date: 2019/1/11
 * @Description: 只在早上七点抢
 */
public class Main {
    public static void main(String[] args) throws IOException, SchedulerException {
        Order.init();
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setUsername(ConfProperties.username);
        orderInfo.setPassword(ConfProperties.password);
        orderInfo.setEmail(ConfProperties.emailReceiverAddr);
        orderInfo.setCnbh(ConfProperties.cnbh);
        orderInfo.setOrderDate("2019-02-11");
        orderInfo.setOrderType("15");
        Order.addOrderJobSchedule(orderInfo);
    }
}
