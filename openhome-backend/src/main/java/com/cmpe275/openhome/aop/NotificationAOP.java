package com.cmpe275.openhome.aop;

import com.cmpe275.openhome.model.User;
import com.cmpe275.openhome.notification.EmailNotification;
import com.cmpe275.openhome.payload.AddPayRequest;
import com.cmpe275.openhome.payload.SignUpRequest;
import com.cmpe275.openhome.repository.UserRepository;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.aspectj.lang.JoinPoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Configuration
@Aspect
public class NotificationAOP {
    @Autowired
    EmailNotification emailNotification;

    @Autowired
    UserRepository userRepository;

    @AfterReturning(pointcut = "execution(* com.cmpe275.openhome.controller.PaymentController.addPayMethod(..))",
    returning = "responseEntity")
    public void signUpNotification(JoinPoint joinPoint, ResponseEntity responseEntity){
        if(responseEntity.getStatusCode() == HttpStatus.OK) {
            System.out.println("Send notification on adding a payment method");
            AddPayRequest request = (AddPayRequest) joinPoint.getArgs()[0];
            User user = userRepository.findById(request.getUserid()).orElse(null);
            if(user != null) {
                final String email = user.getEmail();
                final String cardEnding = request.getCardNumber().substring(12,16);
                emailNotification.sendEmail(email, "New Payment method added",
                        "A new payment method was added to your OpenHome account. " +
                                "It is a card number ending in " + cardEnding);
            }
        }
    }

    @AfterReturning(pointcut = "execution(* com.cmpe275.openhome.controller.AuthController.registerUser(..))")
    public void signUpNotification(JoinPoint joinPoint){
        System.out.println("Sending email after signing up");
        SignUpRequest request = (SignUpRequest) joinPoint.getArgs()[0];
        final String email = request.getEmail();
        emailNotification.sendEmail(email, "Welcome!", "Thank you for signing up to " +
                "openhome with email: " + email);
    }
}
