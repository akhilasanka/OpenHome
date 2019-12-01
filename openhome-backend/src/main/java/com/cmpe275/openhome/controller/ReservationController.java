package com.cmpe275.openhome.controller;

import com.cmpe275.openhome.exception.ResourceNotFoundException;
import com.cmpe275.openhome.model.Property;
import com.cmpe275.openhome.model.Reservation;
import com.cmpe275.openhome.model.User;
import com.cmpe275.openhome.payload.ApiResponse;
import com.cmpe275.openhome.payload.ReservationCreateRequest;
import com.cmpe275.openhome.repository.PropertyRepository;
import com.cmpe275.openhome.repository.UserRepository;
import com.cmpe275.openhome.security.CurrentUser;
import com.cmpe275.openhome.security.UserPrincipal;
import com.cmpe275.openhome.service.ReservationService;
import com.cmpe275.openhome.util.PayProcessingUtil;
import com.cmpe275.openhome.util.DateUtils;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

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
    
    @Autowired
    private PropertyRepository propertyRepository; // for testing
    
    @Autowired
    PayProcessingUtil payProcessingUtil;
    
    @PostMapping("/reservation/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> testCreate(@CurrentUser UserPrincipal userPrincipal, @Valid @RequestBody ReservationCreateRequest createRequest) {
    	User guest = userRepository.findById(userPrincipal.getId())
    			.orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
    	
    	List<Property> properties = propertyRepository.findAll();
    	
    	// set StartDate to 3PM
    	LocalDateTime startDateTime = DateUtils.convertDateToLocalDateTime(createRequest.getStartDate());
    	startDateTime = startDateTime.withHour(15).withMinute(0).withSecond(0).withNano(0);
    	Date startDate = DateUtils.convertLocalDateTimeToDate(startDateTime);
    	
    	// set EndDate to 11AM
    	LocalDateTime endDateTime = DateUtils.convertDateToLocalDateTime(createRequest.getEndDate());
    	startDateTime = endDateTime.withHour(11).withMinute(0).withSecond(0).withNano(0);
    	Date endDate = DateUtils.convertLocalDateTimeToDate(startDateTime);
    	
    	Reservation reservation = new Reservation();
    	reservation.setProperty(properties.get(0));
    	reservation.setGuest(guest);
    	reservation.setWeekdayPrice(1.0);
    	reservation.setWeekendPrice(2.5);
    	reservation.setDailyParkingPrice(0.0);
    	reservation.setStartDate(startDate);
    	reservation.setEndDate(endDate);
    	Double totalPrice = payProcessingUtil.calculateTotalPrice(
    			DateUtils.convertDateToLocalDate(reservation.getStartDate()), 
    			DateUtils.convertDateToLocalDate(reservation.getEndDate()), 
				reservation.getWeekdayPrice(), 
				reservation.getWeekendPrice(), 
				reservation.getDailyParkingPrice()
		);
    	
    	reservation.setTotalPrice(totalPrice);
    	
    	try {
        	reservationService.createReservation(reservation);    		
    	}
    	catch (Exception e) {
    		return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
    	}
    	
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/reservation/create")
                .buildAndExpand(reservation.getId()).toUri();
        
        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "Reservation created successfully!"));
    }
    
}
