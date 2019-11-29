package com.cmpe275.openhome.util;

import com.cmpe275.openhome.exception.PayTransactionException;
import com.cmpe275.openhome.model.ChargeType;
import com.cmpe275.openhome.model.PayTransaction;
import com.cmpe275.openhome.model.PaymentMethod;
import com.cmpe275.openhome.model.Property;
import com.cmpe275.openhome.payload.PostPropertyRequest;
import com.cmpe275.openhome.repository.PayTransactionRepository;
import com.cmpe275.openhome.repository.PaymentMethodRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZoneId;
import java.util.Date;

/**
 * Utility class to process payments charged to guests or penalty charged to hosts.
 */
public class PropertyJsonToModelUtil {
    public static Property getProperty(PostPropertyRequest postPropertyRequest) {
        return null;
    }
}
