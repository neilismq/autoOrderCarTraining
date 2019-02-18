package com.bj.zzq.model;


import com.bj.zzq.core.TriggerInfo;
import org.quartz.Trigger;

import java.util.List;

public class OrderTaskEntity {
    private String orderDate;
    private String timeSlot;
    private String userId;
    private String status;
    private String username;
    private String password;
    private String email;
    private String cnbh;
    private String orderId;
    private String jobName;
    private String groupName;
    private List<TriggerInfo> triggers;
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCnbh() {
        return cnbh;
    }

    public void setCnbh(String cnbh) {
        this.cnbh = cnbh;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<TriggerInfo> getTriggers() {
        return triggers;
    }

    public void setTriggers(List<TriggerInfo> triggers) {
        this.triggers = triggers;
    }
}
