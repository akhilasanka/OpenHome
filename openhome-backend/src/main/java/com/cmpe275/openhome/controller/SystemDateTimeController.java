package com.cmpe275.openhome.controller;

import java.net.URI;
import java.time.LocalDateTime;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.cmpe275.openhome.payload.ApiResponse;
import com.cmpe275.openhome.payload.SystemDateTimeAddRequest;
import com.cmpe275.openhome.security.CurrentUser;
import com.cmpe275.openhome.security.UserPrincipal;
import com.cmpe275.openhome.service.ReservationService;
import com.cmpe275.openhome.util.SystemDateTime;

@RequestMapping("/api")
@RestController
public class SystemDateTimeController {
	
    @Autowired
    private ReservationService reservationService;
    
    @GetMapping("/system/time")
    public LocalDateTime getSystemTime() {
    	return SystemDateTime.getCurSystemTime();
    }
    
    @PostMapping("/system/addTime")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addToSysemTime(@CurrentUser UserPrincipal userPrincipal, @Valid @RequestBody SystemDateTimeAddRequest addRequest) {
    	SystemDateTime.addToOffset(addRequest.getTimeOffset());
    	
		try {
    		reservationService.checkPendingHostCancelationReservations();
			reservationService.checkPendingReservations();
    		reservationService.checkCheckedInReservations();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
    	
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/system/addTime")
                .buildAndExpand().toUri();
        
        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "Added time successfully!"));
    }
}
