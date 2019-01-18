package com.bj.zzq.utils;

import com.bj.zzq.Main;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Author: zhaozhiqiang
 * @Date: 2019/1/15
 * @Description:
 */
public class PickJob implements Job {
    private static Logger log = Logger.getLogger(PickJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {

        log.info("捡漏job任务开始-------------------------------");
        try {
            Calendar instance = Calendar.getInstance();
            for (int i = 1; i < 6; i++) {
                instance.add(Calendar.DAY_OF_YEAR, i);
                int weekNum = instance.get(Calendar.DAY_OF_WEEK);
                //如果不是周六周日，不抢，没啥意思
                if (weekNum != 7 && weekNum != 1) {
                    continue;
                }
                Date time = instance.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String orderDate = sdf.format(time);
                Main.picker(orderDate);
            }
        } catch (Exception e) {
            log.info("捡漏出错！", e);
        }
        log.info("捡漏job任务结束-------------------------------");

    }
}
