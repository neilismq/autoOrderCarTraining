package com.bj.zzq.example;

import org.quartz.*;

import java.util.Date;

/**
 * @Author: zhaozhiqiang
 * @Date: 2019/1/28
 * @Description:
 */
public class JobLongTime implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Trigger trigger = context.getTrigger();
        TriggerKey key = trigger.getKey();
        String name = key.getName();
        int priority = trigger.getPriority();
        System.out.println("JobLongTime start:\nTime:" + new Date() + "\ntriggerName:" + name + "\ntriggerPriority:" + priority+"\n");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
