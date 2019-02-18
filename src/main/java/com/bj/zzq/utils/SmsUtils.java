package com.bj.zzq.utils;

import ch.qos.logback.core.rolling.DefaultTimeBasedFileNamingAndTriggeringPolicy;
import com.twilio.Twilio;
import com.twilio.http.TwilioRestClient;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

/**
 * @Author: zhaozhiqiang
 * @Date: 2019/2/18
 * @Description:
 */
public class SmsUtils {
    public static final String ACCOUNT_SID = "ACc63e3cd3eccf69a02db167ae5241e5ec";
    public static final String AUTH_TOKEN = "cdf5cda47644ec877d8297c8f9790d95";

    /**
     * 发送短信
     *
     * @param to
     * @param content
     */
    public static void sendSms(String to, String content) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message message = Message.creator(new PhoneNumber(to),
                new PhoneNumber("+16783596025"),
                content).create();
        System.out.println(message.getSid());
    }

    public static void main(String[] args) {

        sendSms("+8618210868830", "媳妇，在干嘛呢？");
//        Authenticate Client
//        TwilioRestClient.Builder builder = new TwilioRestClient.Builder("", "");
//        TwilioRestClient client = builder.accountSid("").build();

        //Try sending yourself an SMS message, like this:
//        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
//        Message message = Message.creator(new PhoneNumber("+8618511914092"),
//                new PhoneNumber("+16783596025"),
//                "2222").create();
//
//        System.out.println(message.getSid());

        //Create A New Record
//        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
//
//        Call call = Call.creator(new PhoneNumber("+14155551212"), new PhoneNumber("+15017250604"),
//                new URI("http://demo.twilio.com/docs/voice.xml")).create();
//
//        System.out.println(call.getSid());

        //Get Existing Record
//        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
//
//        Call call = Call.fetcher("CA42ed11f93dc08b952027ffbc406d0868").fetch();
//
//        System.out.println(call.getTo());

        //Iterate Through Records
//        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
//
//        ResourceSet<Call> calls = Call.reader().read();
//
//        for (Call call : calls) {
//            System.out.println(call.getDirection());
//        }
    }

}
