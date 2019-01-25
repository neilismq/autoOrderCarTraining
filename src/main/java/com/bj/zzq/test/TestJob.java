package com.bj.zzq.test;

import org.quartz.*;

/**
 * @Author: zhaozhiqiang
 * @Date: 2019/1/18
 * @Description:
 */
public class TestJob implements InterruptableJob {


    @Override
    public void interrupt() throws UnableToInterruptJobException {

    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

    }
}
