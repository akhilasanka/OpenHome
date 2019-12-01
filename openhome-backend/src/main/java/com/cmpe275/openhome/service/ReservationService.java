package com.cmpe275.openhome.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cmpe275.openhome.exception.ResourceNotFoundException;
import com.cmpe275.openhome.model.Reservation;
import com.cmpe275.openhome.model.ReservationStatusEnum;
import com.cmpe275.openhome.repository.ReservationRepository;
import com.cmpe275.openhome.repository.UserRepository;
import com.cmpe275.openhome.util.DateUtils;
import com.cmpe275.openhome.util.SystemDateTime;

@Component
public class ReservationService {

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    UserRepository userRepository;
    
    // ToDo: add property and properties repository or service
    // ToDo: secure service methods with Roles?
    
    public Reservation findReservation(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));
    }
    
    @Transactional(rollbackOn=Exception.class)
    public Reservation createReservation(Reservation reservation) throws Exception {
    	// check reservation date range (must be between 1 to 14 consecutive days)
    	LocalDate startDate = DateUtils.convertDateToLocalDate(reservation.getStartDate());
    	LocalDate endDate = DateUtils.convertDateToLocalDate(reservation.getEndDate());
    	long daysBetween = startDate.until(endDate, ChronoUnit.DAYS);
    	if (daysBetween < 1|| daysBetween > 14 ) {
    		throw new Exception("Reservation Date Range must be between 1 and 14 days");
    	}
    	
    	// check reservation date range (must be within 365 days from today)
    	LocalDate currentDate = SystemDateTime.getCurSystemTime().toLocalDate().plusDays(365);
    	boolean isAfter365Days = endDate.isAfter(currentDate);
    	if (isAfter365Days) {
    		throw new Exception("Reservation Dates must be within 365 days from the current date");
    	}
    	
    	// check properties are not double booked
    	List<Reservation> bookedReservations = reservationRepository.findAllReservationsForPropertyBetweenDates(
    			reservation.getProperty().getId(),
    			DateUtils.convertLocalDateToDate(startDate), 
    			DateUtils.convertLocalDateToDate(endDate.plusDays(1))
    	);
    	
    	if (!bookedReservations.isEmpty()) {
    		throw new Exception("Reservations are already booked within the specified Date Range");
    	}
    	
    	reservation.setStatus(ReservationStatusEnum.pendingCheckIn);
    	Reservation createdReservation = reservationRepository.save(reservation);
    	return createdReservation;
    }
    
    @Transactional 
    void updateReservation(Reservation reservation) {
    	findReservation(reservation.getId()); // throws exception if entity doesn't exist
    	reservationRepository.save(reservation);
    }
    
    @Transactional(rollbackOn=Exception.class)
    public void checkInReservation(Reservation reservation) throws Exception {
    	// check if "current" time is between 3pm of Start Date and Next Day
    	LocalDateTime startDate = DateUtils.convertDateToLocalDateTime(reservation.getStartDate());

    	LocalDateTime validRangeStart = startDate.withHour(15);
    	LocalDateTime validRangeEnd = startDate.plusDays(1).withHour(3);
    	
    	LocalDateTime currentSystemTime = SystemDateTime.getCurSystemTime();
    	if (currentSystemTime.isBefore(validRangeStart) || currentSystemTime.isAfter(validRangeEnd)) {
    		throw new Exception("Check Ins can only be made during between 3pm of the Start Date and 3am of the next day");
    	}
    	
    	// ToDo: charge the guest!
    	reservation.setStatus(ReservationStatusEnum.checkedIn);
    	updateReservation(reservation);
    }
    
    @Transactional
    public void checkOutReservation(Reservation reservation) {
    	// check if guest checked out with days remaining
    	// if so current day is charged fully and the other days are treated as cancelations    	
    	LocalDate currentDate = SystemDateTime.getCurSystemTime().toLocalDate();
    	LocalDate endDate = DateUtils.convertDateToLocalDate(reservation.getEndDate());

    	if (!currentDate.equals(endDate)) {
        	long daysBetween = currentDate.until(endDate, ChronoUnit.DAYS);
        	if (daysBetween == 1) {
        		// ToDo: charge 30% penalty for next day
        	}
        	else if (daysBetween > 1) {
        		// ToDo: charge 30% penalty for next day and day after that 
        	}
    	}
    	
    	reservation.setStatus(ReservationStatusEnum.checkedOut);
    	updateReservation(reservation);
    }
    
    @Transactional
    public void guestCancelReservation(Reservation reservation) {	
    	// ToDo: Decipher Cancellation logic
    	LocalDateTime currentDateTime = SystemDateTime.getCurSystemTime();
    	LocalDateTime startDateTime = DateUtils.convertDateToLocalDateTime(reservation.getStartDate());
    	LocalDateTime endDateTime = DateUtils.convertDateToLocalDateTime(reservation.getEndDate());

    	// if current time is before (Start Date - 1) @ 3PM charge nothing
    	// if cancelled with more than 24 hours ahead of start date
    	long hoursBetweenCurrentTimeAndStartTime = currentDateTime.until(startDateTime.withHour(15), ChronoUnit.HOURS); 
    	long minutesBetweenCurrentTimeAndStartTime = currentDateTime.until(startDateTime.withHour(15), ChronoUnit.MINUTES);    	
    	if (hoursBetweenCurrentTimeAndStartTime > 24) {
    		// no cancellation fee
    	}
    	else if (minutesBetweenCurrentTimeAndStartTime > 0){
        	// if current time is between (Start Date - 1) @ 3PM and Start Date 3PM charge 30% of Start Date
    	}
    	else {
        	// if current time is after Start Date @ 3PM charge 30% for Start Date and Start Date + 1 if there are multiple days
        	long daysBetween = startDateTime.toLocalDate().until(endDateTime.toLocalDate(), ChronoUnit.DAYS);
        	if (daysBetween == 1) {
        		// ToDo: charge 30% penalty for first day
        	}
        	else if (daysBetween > 1) {
        		// ToDo: charge 30% penalty for first day and day after that 
        	}
    	}
    	
    	reservation.setStatus(ReservationStatusEnum.cancelled);
    	updateReservation(reservation);
    }
    
    @Transactional
    public void hostCancelReservation(Reservation reservation) {	
    	// ToDo: Decipher Cancellation logic
    	reservation.setStatus(ReservationStatusEnum.cancelled);
    	updateReservation(reservation);
    }
    
    @Transactional
    public void checkPendingReservations() {
    	// this will be a triggered by a background job at 3am 
    	// checks 'pending' reservations where the Start Date is in the past
    	
    	// set the reservation statuses to cancelled
    	// trigger payment service charges
    	// charge 30% of reservation price for start date
    	// charge 30% of reservation price for start date + 1 if the reservation ends on start date + 2 days
    	// ToDo: implementation
    	System.out.println("I AM ALIVE!!!!!");
    }
    
    @Transactional 
    public void checkCheckedInReservations() {
    	// this will be a triggered by a background job at 11am 
    	// checks 'checkedIn' reservations where the End Date is the current day
    	
    	// set the reservation statuses to checkedOut
    	
    	// ToDo: implementation + background job caller
    	System.out.println("I AM ALIVE TOO!!!!!");
    }
}
