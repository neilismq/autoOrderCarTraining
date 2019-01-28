package com.bj.zzq.example.example1;

import org.quartz.*;

import java.util.Date;

/**
 * @Author: zhaozhiqiang
 * @Date: 2019/1/28
 * @Description:
 */
@PersistJobDataAfterExecution
public class SimpleJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDetail jobDetail = context.getJobDetail();
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        int count = jobDataMap.getIntValue("count");
        JobKey jobKey = jobDetail.getKey();
        String name = jobKey.getName();
        count++;
        jobDataMap.put("count", count);
        System.out.println("jobName:" + name + "\ncount:" + count + "\nTime:" + new Date() + "\n");
        if (count == 100) {
            try {
                context.getScheduler().deleteJob(jobKey);
                return;
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        }
    }
}
