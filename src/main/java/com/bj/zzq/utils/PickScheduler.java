package com.bj.zzq.utils;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

/**
 * @Author: zhaozhiqiang
 * @Date: 2019/1/15
 * @Description:
 */
public class PickScheduler {
    //调度器
    public static Scheduler getScheduler() throws SchedulerException {
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        return schedulerFactory.getScheduler();
    }

    public static void schedulerJob() throws SchedulerException {
        //创建任务
        JobDetail jobDetail = JobBuilder.newJob(PickJob.class).withIdentity("pickJob", "pickGroup").storeDurably(false).build();
        //创建触发器,周二到周六捡漏，只捡漏周六周日的号
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity("pickTrigger", "pickTriggerGroup")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0/5 * ? * 3-7 ")).build();
        Scheduler scheduler = getScheduler();
        //将任务及其触发器放入调度器
        scheduler.scheduleJob(jobDetail, trigger);
        //调度器开始调度任务
        scheduler.start();
    }
}
