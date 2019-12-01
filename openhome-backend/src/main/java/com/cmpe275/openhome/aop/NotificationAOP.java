package com.cmpe275.openhome.aop;

import com.cmpe275.openhome.model.Property;
import com.cmpe275.openhome.model.User;
import com.cmpe275.openhome.notification.EmailNotification;
import com.cmpe275.openhome.payload.AddPayRequest;
import com.cmpe275.openhome.repository.PropertyRepository;
import com.cmpe275.openhome.repository.UserRepository;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.aspectj.lang.JoinPoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@Configuration
@Aspect
public class NotificationAOP {
    @Autowired
    EmailNotification emailNotification;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PropertyRepository propertyRepository;

    @AfterReturning(pointcut = "execution(* com.cmpe275.openhome.controller.PaymentController.addPayMethod(..))",
    returning = "responseEntity")
    public void addPayNotification(JoinPoint joinPoint, ResponseEntity responseEntity){
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

    @AfterReturning(pointcut = "execution(* com.cmpe275.openhome.controller.PropertyController.postProperty(..))",
            returning = "property")
    public void postPropertyNotification(JoinPoint joinPoint, Property property) {
        // todo waiting on postProperty to return some data.
        System.out.println("Sending email to host after property is setup");
        // email -> hostemail
        // Subject -> New property posted
        // text -> Thank you for posting a new property to OpenHome.
    }

    @AfterReturning(pointcut = "execution(* com.cmpe275.openhome.util.PayProcessingUtil.recordPayment(..))",
            returning = "transactionId")
    public void payTransactionNotification(JoinPoint joinPoint, int transactionId) {
        //todo waiting on property Model to be setup.
        System.out.println("Sending email after payment was charged");
        // Guest email, host email, charge-type, amount, propertyname, reservation dates.
        // email -> guest email & host email
        // Subject -> Payment notification
        // Text -> You were Charged/credited $X.XX for ("" | cancellation/change penalty against)
        //          reservation of <property name> on dates <reservation-dates>
    }

    @AfterReturning(pointcut = "execution(* com.cmpe275.openhome.controller.AuthController.verify(..))", returning = "re")
    public void signUpNotification(JoinPoint joinPoint, ResponseEntity re){
        System.out.println("Sending email after signing up");
        if(re.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> request = (Map<String, Object>) joinPoint.getArgs()[0];
            String authcode = (String) request.get("authcode");
            userRepository.findByAuthcode(authcode).ifPresent(user ->
                    emailNotification.sendEmail(user.getEmail(), "Welcome!",
                            "Thank you for signing up to openhome!"));
        }
    }
}
