package com.bj.zzq.core;

import com.bj.zzq.Main;
import org.apache.log4j.Logger;
import org.quartz.*;

/**
 * @Author: zhaozhiqiang
 * @Date: 2019/1/15
 * @Description:
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class OrderJob implements Job {
    private static Logger log = Logger.getLogger(OrderJob.class);

    @Override
    public void execute(JobExecutionContext context) {
        JobDetail jobDetail = context.getJobDetail();
        JobKey key = jobDetail.getKey();
        String name = key.getName();
        String group = key.getGroup();
        Trigger trigger = context.getTrigger();
        TriggerKey triggerKey = trigger.getKey();
        String triggerName = triggerKey.getName();
        String triggerGroupName = triggerKey.getGroup();

        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        OrderInfo orderInfo = (OrderInfo) jobDataMap.get("orderInfo");
        String username = orderInfo.getUsername();
        String orderType = orderInfo.getOrderType();
        String orderDate = orderInfo.getOrderDate();

        log.info("JobName:" + name + ",JobGroup:" + group + ",TriggerName:" + triggerName + ",triggerGroup:" + triggerGroupName + ",用户：" + username + ",抢号日期：" + orderDate + ",时间段：" + orderType + "的任务开始-------------------------------");

        try {
            Order.orderTask(orderInfo, context);
        } catch (Exception e) {
            log.error("抢票失败", e);
        }

        log.info("job任务结束-------------------------------");

    }
}
