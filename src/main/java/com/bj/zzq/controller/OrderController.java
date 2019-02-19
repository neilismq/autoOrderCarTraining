package com.bj.zzq.controller;

import com.bj.zzq.core.*;
import com.bj.zzq.dao.OrderInfoDao;
import com.bj.zzq.dao.UserDao;
import com.bj.zzq.model.OrderInfoEntity;
import com.bj.zzq.model.OrderTaskEntity;
import com.bj.zzq.model.UserEntity;
import com.bj.zzq.service.OrderService;
import com.bj.zzq.utils.CommonResponse;
import com.bj.zzq.utils.DateUtils;
import com.bj.zzq.utils.IdUtils;
import io.swagger.annotations.ApiOperation;
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

    @ApiOperation(value = "添加预约")
    @ResponseBody
    @RequestMapping(value = "/addOrder", method = RequestMethod.POST)
    public CommonResponse addJobs(@RequestBody OrderInfoEntity orderInfoEntity) throws SchedulerException {
        Date orderDateDf = orderInfoEntity.getOrderDate();
        String orderDate = DateUtils.dateToStr(orderDateDf);
        Date pickEndTime = Order.getPickEndTime(orderDate);
        Date now = new Date();
        if (now.after(pickEndTime)) {
            return CommonResponse.errorInstance().setMessage("抢号时间设置不对");

        }
        UserEntity userEntity = orderService.selectUserByUserId(orderInfoEntity.getUserId());
        if (userEntity == null) {
            return CommonResponse.errorInstance().setMessage("请选择用户");
        }

        List<OrderInfoEntity> orderInfoEntities = orderService.selectOrderInfoUnique(userEntity.getId(), orderInfoEntity);
        if (orderInfoEntities != null && orderInfoEntities.size() > 0) {
            return CommonResponse.errorInstance().setMessage("此订单已经存在，请不要重复设置");
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

    @ApiOperation(value = "删除预约")
    @ResponseBody
    @RequestMapping(value = "/deleteOrder", method = RequestMethod.POST)
    public CommonResponse deleteOrder(@RequestBody String id) throws SchedulerException {

        OrderInfoEntity orderInfoEntity = orderService.selectOrderbyId(id);
        UserEntity userEntity = orderService.selectUserByUserId(orderInfoEntity.getUserId());
        if (userEntity != null) {
            JobKey jobKey = getJobKey(userEntity.getUsername(), DateUtils.dateToStr(orderInfoEntity.getOrderDate()));
            deleteJobs(new JobKey[]{jobKey});
            orderService.deleteOrderById(id);
        }
        return CommonResponse.okInstance();

    }

    @ApiOperation(value = "添加用户")
    @ResponseBody
    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
    public CommonResponse addUsers(@RequestBody UserEntity userEntity) {
        if (StringUtils.isEmpty(userEntity.getUsername())) {
            return CommonResponse.errorInstance().setMessage("账号不能为空，请检查！");
        }
        if (StringUtils.isEmpty(userEntity.getPassword())) {
            return CommonResponse.errorInstance().setMessage("密码不能为空，请检查！");
        }
        if (StringUtils.isEmpty(userEntity.getEmail())) {
            return CommonResponse.errorInstance().setMessage("邮箱不能为空，请检查！");
        }
        List<UserEntity> userEntities = orderService.selectUserByUsername(userEntity.getUsername());
        if (userEntities != null && userEntities.size() > 0) {
            return CommonResponse.errorInstance().setMessage("此用户已经存在，请检查！");
        }
        boolean isValid = testUserIsValid(userEntity.getUsername(), userEntity.getPassword());
        if (!isValid) {
            return CommonResponse.errorInstance().setMessage("账号密码不正确，请检查！");
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

    @ApiOperation(value = "查询所有用户")
    @ResponseBody
    @RequestMapping(value = "/selectUsers", method = RequestMethod.POST)
    public CommonResponse selectAllUsers(@RequestBody String keyword) {
        List<UserEntity> userEntities = orderService.selectAllUsers(keyword);
        return CommonResponse.okInstance().setBody(userEntities);
    }

    @ApiOperation(value = "根据用户查询预约")
    @ResponseBody
    @RequestMapping(value = "/selectOrdersByUser", method = RequestMethod.POST)
    public CommonResponse selectOrderByUserId(@RequestBody OrderInfoEntity orderInfoEntity) {
        List<OrderInfoEntity> orderInfoEntities = orderService.selectOrderByUserId(orderInfoEntity.getUserId());
        return CommonResponse.okInstance().setBody(orderInfoEntities);
    }

    @ApiOperation(value = "暂停预约")
    @ResponseBody
    @RequestMapping(value = "/pauseOrder", method = RequestMethod.POST)
    public CommonResponse pauseOrder(@RequestBody OrderInfoEntity orderInfo) throws SchedulerException {
        OrderInfoEntity orderInfoEntity = orderService.selectOrderbyId(orderInfo.getId());
        if (orderInfoEntity == null) {
            return CommonResponse.errorInstance().setMessage("预约不存在！");
        }
        UserEntity userEntity = orderService.selectUserByUserId(orderInfoEntity.getUserId());
        if (userEntity == null) {
            return CommonResponse.errorInstance().setMessage("用户不存在！");
        }
        JobKey jobKey = getJobKey(userEntity.getUsername(), DateUtils.dateToStr(orderInfoEntity.getOrderDate()));
        pauseJobs(new JobKey[]{jobKey});
        orderInfoEntity.setIsStop("1");//暂停
        orderService.updateOrder(orderInfoEntity);
        return CommonResponse.okInstance();
    }

    @ApiOperation(value = "恢复暂停的预约")
    @ResponseBody
    @RequestMapping(value = "/resumeOrder", method = RequestMethod.POST)
    public CommonResponse resumeOrder(@RequestBody OrderInfoEntity orderInfo) throws SchedulerException {
        OrderInfoEntity orderInfoEntity = orderService.selectOrderbyId(orderInfo.getId());
        if (orderInfoEntity == null) {
            return CommonResponse.errorInstance().setMessage("预约不存在！");
        }
        UserEntity userEntity = orderService.selectUserByUserId(orderInfoEntity.getUserId());
        if (userEntity == null) {
            return CommonResponse.errorInstance().setMessage("用户不存在！");
        }
        JobKey jobKey = getJobKey(userEntity.getUsername(), DateUtils.dateToStr(orderInfoEntity.getOrderDate()));
        resumeJobs(new JobKey[]{jobKey});

        orderInfoEntity.setIsStop("0");//0-正常
        orderService.updateOrder(orderInfoEntity);
        return CommonResponse.okInstance();
    }

    private JobKey getJobKey(String username, String orderDate) {
        return new JobKey("job_" + username + "_" + orderDate, "job_group");
    }

    private void deleteJobs(JobKey[] jobKeys) throws SchedulerException {
        scheduler.deleteJobs(Arrays.asList(jobKeys));
    }

    private void pauseJobs(JobKey[] jobKeys) throws SchedulerException {
        for (JobKey jobKey : jobKeys) {
            scheduler.pauseJob(jobKey);
        }
    }


    private void resumeJobs(JobKey[] jobKeys) throws SchedulerException {
        for (JobKey jobKey : jobKeys) {
            scheduler.resumeJob(jobKey);
        }
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
