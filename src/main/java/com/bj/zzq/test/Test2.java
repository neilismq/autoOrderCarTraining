package com.bj.zzq.test;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.calendar.HolidayCalendar;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.listeners.SchedulerListenerSupport;
import org.quartz.spi.JobStore;
import org.quartz.spi.ThreadPool;

import java.util.Date;

/**
 * @Author: zhaozhiqiang
 * @Date: 2019/1/24
 * @Description:
 */
public class Test2 {
    public static void main(String[] args) throws SchedulerException, InterruptedException {
        SchedulerFactory sf = new StdSchedulerFactory();
        Scheduler sched = sf.getScheduler();
        JobDetail job1 = JobBuilder.newJob(Job1.class)
                .withIdentity("job1", "group1")
                .build();

        SimpleTrigger trigger1 = TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "group1")
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(10)
                        .withRepeatCount(4))
                .build();
        job1.getJobDataMap().put("color", "Green");
        job1.getJobDataMap().put("count", 1);

        JobDetail job2 = JobBuilder.newJob(Job1.class)
                .withIdentity("job2", "group1")
                .build();

        SimpleTrigger trigger2 = TriggerBuilder.newTrigger()
                .withIdentity("trigger2", "group1")
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(10)
                        .withRepeatCount(4))
                .build();
        job2.getJobDataMap().put("color", "Red");
        job2.getJobDataMap().put("count", 1);
        sched.scheduleJob(job1, trigger1);
        sched.scheduleJob(job2, trigger2);
        sched.start();
        sched.shutdown(true);
        Thread.sleep(60 * 1000);
    }
}
