package com.cmpe275.openhome.aop;

import com.cmpe275.openhome.notification.EmailNotification;
import com.cmpe275.openhome.payload.SignUpRequest;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.aspectj.lang.JoinPoint;

@Configuration
@Aspect
public class NotificationAOP {
    @Autowired
    EmailNotification emailNotification;

    @AfterReturning(pointcut = "execution(* com.cmpe275.openhome.controller.AuthController.registerUser(..))")
    public void signUpNotification(JoinPoint joinPoint){
        System.out.println("Sending email after signing up");
        SignUpRequest request = (SignUpRequest) joinPoint.getArgs()[0];
        final String email = request.getEmail();
        emailNotification.sendEmail(email, "Welcome!", "Thank you for signing up to " +
                "openhome with email: " + email);
    }
}
