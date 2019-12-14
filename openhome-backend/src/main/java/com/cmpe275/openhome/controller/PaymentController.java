package com.cmpe275.openhome.controller;

import com.cmpe275.openhome.exception.PayTransactionException;
import com.cmpe275.openhome.model.ChargeType;
import com.cmpe275.openhome.model.PaymentMethod;
import com.cmpe275.openhome.model.User;
import com.cmpe275.openhome.payload.AddPayRequest;
import com.cmpe275.openhome.payload.ApiResponse;
import com.cmpe275.openhome.payload.PayMethodResponse;
import com.cmpe275.openhome.repository.PaymentMethodRepository;
import com.cmpe275.openhome.repository.UserRepository;
import com.cmpe275.openhome.security.CurrentUser;
import com.cmpe275.openhome.security.UserPrincipal;
import com.cmpe275.openhome.util.PayProcessingUtil;
import com.cmpe275.openhome.util.SystemDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class PaymentController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PaymentMethodRepository paymentMethodRepository;

    @Autowired
    PayProcessingUtil payProcessingUtil;
    @PostMapping("/pay/addpaymethod")
    public ResponseEntity<?> addPayMethod(@Valid @RequestBody AddPayRequest request) {
        if(request == null || !request.validate()) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Unparsable request"));
        }
        PaymentMethod paymentMethod = paymentMethodRepository.findByUserId(request.getUserid());
        if(paymentMethod == null)
            paymentMethod = new PaymentMethod();
        final User user = userRepository.findById(request.getUserid()).orElse(null);
        paymentMethod.parseAddPayRequest(request, user);
        final PaymentMethod result = paymentMethodRepository.save(paymentMethod);
        if(result == null) {
            return ResponseEntity.status(500).body(new ApiResponse(false, "Failed to save your payment method"));
        }
        return ResponseEntity.ok(new ApiResponse(true, "Successfully saved the payment method"));
    }

    @GetMapping("/pay/getvalidpaymentmethod")
    @PreAuthorize("hasRole('USER')")
    public PayMethodResponse getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        final PaymentMethod payMethod = paymentMethodRepository.findByUserId(userPrincipal.getId());
        LocalDateTime curDateTime = SystemDateTime.getCurSystemTime();
        int curYear = curDateTime.getYear();
        int curMonth = curDateTime.getMonth().getValue();

        if(payMethod == null || payMethod.getExpiryYear() < curYear ||
                (payMethod.getExpiryYear()==curYear && payMethod.getExpiryMonth()< curMonth)) {
        	System.out.println("NULL");
            return new PayMethodResponse(null);
        }
        else {
            return new PayMethodResponse(payMethod);
        }
    }

    // for testing only. Not to be called from FE.
    @GetMapping("/pay/insertsampletransaction")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> insertSampleTransaction(@CurrentUser UserPrincipal userPrincipal) {
        try {
            // host is charged a penalty
            payProcessingUtil.recordPayment(1L, ChargeType.HOSTPENALTY, 10.0);
            // guest is charged a penalty
            payProcessingUtil.recordPayment(2L, ChargeType.GUESTPENALTY, 100.0);
            // guest checks in & is charged
            payProcessingUtil.recordPayment(3L, ChargeType.GUESTCHECKIN, 500.0);
            return ResponseEntity.ok(null);
        } catch (PayTransactionException e) {
            return ResponseEntity.status(500).body(null);
        }
    }
    
}
