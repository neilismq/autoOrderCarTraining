package com.bj.zzq.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfProperties {
    public static Properties properties;
    public static String username;
    public static String password;
    public static String cnbh;
    public static String orderDate;
    public static String timeSlot;


    public static String proxyProtocol;
    public static String proxyIp;
    public static Integer proxyPort;

    public static String emailSenderAddr;
    public static String emailSenderPasswordOrAuthorizatioCode;
    public static String emailReceiverAddr;

    static {
        properties = new Properties();
        InputStream inputStream = ConfProperties.class.getClassLoader().getResourceAsStream("application.properties");
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        username = properties.getProperty("username");
        password = properties.getProperty("password");
        cnbh = properties.getProperty("cnbh");
        orderDate = properties.getProperty("orderDate");
        timeSlot = properties.getProperty("timeSlot");
        proxyProtocol = properties.getProperty("proxyProtocol");
        proxyIp = properties.getProperty("proxyIp");
        String proxyPort2 = properties.getProperty("proxyPort");
        proxyPort = (proxyPort2 == null ? 0 : Integer.valueOf(proxyPort2));
        emailSenderAddr = properties.getProperty("emailSenderAddr");
        emailSenderPasswordOrAuthorizatioCode = properties.getProperty("emailSenderPasswordOrAuthorizatioCode");
        emailReceiverAddr = properties.getProperty("emailReceiverAddr");
    }
}