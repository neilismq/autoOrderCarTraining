package com.bj.zzq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bj.zzq.utils.HttpUtils;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @Author: zhaozhiqiang
 * @Date: 2019/1/11
 * @Description:
 */
public class Main {
    public static void main(String[] args) throws ParseException {

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    doSomething();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        String dateStr = "2019-01-13 06:59:59";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(dateStr);
        timer.schedule(timerTask, date, 1000 * 60 * 60 * 24 * 1);
    }

    public static void doSomething() throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, URISyntaxException {
//        String username = "18511914092";
//        String password = "Zzq798828932";
        String username = "18210868830";
        String password = "zzq798828932";
        String cnbh = "17067";//教练编号
        String now = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String orderDate = "";
        if ("2018-01-13".equals(now)) {
            orderDate = "2019-01-19"; //yyyy-MM-dd
        } else if ("2018-01-14".equals(now)) {
            orderDate = "2019-01-20"; //yyyy-MM-dd
        }

        HashMap params = new HashMap();
        HttpUtils.addJsonpParams(params);
        params.put("username", username);
        params.put("passwordmd5", DigestUtils.md5Hex(password));

        //登录1
        String result = HttpUtils.doHttp("get", HttpUtils.loginUrl, null, params);
        //学员编号
        String xybh = result.substring(result.indexOf("XYBH") + 10, result.indexOf("\\", result.indexOf("XYBH") + 10));
        //不知道啥号，能用就行
        String jgid = result.substring(result.indexOf("JGID") + 10, result.indexOf("\\", result.indexOf("JGID") + 10));
        //又一个不知道啥的号，不管了，能用就行
        String xxzh = result.substring(result.indexOf("XXZH") + 10, result.indexOf("\\", result.indexOf("XXZH") + 10));

        //登录2,后台只判断了User-Agent。。。
        HashMap<String, String> params2 = new HashMap<String, String>();
        params2.put("xybh", xybh);
        params2.put("password", password);
        params2.put("jgid", jgid);
        HttpUtils.addJsonpParams(params2);
        HttpUtils.doHttp("get", HttpUtils.longquanjiaxiaoLoginUrl, null, params2);

        //获取用户信息
        HashMap<String, String> params4 = new HashMap<String, String>();
        //51788558
        params4.put("xxzh", xxzh);
        String result4 = HttpUtils.doHttp("get", HttpUtils.userInfoUrl, null, params4);
        boolean isOrder = false;
        int count = 0;
        while (!isOrder || count++ > 1000) {
            //查询是否有号
            HashMap<String, String> params3 = new HashMap<String, String>();
            HttpUtils.addJsonpParams(params3);
            params3.put("xxzh", xxzh);
            String result3 = HttpUtils.doHttp("get", HttpUtils.orderQueryUrl, null, params3);
            String replace = result3.substring(result3.indexOf("\"") + 1, result3.length() - 2).replace("\\r\\n", "").replace("\\", "");
            System.out.println(replace);
            JSONObject jsonObject = (JSONObject) JSON.parse(replace);
            JSONObject JSONObject2 = (JSONObject) jsonObject.get("data");
            JSONArray uiDatas = JSONObject2.getJSONArray("UIDatas");
            for (int i = 0; i < uiDatas.size(); i++) {
                JSONObject o = (JSONObject) uiDatas.get(i);
                Integer sl = o.getInteger("SL");
                String yyrq = o.getString("Yyrq"); //2019/01/15 17:58:51
                String Xnsd = o.getString("Xnsd"); // 时间段 1点到5点 简称 15
                yyrq = yyrq.substring(0, 10).replaceAll("/", "-");
                if (sl <= 0 || !"15".equals(Xnsd) || !orderDate.equals(yyrq)) {
                    continue;
                }
                //有号，可以预约了
                HashMap params5 = new HashMap();
                String xnsd = "15";//只约下午的
                params5.put("cnbh", cnbh);
                params5.put("xxzh", xxzh);
                params5.put("params", cnbh + "." + yyrq + "." + xnsd + ".");
                params5.put("isJcsdYyMode", "1");
                HttpUtils.addJsonpParams(params5);
                String result5 = HttpUtils.doHttp("get", HttpUtils.orderUrl, null, params5);
                if (result5 == null || "".equals(result5) || "undefined".equals(result5)) {
                    isOrder = true;
                    break;
                }
            }
        }
    }
}
