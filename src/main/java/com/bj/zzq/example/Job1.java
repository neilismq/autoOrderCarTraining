package com.bj.zzq.example;

import org.quartz.*;

import java.util.Date;

/**
 * @Author: zhaozhiqiang
 * @Date: 2019/1/25
 * @Description:
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class Job1 implements Job {
    private int _counter = 1;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        JobKey key = context.getJobDetail().getKey();
        String name = key.getName();
        String color = jobDataMap.getString("color");
        int count = jobDataMap.getInt("count");
//        System.out.println("ColorJob: " + name + " executing at " + new Date() + "\n" +
//                "  favorite color is " + color + "\n" +
//                "  execution count (from job map) is " + count + "\n" +
//                "  execution count (from job member variable) is " + _counter);
        System.out.println("Time:" + new Date() + "\nJobname:" + name + "\nColor:" + color + "\n" + "count:" + count + "\n_counter:" + _counter + "\n");
//        try {
//            Thread.sleep( 1* 1000L);
//            count++;
//            jobDataMap.put("count", count);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }


    }
}
