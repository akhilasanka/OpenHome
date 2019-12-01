package com.cmpe275.openhome.aop;

import com.cmpe275.openhome.model.PayTransaction;
import com.cmpe275.openhome.model.Property;
import com.cmpe275.openhome.model.User;
import com.cmpe275.openhome.notification.EmailNotification;
import com.cmpe275.openhome.payload.AddPayRequest;
import com.cmpe275.openhome.payload.PostPropertyResponse;
import com.cmpe275.openhome.repository.PropertyRepository;
import com.cmpe275.openhome.repository.UserRepository;
import com.cmpe275.openhome.util.DateUtils;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.aspectj.lang.JoinPoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.SimpleDateFormat;
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
            returning = "re")
    public void postPropertyNotification(JoinPoint joinPoint, ResponseEntity re) {
        System.out.println("Sending email to host after property is setup");
        PostPropertyResponse ppr = (PostPropertyResponse) re.getBody();
        if(ppr != null) {
            Long propId = ppr.getPropertyId();
            Property p = propertyRepository.findById(propId).orElse(null);
            if (p != null) {
                final String email = p.getOwner().getEmail();
                final String subject = "New property posted";
                final String text = "Thank you for posting a new property at " + String.format("%s, %s, %s",
                        p.getAddressStreet(), p.getAddressCity(), p.getAddressState());
                emailNotification.sendEmail(email, subject, text);
            }
        }
    }

    @AfterReturning(pointcut = "execution(* com.cmpe275.openhome.util.PayProcessingUtil.recordPayment(..))",
            returning = "transaction")
    public void payTransactionNotification(JoinPoint joinPoint, PayTransaction transaction) {
        System.out.println("Sending email after payment was charged");
        final String guestEmail = transaction.getReservation().getGuest().getEmail();
        final String hostEmail = transaction.getReservation().getProperty().getOwner().getEmail();
        final String subject = "payment notification";
        final String textTemplate = "You were %s " +
                String.format("$%.2f on %s for ", transaction.getAmount(),
                        DateUtils.formatForDisplay(transaction.getTransactionDate()))
                + "%s" + String.format("reservation of %s on dates %s to %s",
                    transaction.getReservation().getProperty().getPropertyName(),
                    DateUtils.formatForDisplay(transaction.getReservation().getStartDate()),
                    DateUtils.formatForDisplay(transaction.getReservation().getEndDate()));
        final String charged = "Charged", credited = "Credited",
                cancel = "cancellation/change penalty on ", checkin = "checkin on ", refund = "refund on";
        String guestText = "";
        String hostText = "";
        switch (transaction.getChargeType()) {
            case GUESTPENALTY:
                guestText = String.format(textTemplate, charged, cancel);
                hostText = String.format(textTemplate, credited, cancel);
                break;
            case GUESTCHECKIN:
                guestText = String.format(textTemplate, charged, checkin);
                hostText = String.format(textTemplate, credited, checkin);
                break;
            case HOSTPENALTY:
                guestText = String.format(textTemplate, credited, cancel);
                hostText = String.format(textTemplate, charged, cancel);
                break;
            case GUESTREFUND:
                guestText = String.format(textTemplate, credited, refund);
                hostText = String.format(textTemplate, charged, refund);
                break;
        }
        emailNotification.sendEmail(guestEmail, subject, guestText);
        emailNotification.sendEmail(hostEmail, subject, hostText);
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
