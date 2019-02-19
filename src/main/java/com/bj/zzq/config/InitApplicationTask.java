package com.bj.zzq.config;

import com.bj.zzq.core.Order;
import com.bj.zzq.model.OrderInfoEntity;
import com.bj.zzq.model.OrderTaskEntity;
import com.bj.zzq.model.UserEntity;
import com.bj.zzq.service.OrderService;
import com.bj.zzq.utils.DateUtils;
import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: zhaozhiqiang
 * @Date: 2019/2/19
 * @Description:
 */
@Component
public class InitApplicationTask {
    private static Logger log = Logger.getLogger(InitApplicationTask.class.getClass());

    @Autowired
    private Scheduler scheduler;
    @Autowired
    private OrderService orderService;

    @PostConstruct
    public void init() {
        List<OrderInfoEntity> orderInfoEntities = orderService.selectAllExecutableOrders();
        List<UserEntity> userEntities = orderService.selectAllUsers(null);
        Map<String, UserEntity> collect = userEntities.stream().collect(Collectors.toMap(UserEntity::getId, Function.identity()));
        orderInfoEntities.stream().forEach(orderInfoEntity -> {
            OrderTaskEntity taskEntity = new OrderTaskEntity();
            String userId = orderInfoEntity.getUserId();
            if (collect.keySet().contains(userId)) {
                UserEntity userEntity = collect.get(userId);
                taskEntity.setUsername(userEntity.getUsername());
                taskEntity.setPassword(userEntity.getPassword());
                taskEntity.setCnbh(userEntity.getCnbh());
                taskEntity.setEmail(userEntity.getEmail());
                taskEntity.setUserId(userEntity.getId());
                taskEntity.setOrderDate(DateUtils.dateToStr(orderInfoEntity.getOrderDate()));
                taskEntity.setTimeSlot(orderInfoEntity.getTimeSlot());
                taskEntity.setStatus(orderInfoEntity.getStatus());
                taskEntity.setOrderId(orderInfoEntity.getId());
                try {
                    Order.addOrderJobSchedule(scheduler, taskEntity);
                } catch (SchedulerException e) {
                    log.error("初始化任务错误", e);
                }
            }
        });
    }

}
