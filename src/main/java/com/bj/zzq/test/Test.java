package com.bj.zzq.test;

import com.bj.zzq.core.OrderInfo;
import com.bj.zzq.utils.DateUtils;
import com.bj.zzq.utils.EmailUtils;
import org.hibernate.validator.constraints.Email;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.transaction.PlatformTransactionManager;

import java.lang.reflect.Method;

/**
 * @Author: zhaozhiqiang
 * @Date: 2019/2/13
 * @Description:
 */
public class Test {

    public static void main(String[] args) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setEmail("1010955631@qq.com");
        EmailUtils.sendEmail("龙泉驾校约车成功", "恭喜你约到 " + "2019-09-12" + " (周" + "一" + ") " + "下午1点到5点" + "的车，详情请登录学车不查看！", orderInfo);
    }

}
