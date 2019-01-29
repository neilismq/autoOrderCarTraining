package com.bj.zzq.core;

import org.quartz.Trigger;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: zhaozhiqiang
 * @Date: 2019/1/29
 * @Description:
 */
public class TriggerInfo implements Serializable {
    private String name;
    private String group;
    private String startTime;// yyyy-MM-dd HH:mm:ss
    private String endTime;
    private int priority;
    private int repeatCount;
    private long interval;//毫秒
    //NONE, NORMAL, PAUSED, COMPLETE, ERROR, BLOCKED
    private String status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
