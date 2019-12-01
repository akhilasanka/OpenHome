package com.cmpe275.openhome.util;

import com.cmpe275.openhome.exception.PayTransactionException;
import com.cmpe275.openhome.model.ChargeType;
import com.cmpe275.openhome.model.PayTransaction;
import com.cmpe275.openhome.model.PaymentMethod;
import com.cmpe275.openhome.repository.PayTransactionRepository;
import com.cmpe275.openhome.repository.PaymentMethodRepository;
import com.cmpe275.openhome.repository.ReservationRepository;

import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZoneId;
import java.util.Date;

/**
 * Utility class to process payments charged to guests or penalty charged to hosts.
 */
public class PayProcessingUtil {

    @Autowired
    PaymentMethodRepository paymentMethodRepository;

    @Autowired
    PayTransactionRepository payTransactionRepository;

    @Autowired
    ReservationRepository reservationRepository;

    public Integer recordPayment(long reservationId, ChargeType chargeType, double amount)
            throws PayTransactionException {

        PayTransaction transaction = new PayTransaction();
        transaction.setReservation(reservationRepository.findReservationById(reservationId));
        transaction.setChargeType(chargeType);
        transaction.setAmount(amount);
        transaction.setTransactionDate(Date.from( SystemDateTime.getCurSystemTime()
                .atZone( ZoneId.systemDefault()).toInstant()));
        if(chargeType == ChargeType.HOSTPENALTY) {
            // host is paying the guest here, so card is "HOST"
            transaction.setCardUsed("HOST");
        } else {
            // fetch the card & add it to the transaction table
            final PaymentMethod paymentMethod = paymentMethodRepository.getPayByReservationId(reservationId);
            if(paymentMethod == null)
                throw new PayTransactionException("No Payment method for guest found");
            transaction.setCardUsed(paymentMethod.getCardEnding());
        }

        return payTransactionRepository.save(transaction).getTransactionId();
    }
}
