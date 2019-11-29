package com.cmpe275.openhome.config;

import com.cmpe275.openhome.notification.EmailNotification;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;

@Configuration
public class SMTPConfig {
    @Bean
    public Session session(){
        final String username = "openhomecmpe275@gmail.com";
        final String password = "OpenHome123";

        final Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true"); //TLS

        return Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
    }

    @Bean
    public EmailNotification emailNotification(Session session) {
        return new EmailNotification(session);
    }
}
