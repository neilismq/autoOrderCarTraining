package com.bj.zzq.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bj.zzq.dao.OrderService;
import com.bj.zzq.utils.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.*;

import java.io.*;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeSet;

/**
 * @Author: zhaozhiqiang
 * @Date: 2019/1/11
 * @Description:
 */
public class Order {
    private static Logger log = Logger.getLogger(Order.class);

    public static void addOrderJobSchedule(Scheduler scheduler, OrderInfo orderInfo) throws SchedulerException {
        JobDetail orderJob = getOrderJob(orderInfo);
        Trigger fireTrigger = getFireTrigger(orderInfo);
        Trigger pickTrigger = getPickTrigger(orderInfo);
        TreeSet<Trigger> triggers = new TreeSet<>();
        if (fireTrigger != null) {
            triggers.add(fireTrigger);
        }
        if (pickTrigger != null) {
            triggers.add(pickTrigger);
        }
        scheduler.scheduleJob(orderJob, triggers, true);
    }

    /**
     * @param orderDate
     * @return 开火时间(捡漏开始时间)
     */
    public static Date generateFireTimeByOrderDate(String orderDate) {
        // 77 17 27 37 47 57 67
        Date date = DateUtils.strToDate(orderDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, -6);
        calendar.set(Calendar.HOUR_OF_DAY, 7);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date fireTime = calendar.getTime();
        Calendar now = Calendar.getInstance();
        if (fireTime.before(now.getTime())) {
            return null;
        }
        return fireTime;
    }

    /**
     * 捡漏结束时间（默认前一天晚上10点结束）
     *
     * @param orderDate
     */
    public static Date getPickEndTime(String orderDate) {
        Date date = DateUtils.strToDate(orderDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 22);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }


    private static Trigger getFireTrigger(OrderInfo orderInfo) {
        Date fireTime = generateFireTimeByOrderDate(orderInfo.getOrderDate());
        SimpleTrigger fireTrigger = null;
        if (fireTime != null) {
            fireTrigger = TriggerBuilder.newTrigger().withIdentity("fire_trigger_" + orderInfo.getUsername() + "_" + orderInfo.getOrderDate(), "group_order").withSchedule(SimpleScheduleBuilder.simpleSchedule().withRepeatCount(100).withIntervalInMilliseconds(1)).startAt(fireTime).withPriority(10).build();
        }
        return fireTrigger;
    }


    private static Trigger getPickTrigger(OrderInfo orderInfo) {
        Date pickEndTime = getPickEndTime(orderInfo.getOrderDate());
        Date startTime = generateFireTimeByOrderDate(orderInfo.getOrderDate());
        TriggerBuilder<SimpleTrigger> builder = TriggerBuilder.newTrigger().withIdentity("pick_trigger_" + orderInfo.getUsername() + "_" + orderInfo.getOrderDate(), "group_order").withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInMinutes(3)).withPriority(5).endAt(pickEndTime);
        if (startTime == null) {
            builder.startNow();
        } else {
            builder.startAt(startTime);
        }
        return builder.build();
    }

    private static JobDetail getOrderJob(OrderInfo orderInfo) {
        JobDetail jobDetail = JobBuilder.newJob(OrderJob.class).withIdentity("job_" + orderInfo.getUsername() + "_" + orderInfo.getOrderDate(), "job_group").storeDurably(true).build();
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        jobDataMap.put("orderInfo", orderInfo);
        return jobDetail;
    }

    /**
     * 真正抢号
     *
     * @param orderInfo
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws KeyManagementException
     * @throws URISyntaxException
     */
    public static void orderTask(OrderInfo orderInfo, JobExecutionContext context, OrderService orderService) throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, URISyntaxException, InterruptedException, SchedulerException {

        String xxzh = login(orderInfo);
        //获取用户信息
        String cnbh = orderInfo.getCnbh();
        String orderType = orderInfo.getTimeSlot();
        String orderDate = orderInfo.getOrderDate();
        if (StringUtils.isBlank(cnbh)) {
            //获取cnbh
            HashMap<String, String> params4 = new HashMap<>();
            params4.put("xxzh", xxzh);
            String resultStudent = HttpUtils.doHttp("get", HttpUtils.userInfoUrl, null, params4, orderInfo);
            JSONObject jsonObject = (JSONObject) JSON.parse(resultStudent);
            JSONObject data = jsonObject.getJSONObject("data");
            cnbh = data.getString("CNBH");
        }

        //查询是否有号
//        HashMap<String, String> params3 = new HashMap<>();
//        HttpUtils.addJsonpParams(params3);
//        params3.put("xxzh", xxzh);
//        String result3 = HttpUtils.doHttp("get", HttpUtils.orderQueryUrl, null, params3, orderInfo);
//        //jQuery19103597663931350108_1547188429681({
//        //  "data": null,
//        //  "code": 111,
//        //  "message": "访问太过频繁,请输入验证码！"
//        //})
//        if (result3.contains("验证码")) {
//            Thread.sleep(5000);
//            return;
//        }
//        String replace = result3.substring(result3.indexOf("\"") + 1, result3.length() - 2).replace("\\r\\n", "").replace("\\", "");
//        JSONObject jsonObject = (JSONObject) JSON.parse(replace);
//        JSONObject JSONObject2 = (JSONObject) jsonObject.get("data");
//        JSONArray uiDatas = JSONObject2.getJSONArray("UIDatas");
//        for (int i = 0; i < uiDatas.size(); i++) {
//            JSONObject o = (JSONObject) uiDatas.get(i);
//            Integer sl = o.getInteger("SL");
//            String yyrq = o.getString("Yyrq"); //2019/01/15 17:58:51
//            String xnsdPage = o.getString("Xnsd"); // 时间段 1点到5点 简称 15
//            yyrq = yyrq.substring(0, 10).replaceAll("/", "-");
//
//            if (sl <= 0 || !orderType.equals(xnsdPage) || !orderDate.equals(yyrq)) {
//                continue;
//            }

        //有号，可以预约了
        HashMap params5 = new HashMap();
        params5.put("cnbh", cnbh);
        params5.put("xxzh", xxzh);
//        params5.put("params", cnbh + "." + yyrq + "." + orderType + ".");
        params5.put("params", cnbh + "." + orderDate + "." + orderType + ".");
        params5.put("isJcsdYyMode", "1");
        HttpUtils.addJsonpParams(params5);
        String result5 = HttpUtils.doHttp("get", HttpUtils.orderUrl, null, params5, orderInfo);
        result5 = result5.substring(result5.indexOf("\"") + 1, result5.length() - 2).replace("\\r\\n", "").replace("\\", "");
        JSONObject jsonObject5 = (JSONObject) JSON.parse(result5);
        int code = jsonObject5.getInteger("code");
        if (code == 0) {
            log.info("抢到了！ " + orderDate + " " + orderType);
            orderService.updateOrderStatusSuccess(orderInfo);
            String finalXnsd = orderType;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String howTime = "";
                    if (finalXnsd.equals("15")) {
                        howTime = "下午1点到5点";
                    } else if (finalXnsd.equals("812")) {
                        howTime = "上午8点到12点";
                    } else if (finalXnsd.equals("58")) {
                        howTime = "下午5点到8点";
                    }
                    String numInWeekUpper = DateUtils.dateToWeek(orderDate);
                    EmailUtils.sendEmail("龙泉驾校约车成功", "恭喜你约到 " + orderDate + " (周" + numInWeekUpper + ") " + howTime + "的车，详情请登录学车不查看！", orderInfo);
                }
            }).start();
            //记录一下
            File orderLog = new File("orderSuccess.log");
            if (!orderLog.exists()) {
                orderLog.createNewFile();
            }
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(orderLog)));
            bw.newLine();
            bw.write(orderInfo.toString());
            bw.flush();
            //删除job
            Scheduler scheduler = context.getScheduler();
            scheduler.deleteJob(context.getJobDetail().getKey());
            return;
        }
//        }

    }

    public static String login(OrderInfo orderInfo) throws URISyntaxException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        HashMap params = new HashMap();
        HttpUtils.addJsonpParams(params);
        params.put("username", orderInfo.getUsername());
        params.put("passwordmd5", DigestUtils.md5Hex(orderInfo.getPassword()));

        //登录1
        String result = HttpUtils.doHttp("get", HttpUtils.loginUrl, null, params, orderInfo);
        //学员编号
        String xybh = result.substring(result.indexOf("XYBH") + 10, result.indexOf("\\", result.indexOf("XYBH") + 10));
        //不知道啥号，能用就行
        String jgid = result.substring(result.indexOf("JGID") + 10, result.indexOf("\\", result.indexOf("JGID") + 10));
        //又一个不知道啥的号，不管了，能用就行
        String xxzh = result.substring(result.indexOf("XXZH") + 10, result.indexOf("\\", result.indexOf("XXZH") + 10));

        //登录2,后台只判断了User-Agent。。。
        HashMap<String, String> params2 = new HashMap<>();
        params2.put("xybh", xybh);
        params2.put("password", orderInfo.getPassword());
        params2.put("jgid", jgid);
        HttpUtils.addJsonpParams(params2);
        HttpUtils.doHttp("get", HttpUtils.longquanjiaxiaoLoginUrl, null, params2, orderInfo);
        return xxzh;
    }
}
