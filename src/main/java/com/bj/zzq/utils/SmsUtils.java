package com.bj.zzq.utils;

import ch.qos.logback.core.rolling.DefaultTimeBasedFileNamingAndTriggeringPolicy;
import com.twilio.Twilio;
import com.twilio.http.TwilioRestClient;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * @Author: zhaozhiqiang
 * @Date: 2019/2/18
 * @Description:
 */
public class SmsUtils {
    public static final String username = "zzq123456";
    public static final String secret = "d41d8cd98f00b204e980";

    public static void sendSms(String phoneNum, String sendText) throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, URISyntaxException {
        String url = "http://utf8.api.smschinese.cn/?Uid=" + username + "&Key=" + secret + "&smsMob=" + phoneNum + "&smsText=" + sendText;
        HttpUtils.doHttp("GET", url, null, null, null);
    }

    public static void main(String[] args) throws URISyntaxException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException {
        sendSms("18511914092","恭喜发财");
    }

}
