package com.cmpe275.openhome.util;

import com.cmpe275.openhome.exception.PayTransactionException;
import com.cmpe275.openhome.model.ChargeType;
import com.cmpe275.openhome.model.PaymentMethod;
import com.cmpe275.openhome.repository.PaymentMethodRepository;

import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

/**
 * Utility class to process payments charged to guests or penalty charged to hosts.
 */
public class PayProcessingUtil {

    @Autowired
    PaymentMethodRepository paymentMethodRepository;
    public void recordPayment(long reservationId, ChargeType chargeType, double amount)
            throws PayTransactionException {
        LocalDateTime now = SystemDateTime.getCurSystemTime();
        if(chargeType == ChargeType.HOSTPENALTY) {
            // host is paying the guest here, so card is "HOST"
        } else {
            // fetch the card & add it to the transaction table
            final PaymentMethod paymentMethod = paymentMethodRepository.getPayByReservationId(reservationId).orElse(null);
            if(paymentMethod == null)
                throw new PayTransactionException("No Payment method for guest found");

        }
    }
}
