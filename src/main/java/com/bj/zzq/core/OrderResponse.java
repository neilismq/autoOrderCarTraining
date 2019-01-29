package com.bj.zzq.core;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: zhaozhiqiang
 * @Date: 2019/1/29
 * @Description:
 */
public class OrderResponse extends OrderInfo {
    private String jobName;
    private String jobGoup;
    private List<TriggerInfo> triggers;

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobGoup() {
        return jobGoup;
    }

    public void setJobGoup(String jobGoup) {
        this.jobGoup = jobGoup;
    }

    public List<TriggerInfo> getTriggers() {
        return triggers;
    }

    public void setTriggers(List<TriggerInfo> triggers) {
        this.triggers = triggers;
    }
}
