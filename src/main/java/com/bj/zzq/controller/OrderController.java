package com.bj.zzq.controller;

import com.bj.zzq.core.*;
import com.bj.zzq.dao.OrderService;
import com.bj.zzq.utils.CommonResponse;
import com.bj.zzq.utils.DateUtils;
import com.bj.zzq.utils.IdUtils;
import com.sun.corba.se.spi.orb.ORBData;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

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

    @Autowired
    private OrderService orderService;

    @ResponseBody
    @RequestMapping(value = "/addOrder", method = RequestMethod.POST)
    public CommonResponse addJobs(@RequestBody OrderInfo orderInfo) throws SchedulerException {
        String orderDate = orderInfo.getOrderDate();
        Date pickEndTime = Order.getPickEndTime(orderDate);
        Date now = new Date();
        if (now.after(pickEndTime)) {
            CommonResponse response = CommonResponse.errorInstance();
            response.setBody("抢号时间设置不对");
            return response;
        }
        List<OrderInfo> orderInfo1 = orderService.selectOrderInfoUnique(orderInfo);
        if (orderInfo1 != null && orderInfo1.size() > 0) {
            CommonResponse response = CommonResponse.errorInstance();
            response.setBody("此订单已经存在！");
            return response;
        }
        orderInfo.setId(IdUtils.uuid());
        orderInfo.setCreate_time(new Date());
        orderInfo.setStatus("0");// 0-没抢到，1-已抢到
        orderService.insertOrderInfo(orderInfo);
        Order.addOrderJobSchedule(scheduler, orderInfo);
        return CommonResponse.okInstance();
    }

    @ResponseBody
    @RequestMapping(value = "/deleteOrder", method = RequestMethod.POST)
    public CommonResponse deleteOrder(@RequestBody String id) throws SchedulerException {
        OrderInfo orderInfo = orderService.selectOrderbyId(id);
        JobKey jobKey = new JobKey("job_" + orderInfo.getUsername() + "_" + orderInfo.getOrderDate(), "job_group");
        deleteJobs(new JobKey[]{jobKey});
        orderService.deleteOrderById(id);
        return CommonResponse.okInstance();
    }

    @ResponseBody
    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
    public CommonResponse addUsers(@RequestBody UserInfo userInfo) {
        List<UserInfo> userInfo1 = orderService.selectUserByUsername(userInfo.getUsername());
        if (userInfo1 != null && userInfo1.size() > 0) {
            CommonResponse response = CommonResponse.errorInstance();
            response.setBody("此用户已经存在！");
            return response;
        }
        String uuid = IdUtils.uuid();
        userInfo.setId(uuid);
        orderService.insertUser(userInfo);
        return CommonResponse.okInstance();
    }

    @ResponseBody
    @RequestMapping(value = "/deleteUser", method = RequestMethod.POST)
    public CommonResponse deleteUser(@RequestBody String id) {
        orderService.deleteUserById(id);
        return CommonResponse.okInstance();
    }

    @ResponseBody
    @RequestMapping(value = "/selectUsers", method = RequestMethod.POST)
    public CommonResponse selectAllUsers() {
        List<UserInfo> userInfos = orderService.selectAllUsers();
        CommonResponse response = CommonResponse.okInstance();
        response.setBody(userInfos);
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/selectOrdersByUser", method = RequestMethod.POST)
    public CommonResponse selectOrderByUserId(@RequestBody String user_id) {
        List<OrderInfo> orderInfos = orderService.selectOrderByUserId(user_id);
        CommonResponse response = CommonResponse.okInstance();
        response.setBody(orderInfos);
        return response;
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
