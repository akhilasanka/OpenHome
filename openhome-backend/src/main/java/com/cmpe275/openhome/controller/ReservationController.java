package com.cmpe275.openhome.controller;

import com.cmpe275.openhome.exception.ResourceNotFoundException;
import com.cmpe275.openhome.model.Property;
import com.cmpe275.openhome.model.Reservation;
import com.cmpe275.openhome.model.User;
import com.cmpe275.openhome.payload.ApiResponse;
import com.cmpe275.openhome.payload.ReservationCancelRequest;
import com.cmpe275.openhome.payload.ReservationCheckInRequest;
import com.cmpe275.openhome.payload.ReservationCreateRequest;
import com.cmpe275.openhome.payload.ReservationPriceRequest;
import com.cmpe275.openhome.repository.UserRepository;
import com.cmpe275.openhome.security.CurrentUser;
import com.cmpe275.openhome.security.UserPrincipal;
import com.cmpe275.openhome.service.PropertyService;
import com.cmpe275.openhome.service.ReservationService;
import com.cmpe275.openhome.util.PayProcessingUtil;
import com.cmpe275.openhome.util.DateUtils;

import java.net.URI;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RequestMapping("/api")
@RestController
public class ReservationController {

    @Autowired
    private ReservationService reservationService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PropertyService propertyService; // for testing
    
    @Autowired
    PayProcessingUtil payProcessingUtil;
    
    @PostMapping("/reservation/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createReservation(@CurrentUser UserPrincipal userPrincipal, @Valid @RequestBody ReservationCreateRequest createRequest) {
    	User guest = userRepository.findById(userPrincipal.getId())
    			.orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
    	
    	Property property = propertyService.getProperty(createRequest.getPropertyId().toString());
    	
    	// set StartDate to 3PM
    	LocalDateTime startDateTime = DateUtils.convertDateToLocalDateTime(createRequest.getStartDate());
    	startDateTime = startDateTime.withHour(15).withMinute(0).withSecond(0).withNano(0);
    	Date startDate = DateUtils.convertLocalDateTimeToDate(startDateTime);
    	
    	// set EndDate to 11AM
    	LocalDateTime endDateTime = DateUtils.convertDateToLocalDateTime(createRequest.getEndDate());
    	startDateTime = endDateTime.withHour(11).withMinute(0).withSecond(0).withNano(0);
    	Date endDate = DateUtils.convertLocalDateTimeToDate(startDateTime);
    	
    	Reservation reservation = new Reservation();
    	reservation.setProperty(property);
    	reservation.setGuest(guest);
    	reservation.setWeekdayPrice(property.getWeekdayPrice());
    	reservation.setWeekendPrice(property.getWeekendPrice());
    	reservation.setDailyParkingPrice(property.getDailyParkingFee());
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
    
    @PostMapping("/reservation/cancel")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> cancelReservation(@CurrentUser UserPrincipal userPrincipal, @Valid @RequestBody ReservationCancelRequest cancelRequest) {
    	User user = userRepository.findById(userPrincipal.getId())
    			.orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
    	
    	Reservation reservation = reservationService.findReservation(cancelRequest.getReservationId());
    	
    	boolean isGuest = reservation.getGuest().getId().equals(user.getId());
    	boolean isHost = reservation.getProperty().getOwner().getId().equals(user.getId());
        DecimalFormat df = new DecimalFormat("0.00");
    	Double penalty = new Double(0);

    	try {
    		
    		if (isGuest) {
    			penalty = reservationService.guestCancelReservation(reservation);
    		}
    		else if (isHost) {
    			penalty = reservationService.hostCancelReservation(reservation);
    		}
    		else {
    			throw new Exception("User is neither host nor guest of this reservation. Cannot cancel!");
    		}
    	}
    	catch (Exception e) {
    		return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
    	}
    	
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/reservation/cancel")
                .buildAndExpand(reservation.getId()).toUri();
        
        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "Reservation was canceled successfully! A penalty of $" + df.format(penalty.doubleValue()) + " was charged."));
    }
    
    @PostMapping("/reservation/checkIn")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> checkInReservation(@CurrentUser UserPrincipal userPrincipal, @Valid @RequestBody ReservationCheckInRequest cancelRequest) {
    	User user = userRepository.findById(userPrincipal.getId())
    			.orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
    	
    	Reservation reservation = reservationService.findReservation(cancelRequest.getReservationId());
    	boolean isGuest = reservation.getGuest().getId().equals(user.getId());

    	try {
    		if (isGuest) {
    			reservationService.checkInReservation(reservation);
    		}
    		else {
    			throw new Exception("User is not a guest of this reservation. Cannot check-in!");
    		}
    	}
    	catch (Exception e) {
    		return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
    	}
    	
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/reservation/checkIn")
                .buildAndExpand(reservation.getId()).toUri();
        
        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "Reservation was checked-in successfuly! Enjoy your stay!"));
    }
    
    @PostMapping("/reservation/checkOut")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> checkOutReservation(@CurrentUser UserPrincipal userPrincipal, @Valid @RequestBody ReservationCheckInRequest cancelRequest) {
    	User user = userRepository.findById(userPrincipal.getId())
    			.orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
    	
    	Reservation reservation = reservationService.findReservation(cancelRequest.getReservationId());
    	boolean isGuest = reservation.getGuest().getId().equals(user.getId());

    	try {
    		if (isGuest) {
    			reservationService.checkOutReservation(reservation);
    		}
    		else {
    			throw new Exception("User is not a guest of this reservation. Cannot check-out!");
    		}
    	}
    	catch (Exception e) {
    		return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
    	}
    	
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/reservation/checkOut")
                .buildAndExpand(reservation.getId()).toUri();
        
        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "Reservation was checked-out successfuly! See you next time!"));
    }
    
    @PostMapping("/reservation/priceRequest")
    @PreAuthorize("hasRole('USER')")
    public Double getReservationPrice(@CurrentUser UserPrincipal userPrincipal, @Valid @RequestBody ReservationPriceRequest priceRequest) {
    	Double totalPrice = new Double(0);
		try {
	    	Property property = propertyService.getProperty(priceRequest.getPropertyId().toString());
	    	LocalDate startDate = DateUtils.convertDateToLocalDate(priceRequest.getStartDate());
	    	LocalDate endDate = DateUtils.convertDateToLocalDate(priceRequest.getEndDate());
	    	totalPrice = payProcessingUtil.calculateTotalPrice(
	    			startDate, 
	    			endDate, 
	    			property.getWeekdayPrice(), 
	    			property.getWeekendPrice(), 
	    			property.getDailyParkingFee()
			);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
    	
		return totalPrice;
    }
    
    @GetMapping("/reservation/{reservationId}")
    @PreAuthorize("hasRole('USER')")
    public Reservation getReservation(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long reservationId) {
    	User user = userRepository.findById(userPrincipal.getId())
    			.orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
    	
    	Reservation reservation = reservationService.findReservation(reservationId);
    	
    	boolean isGuest = reservation.getGuest().getId().equals(user.getId());
    	boolean isHost = reservation.getProperty().getOwner().getId().equals(user.getId());
    	if (!isGuest && !isHost) {
    		reservation = null;
    	}
    	
		return reservation;
    }
    
}
