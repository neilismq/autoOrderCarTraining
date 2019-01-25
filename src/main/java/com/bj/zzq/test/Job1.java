package com.bj.zzq.test;

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
        count++;
        jobDataMap.put("count", count);

        try {
            Thread.sleep(2L * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

       System.out.println("ColorJob: " + name + " executing at " + new Date() + "\n" +
                "  favorite color is " + color + "\n" +
                "  execution count (from job map) is " + count + "\n" +
                "  execution count (from job member variable) is " + _counter);
    }
}
