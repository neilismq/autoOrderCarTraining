package com.bj.zzq.controller;

import com.bj.zzq.core.Order;
import com.bj.zzq.core.OrderInfo;
import com.bj.zzq.core.OrderResponse;
import com.bj.zzq.core.TriggerInfo;
import com.bj.zzq.utils.CommonResponse;
import com.bj.zzq.utils.DateUtils;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @Author: zhaozhiqiang
 * @Date: 2019/1/29
 * @Description:
 */
@Controller
@RequestMapping(value = "/order")
public class OrderController {
    @Autowired
    private Scheduler scheduler;

    @ResponseBody
    @RequestMapping(value = "/addJobs", method = RequestMethod.POST)
    public CommonResponse addJobs(@RequestBody OrderInfo[] orderInfo) throws SchedulerException {
        for (OrderInfo orderInfoItem : orderInfo) {
            Order.addOrderJobSchedule(scheduler, orderInfoItem);
        }
        return CommonResponse.okInstance();
    }

    @ResponseBody
    @RequestMapping(value = "/deleteJobs", method = RequestMethod.POST)
    public CommonResponse deleteJobs(@RequestBody JobKey[] jobKeys) throws SchedulerException {
        scheduler.deleteJobs(Arrays.asList(jobKeys));
        return CommonResponse.okInstance();
    }

    @ResponseBody
    @RequestMapping(value = "/pauseJobs", method = RequestMethod.POST)
    public CommonResponse pauseJobs(@RequestBody JobKey[] jobKeys) throws SchedulerException {
        for (JobKey jobKey : jobKeys) {
            scheduler.pauseJob(jobKey);
        }
        return CommonResponse.okInstance();
    }

    @ResponseBody
    @RequestMapping(value = "/resumeJobs", method = RequestMethod.POST)
    public CommonResponse resumeJobs(@RequestBody JobKey[] jobKeys) throws SchedulerException {
        for (JobKey jobKey : jobKeys) {
            scheduler.resumeJob(jobKey);
        }
        return CommonResponse.okInstance();
    }

    @ResponseBody
    @RequestMapping(value = "/startSchduler", method = RequestMethod.POST)
    public CommonResponse startSchdule() throws SchedulerException {
        scheduler.start();
        scheduler.standby();
        return CommonResponse.okInstance();
    }

    @ResponseBody
    @RequestMapping(value = "/standbySchduler", method = RequestMethod.POST)
    public CommonResponse standbySchdule() throws SchedulerException {
        scheduler.start();
        scheduler.standby();
        return CommonResponse.okInstance();
    }
    //不允许用户停止schduler
//    @ResponseBody
//    @RequestMapping(value = "/shutdownSchdule", method = RequestMethod.POST)
//    public CommonResponse shutdownSchdule() throws SchedulerException {
//        scheduler.shutdown(true);
//        return CommonResponse.okInstance();
//    }

    @ResponseBody
    @RequestMapping(value = "/querySchdulerStatus", method = RequestMethod.POST)
    public CommonResponse querySchduleStatus() throws SchedulerException {
        String status = "";
        if (scheduler.isInStandbyMode()) {
            status = "待机";
        } else if (scheduler.isShutdown()) {
            status = "关机";
        } else if (scheduler.isStarted()) {
            status = "正常";
        } else {
            status = "未知";
        }
        CommonResponse response = CommonResponse.okInstance();
        response.setBody(status);
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/queryJobsInfo", method = RequestMethod.POST)
    public CommonResponse queryJobsInfo() throws SchedulerException {

        Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.anyJobGroup());
        ArrayList<OrderResponse> orderResponses = new ArrayList<>();
        for (JobKey jobKey : jobKeys) {
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            JobDataMap jobDataMap = jobDetail.getJobDataMap();
            OrderInfo orderInfo = (OrderInfo) jobDataMap.get("orderInfo");
            OrderResponse orderResponse = new OrderResponse();
            orderResponse.setUsername(orderInfo.getUsername());
            orderResponse.setPassword(orderInfo.getPassword());
            orderResponse.setJobName(jobKey.getName());
            orderResponse.setJobGoup(jobKey.getGroup());
            orderResponse.setOrderDate(orderInfo.getOrderDate());
            orderResponse.setOrderType(orderInfo.getOrderType());
            orderResponse.setCnbh(orderInfo.getCnbh());
            orderResponse.setEmail(orderInfo.getEmail());
            ArrayList<TriggerInfo> triggerInfos = new ArrayList<>();
            List<? extends Trigger> triggersOfJob = scheduler.getTriggersOfJob(jobKey);
            for (Trigger trigger : triggersOfJob) {
                Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                TriggerInfo triggerInfo = new TriggerInfo();
                TriggerKey triggerKey = trigger.getKey();
                triggerInfo.setName(triggerKey.getName());
                triggerInfo.setGroup(triggerKey.getGroup());
                triggerInfo.setStartTime(trigger.getStartTime() == null ? "" : DateUtils.timeToStr(trigger.getStartTime()));
                triggerInfo.setEndTime(trigger.getEndTime() == null ? "" : DateUtils.timeToStr(trigger.getEndTime()));
                triggerInfo.setPriority(trigger.getPriority());
                SimpleTrigger simpleTrigger = (SimpleTrigger) trigger;
                triggerInfo.setRepeatCount(simpleTrigger.getRepeatCount());
                triggerInfo.setInterval(simpleTrigger.getRepeatInterval());
                triggerInfo.setStatus(triggerState.name());
                triggerInfos.add(triggerInfo);
            }

            orderResponse.setTriggers(triggerInfos);
            orderResponses.add(orderResponse);
        }
        CommonResponse response = CommonResponse.okInstance();
        response.setBody(orderResponses);
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/test")
    public CommonResponse test() {
        CommonResponse response = CommonResponse.okInstance();
        OrderInfo[] infos = new OrderInfo[2];
        OrderInfo info = new OrderInfo();
        info.setUsername("111");
        info.setPassword("222");
        info.setEmail("11");
        info.setCnbh("111");
        info.setOrderDate("111");
        info.setOrderType("15");
        infos[0] = info;
        infos[1] = info;
        response.setBody(infos);
        return response;
    }
}
