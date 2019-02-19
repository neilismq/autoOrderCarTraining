package com.bj.zzq.controller;

import com.bj.zzq.core.*;
import com.bj.zzq.model.OrderInfoEntity;
import com.bj.zzq.model.OrderTaskEntity;
import com.bj.zzq.model.UserEntity;
import com.bj.zzq.service.OrderService;
import com.bj.zzq.utils.CommonResponse;
import com.bj.zzq.utils.DateUtils;
import com.bj.zzq.utils.IdUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
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
    private static Logger log = Logger.getLogger(OrderController.class.getClass());

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private OrderService orderService;


    @ResponseBody
    @RequestMapping(value = "/addOrder", method = RequestMethod.POST)
    public CommonResponse addJobs(@RequestBody OrderInfoEntity orderInfoEntity) throws SchedulerException {
        Date orderDateDf = orderInfoEntity.getOrderDate();
        String orderDate = DateUtils.dateToStr(orderDateDf);
        Date pickEndTime = Order.getPickEndTime(orderDate);
        Date now = new Date();
        if (now.after(pickEndTime)) {
            CommonResponse response = CommonResponse.errorInstance();
            response.setMessage("抢号时间设置不对");
            return response;
        }
        List<UserEntity> userEntities = orderService.selectUserByUserId(orderInfoEntity.getUserId());
        if (userEntities == null || userEntities.size() == 0) {
            CommonResponse response = CommonResponse.errorInstance();
            response.setMessage("请选择用户！");
            return response;
        }
        UserEntity userEntity = userEntities.get(0);
        List<OrderInfoEntity> orderInfoEntities = orderService.selectOrderInfoUnique(userEntity.getId(), orderInfoEntity);
        if (orderInfoEntities != null && orderInfoEntities.size() > 0) {
            CommonResponse response = CommonResponse.errorInstance();
            response.setMessage("此订单已经存在，请不要重复设置！");
            return response;
        }
        orderInfoEntity.setId(IdUtils.uuid());
        orderInfoEntity.setCreateTime(new Date());
        orderInfoEntity.setStatus("0");// 0-没抢到，1-已抢到
        orderInfoEntity.setIsStop("0");// 0-没抢到，1-已抢到
        orderService.insertOrderInfo(orderInfoEntity);
        OrderTaskEntity orderTaskEntity = new OrderTaskEntity();
        orderTaskEntity.setUsername(userEntity.getUsername());
        orderTaskEntity.setPassword(userEntity.getPassword());
        orderTaskEntity.setCnbh(userEntity.getCnbh());
        orderTaskEntity.setEmail(userEntity.getEmail());
        orderTaskEntity.setOrderDate(DateUtils.dateToStr(orderInfoEntity.getOrderDate()));
        orderTaskEntity.setTimeSlot(orderInfoEntity.getTimeSlot());
        orderTaskEntity.setOrderId(orderInfoEntity.getId());
        Order.addOrderJobSchedule(scheduler, orderTaskEntity);
        return CommonResponse.okInstance();
    }

    @ResponseBody
    @RequestMapping(value = "/deleteOrder", method = RequestMethod.POST)
    public CommonResponse deleteOrder(@RequestBody String id) throws SchedulerException {
        OrderInfoEntity orderInfoEntity = orderService.selectOrderbyId(id);
        List<UserEntity> userEntities = orderService.selectUserByUserId(orderInfoEntity.getUserId());
        if (userEntities != null && userEntities.size() > 0) {
            UserEntity userEntity = userEntities.get(0);
            JobKey jobKey = new JobKey("job_" + userEntity.getUsername() + "_" + orderInfoEntity.getOrderDate(), "job_group");
            deleteJobs(new JobKey[]{jobKey});
            orderService.deleteOrderById(id);
        }
        return CommonResponse.okInstance();
    }

    @ResponseBody
    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
    public CommonResponse addUsers(@RequestBody UserEntity userEntity) {
        if (StringUtils.isEmpty(userEntity.getUsername())) {
            return CommonResponse.errorInstance().setBody("账号不能为空，请检查！");
        }
        if (StringUtils.isEmpty(userEntity.getPassword())) {
            return CommonResponse.errorInstance().setBody("密码不能为空，请检查！");
        }
        if (StringUtils.isEmpty(userEntity.getEmail())) {
            return CommonResponse.errorInstance().setBody("邮箱不能为空，请检查！");
        }
        List<UserEntity> userEntities = orderService.selectUserByUsername(userEntity.getUsername());
        if (userEntities != null && userEntities.size() > 0) {
            return CommonResponse.errorInstance().setBody("此用户已经存在，请检查！");
        }
        boolean isValid = testUserIsValid(userEntity.getUsername(), userEntity.getPassword());
        if (!isValid) {
            return CommonResponse.errorInstance().setBody("账号密码不正确，请检查！");
        }
        if (StringUtils.isEmpty(userEntity.getCnbh())) {
            try {
                String cnbh = Order.getCnbh(userEntity.getUsername(), userEntity.getPassword());
                userEntity.setCnbh(cnbh);
            } catch (Exception e) {
                log.error("获取cnbh失败", e);
            }
        }
        String uuid = IdUtils.uuid();
        userEntity.setId(uuid);
        userEntity.setCreateTime(new Date());
        orderService.insertUser(userEntity);
        return CommonResponse.okInstance();
    }

    private boolean testUserIsValid(String username, String password) {
        OrderTaskEntity taskEntity = new OrderTaskEntity();
        taskEntity.setUsername(username);
        taskEntity.setPassword(password);
        try {
            Order.login(taskEntity);
        } catch (Exception e) {
            log.error("测试登录失败", e);
            return false;
        }
        return true;
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
        List<UserEntity> userEntities = orderService.selectAllUsers();
        CommonResponse response = CommonResponse.okInstance();
        response.setBody(userEntities);
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/selectOrdersByUser", method = RequestMethod.POST)
    public CommonResponse selectOrderByUserId(@RequestBody OrderInfoEntity orderInfoEntity) {
        List<OrderInfoEntity> orderInfoEntities = orderService.selectOrderByUserId(orderInfoEntity.getUserId());
        CommonResponse response = CommonResponse.okInstance();
        response.setBody(orderInfoEntities);
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
        ArrayList<OrderTaskEntity> orderResponses = new ArrayList<>();
        for (JobKey jobKey : jobKeys) {
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            JobDataMap jobDataMap = jobDetail.getJobDataMap();
            OrderTaskEntity orderTaskEntity = (OrderTaskEntity) jobDataMap.get("orderInfo");
            OrderTaskEntity orderResponse = new OrderTaskEntity();
            orderResponse.setUsername(orderTaskEntity.getUsername());
            orderResponse.setPassword(orderTaskEntity.getPassword());
            orderResponse.setJobName(jobKey.getName());
            orderResponse.setGroupName(jobKey.getGroup());
            orderResponse.setOrderDate(orderTaskEntity.getOrderDate());
            orderResponse.setTimeSlot(orderTaskEntity.getTimeSlot());
            orderResponse.setCnbh(orderTaskEntity.getCnbh());
            orderResponse.setEmail(orderTaskEntity.getEmail());
            List<TriggerInfo> triggerInfos = new ArrayList<>();
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

}
