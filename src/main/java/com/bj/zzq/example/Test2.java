package com.bj.zzq.example;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.HashSet;

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
                .withIdentity("jobLongTime2", "group1")
                .build();

        SimpleTrigger trigger1 = TriggerBuilder.newTrigger()
                .withIdentity("triggerLongTime4", "group1")
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(3)
                        .withRepeatCount(3).withMisfireHandlingInstructionIgnoreMisfires())
                .build();
        job1.getJobDataMap().put("color", "Green");
        job1.getJobDataMap().put("count", 1);
        JobDetail jobLong = JobBuilder.newJob(JobLongTime.class)
                .withIdentity("jobLongTime1", "group1")
                .build();

        SimpleTrigger triggerLong = TriggerBuilder.newTrigger()
                .withIdentity("triggerLongTime8", "group1")
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()).withPriority(2)
                .build();
        SimpleTrigger triggerLong2 = TriggerBuilder.newTrigger()
                .withIdentity("triggerLongTime7", "group1")
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInSeconds(2000)).withPriority(2)
                .build();
        HashSet<Trigger> triggerSet = new HashSet<>();
        triggerSet.add(triggerLong2);
        triggerSet.add(triggerLong);
        sched.scheduleJob(job1, trigger1);
        sched.scheduleJob(jobLong, triggerSet, true);

        sched.start();
        //sched.shutdown();
    }
}
