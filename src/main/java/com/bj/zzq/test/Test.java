package com.bj.zzq.test;

import com.bj.zzq.utils.EmailUtils;

/**
 * @Author: zhaozhiqiang
 * @Date: 2019/2/13
 * @Description:
 */
public class Test {

    public static void main(String[] args) {
        EmailUtils.sendEmail("龙泉驾校约车成功", "恭喜你约到 " + "2019-09-12" + " (周" + "一" + ") " + "下午1点到5点" + "的车，详情请登录学车不查看！", "");
    }

}
