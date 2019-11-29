package com.cmpe275.openhome.controller;

import com.cmpe275.openhome.model.PaymentMethod;
import com.cmpe275.openhome.payload.AddPayRequest;
import com.cmpe275.openhome.payload.ApiResponse;
import com.cmpe275.openhome.payload.PayMethodResponse;
import com.cmpe275.openhome.repository.PaymentMethodRepository;
import com.cmpe275.openhome.security.CurrentUser;
import com.cmpe275.openhome.security.UserPrincipal;
import com.cmpe275.openhome.util.SystemDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

import javax.validation.Valid;

@RestController
@RequestMapping("/pay")
public class PaymentController {
    @Autowired
    PaymentMethodRepository paymentMethodRepository;
    @PostMapping("/addpaymethod")
    public ResponseEntity<?> addPayMethod(@Valid @RequestBody AddPayRequest request) {
        if(request == null || !request.validate()) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Unparsable request"));
        }
        final PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.parseAddPayRequest(request);
        final PaymentMethod result = paymentMethodRepository.save(paymentMethod);
        if(result == null) {
            return ResponseEntity.status(500).body(new ApiResponse(false, "Failed to save your payment method"));
        }
        return ResponseEntity.ok(new ApiResponse(true, "Successfully saved the payment method"));
    }

    @GetMapping("/getvalidpaymentmethod")
    @PreAuthorize("hasRole('USER')")
    public PayMethodResponse getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        final PaymentMethod payMethod = paymentMethodRepository.findById(userPrincipal.getId()).orElse(null);
        LocalDateTime curDateTime = SystemDateTime.getCurSystemTime();
        int curYear = curDateTime.getYear();
        int curMonth = curDateTime.getMonth().getValue();
        if(payMethod == null || payMethod.getExpiryYear() < curYear ||
                (payMethod.getExpiryYear()==curYear && payMethod.getExpiryMonth()< curMonth))
            return new PayMethodResponse(null);
        else
            return new PayMethodResponse(payMethod);
    }
}
