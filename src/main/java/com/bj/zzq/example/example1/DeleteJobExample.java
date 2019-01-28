package com.bj.zzq.example.example1;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

/**
 * @Author: zhaozhiqiang
 * @Date: 2019/1/28
 * @Description:
 */
public class DeleteJobExample {
    public static void main(String[] args) throws SchedulerException, InterruptedException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        JobDetail jobDetail = JobBuilder.newJob(SimpleJob.class).withIdentity("job1", "group1").storeDurably(true).build();
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        jobDataMap.put("count", 1);
        SimpleTrigger trigger = TriggerBuilder.newTrigger().startNow().withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInMilliseconds(1)).build();
        scheduler.scheduleJob(jobDetail, trigger);
        scheduler.start();
        Thread.sleep(1000 * 1000);

    }
}
