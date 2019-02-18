package com.bj.zzq.utils;

import org.apache.log4j.Logger;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * @Author: zhaozhiqiang
 * @Date: 2019/1/15
 * @Description:
 */
public class EmailUtils {

    private static Logger log = Logger.getLogger(EmailUtils.class);

    public static void sendEmail(String title, String content, String receive) {
        try {
            Properties properties = new Properties();
            String mailHost = "smtp.163.com";
            properties.setProperty("mail.host", mailHost);
            //设置用户的认证方式
            properties.setProperty("mail.smtp.auth", "true");
            //设置传输协议
            properties.setProperty("mail.transport.protocol", "smtp");
            //获取连接
            Session session = Session.getInstance(properties);
            //session.setDebug(true);
            //创建消息
            MimeMessage message = new MimeMessage(session);
            //发件人
            message.setFrom(new InternetAddress(PropertiesLoader.emailSenderAddr));
            //收件人地址
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(receive));
            //标题
            message.setSubject(title, "utf-8");
            message.setContent(content, "text/html;charset=utf-8");
            //发送日期
            //message.setSentDate(new Date());
            //根据session对象获取邮件传输对象Transport
            Transport transport = session.getTransport();
            //设置发件人的账户名和密码
            transport.connect(PropertiesLoader.emailReceiverAddr, PropertiesLoader.emailSenderPasswordOrAuthorizatioCode);
            //发送邮件，并发送到所有收件人地址，message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
            transport.sendMessage(message, message.getAllRecipients());
            //关闭邮件连接
            transport.close();
            log.info("发送邮件成功");
        } catch (MessagingException e) {
            log.info("发送邮件失败", e);
        }
    }
}
