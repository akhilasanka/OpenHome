package com.cmpe275.openhome.controller;

import com.cmpe275.openhome.exception.ResourceNotFoundException;
import com.cmpe275.openhome.model.Reservation;
import com.cmpe275.openhome.model.User;
import com.cmpe275.openhome.payload.ApiResponse;
import com.cmpe275.openhome.payload.ReservationCreateRequest;
import com.cmpe275.openhome.repository.UserRepository;
import com.cmpe275.openhome.security.CurrentUser;
import com.cmpe275.openhome.security.UserPrincipal;
import com.cmpe275.openhome.service.ReservationService;

import java.net.URI;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class ReservationController {

    @Autowired
    private ReservationService reservationService;
    
    @Autowired
    private UserRepository userRepository;
    
    @PostMapping("/reservation/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> testCreate(@CurrentUser UserPrincipal userPrincipal, @Valid @RequestBody ReservationCreateRequest createRequest) {
    	User guest = userRepository.findById(userPrincipal.getId())
    			.orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
    	
    	Reservation reservation = new Reservation();
    	reservation.setGuest(guest);
    	reservation.setDailyPrice(1.0);
    	reservation.setStartDate(createRequest.getStartDate());
    	reservation.setEndDate(createRequest.getEndDate());
    	
    	reservationService.createReservation(reservation);
    	
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/reservation/create")
                .buildAndExpand(reservation.getId()).toUri();
        
        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "Reservation created successfully!"));
    }
    
}
