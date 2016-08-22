package com.iblock.service.mail;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by baidu on 16/8/19.
 */
@Component
@Log4j
public class MailService {

    @Resource
    private JavaMailSender mailSender;
    @Value("${mail.username}")
    private String from;

    public boolean send(String subject, String[] tos, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(tos);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("mail send error!", e);
        }
        return true;
    }
}
