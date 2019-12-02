package com.cmpe275.openhome.aop;

import com.cmpe275.openhome.model.PayTransaction;
import com.cmpe275.openhome.model.Property;
import com.cmpe275.openhome.model.Reservation;
import com.cmpe275.openhome.model.User;
import com.cmpe275.openhome.notification.EmailNotification;
import com.cmpe275.openhome.payload.AddPayRequest;
import com.cmpe275.openhome.payload.PostPropertyResponse;
import com.cmpe275.openhome.repository.PropertyRepository;
import com.cmpe275.openhome.repository.ReservationRepository;
import com.cmpe275.openhome.repository.UserRepository;
import com.cmpe275.openhome.util.DateUtils;
import com.cmpe275.openhome.util.SystemDateTime;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.aspectj.lang.JoinPoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;
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

    @Autowired
    ReservationRepository reservationRepository;

    @AfterReturning(pointcut = "execution(* com.cmpe275.openhome.controller.PaymentController.addPayMethod(..))",
    returning = "responseEntity")
    public void addPayNotification(JoinPoint joinPoint, ResponseEntity responseEntity){
        try{
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterReturning(pointcut = "execution(* com.cmpe275.openhome.controller.PropertyController.deleteProperty(..))")
    public void deletePropertyNotification(JoinPoint joinPoint) {
        Long propertyId = (Long) joinPoint.getArgs()[3];
        updatePropertyNotificationHelper(propertyId, "deleted");
    }


    @AfterReturning(pointcut = "execution(* com.cmpe275.openhome.controller.PropertyController.editProperty(..))")
    public void updatePropertyNotification(JoinPoint joinPoint) {
        Long propertyId = (Long) joinPoint.getArgs()[3];
        updatePropertyNotificationHelper(propertyId, "updated");
    }

    public void updatePropertyNotificationHelper(Long propertyId, String changetype) {
        System.out.println("Sending email after property is updated");
        // send email to host

        Property p = propertyRepository.findById(propertyId).orElse(null);
        if(p != null) {
            final String hostEmail = p.getOwner().getEmail();
            emailNotification.sendEmail(hostEmail, "Property update",
                    String.format("Your property %s is %s", p.getPropertyName(), changetype));
            // send email to all guests with current or future reservations.
            List<Reservation> reservations = reservationRepository.findAllByProperty(p);
            Date curDate = DateUtils.convertLocalDateTimeToDate(SystemDateTime.getCurSystemTime());
            for(Reservation r: reservations) {
                if(!r.getEndDate().before(curDate)) {
                    // enddate not before curDate, so notify the reservation
                    final String guestEmail = r.getGuest().getEmail();
                    emailNotification.sendEmail(guestEmail, "Reservation updated",
                            String.format("The host has %s the property %s. This affects your " +
                                    "reservation here from %s to %s. The new status of your " +
                                    "reservation is %s", changetype, p.getPropertyName(),
                                    DateUtils.formatForDisplay(r.getStartDate()),
                                    DateUtils.formatForDisplay(r.getEndDate()),
                                    r.getStatus().toString()));
                }
            }
        }
    }

    public void reserveNotifyHelper(Reservation r, String changetype) {
        //Reservation r = reservationRepository.findReservationById(reserveId);
        if(r != null) {
            final String hostEmail = r.getProperty().getOwner().getEmail();
            final String guestEmail = r.getGuest().getEmail();
            final String subject = String.format("Reservation %s", changetype);
            final String text = String.format("At property %s, reservation from %s to %s was %s",
                    r.getProperty().getPropertyName(),
                    DateUtils.formatForDisplay(r.getStartDate()),
                    DateUtils.formatForDisplay(r.getEndDate()),
                    changetype);
            emailNotification.sendEmail(hostEmail, subject, text);
            emailNotification.sendEmail(guestEmail, subject, text);
        }
    }

    @AfterReturning(pointcut = "execution(* com.cmpe275.openhome.service.ReservationService.createReservation(..))")
    public void createReservationNotification(JoinPoint joinPoint) {
        System.out.println("Sending email after reservation");
        Reservation reservation = (Reservation) joinPoint.getArgs()[0];
        reserveNotifyHelper(reservation, "created");
    }

    @AfterReturning(pointcut = "execution(* com.cmpe275.openhome.service.ReservationService.updateReservation(..))")
    public void updateReservationNotification(JoinPoint joinPoint) {
        System.out.println("Sending email after reservation update/delete");
        Reservation reservation = (Reservation) joinPoint.getArgs()[0];
        switch (reservation.getStatus()) {
            case checkedIn:
                reserveNotifyHelper(reservation, "check-in");
                break;
            case checkedOut:
                reserveNotifyHelper(reservation, "check-out");
                break;
            case cancelled:
                reserveNotifyHelper(reservation, "cancelled");
                break;
            case pendingHostCancelation:
                reserveNotifyHelper(reservation, "cancelled by host (Changes take effect the next reservation day)");
                break;
            default:
                reserveNotifyHelper(reservation, "changed");
        }
    }

    @AfterReturning(pointcut = "execution(* com.cmpe275.openhome.controller.PropertyController.postProperty(..))",
            returning = "re")
    public void postPropertyNotification(JoinPoint joinPoint, ResponseEntity re) {
        System.out.println("Sending email to host after property is setup");

        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterReturning(pointcut = "execution(* com.cmpe275.openhome.util.PayProcessingUtil.recordPayment(..))",
            returning = "transaction")
    public void payTransactionNotification(JoinPoint joinPoint, PayTransaction transaction) {
        System.out.println("Sending email after payment was charged");
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterReturning(pointcut = "execution(* com.cmpe275.openhome.controller.AuthController.verify(..))", returning = "re")
    public void signUpNotification(JoinPoint joinPoint, ResponseEntity re){
        System.out.println("Sending email after signing up");
        try {
            if(re.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> request = (Map<String, Object>) joinPoint.getArgs()[0];
                String authcode = (String) request.get("authcode");
                userRepository.findByAuthcode(authcode).ifPresent(user ->
                        emailNotification.sendEmail(user.getEmail(), "Welcome!",
                                "Thank you for signing up to openhome!"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
