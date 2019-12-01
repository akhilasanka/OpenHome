package com.cmpe275.openhome.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cmpe275.openhome.exception.PayTransactionException;
import com.cmpe275.openhome.exception.ResourceNotFoundException;
import com.cmpe275.openhome.model.ChargeType;
import com.cmpe275.openhome.model.Reservation;
import com.cmpe275.openhome.model.ReservationStatusEnum;
import com.cmpe275.openhome.repository.ReservationRepository;
import com.cmpe275.openhome.repository.UserRepository;
import com.cmpe275.openhome.util.DateUtils;
import com.cmpe275.openhome.util.PayProcessingUtil;
import com.cmpe275.openhome.util.SystemDateTime;

@Component
public class ReservationService {

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    UserRepository userRepository;
    
    @Autowired
    PayProcessingUtil payProcessingUtil;
    
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
    			reservation.getStartDate(), 
    			reservation.getEndDate()
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
    	// reservation must be pending check-in
    	if (!reservation.getStatus().equals(ReservationStatusEnum.pendingCheckIn)) {
    		throw new Exception("Check-ins only allowed for Reservations that are pending check in");
    	}
    	
    	// check if "current" time is between 3pm of Start Date and Next Day
    	LocalDateTime startDate = DateUtils.convertDateToLocalDateTime(reservation.getStartDate());

    	LocalDateTime validRangeStart = startDate.withHour(15);
    	LocalDateTime validRangeEnd = startDate.plusDays(1).withHour(3);
    	
    	LocalDateTime currentSystemTime = SystemDateTime.getCurSystemTime();
    	if (currentSystemTime.isBefore(validRangeStart) || currentSystemTime.isAfter(validRangeEnd)) {
    		throw new Exception("Check-ins can only be made during between 3pm of the Start Date and 3am of the next day");
    	}
    	
    	// charge guest for total price
    	payProcessingUtil.recordPayment(reservation.getId(), ChargeType.GUESTCHECKIN, reservation.getTotalPrice());
    	reservation.setStatus(ReservationStatusEnum.checkedIn);
    	updateReservation(reservation);
    }
    
    @Transactional
    public void checkOutReservation(Reservation reservation) throws Exception {
    	// reservation must be checkedIn
    	if (!reservation.getStatus().equals(ReservationStatusEnum.checkedIn)) {
    		throw new Exception("Check-outs only allowed for Reservations that are checked-in");
    	}
    	
    	// check if guest checked out with days remaining
    	// if so current day is charged fully and the other days are treated as cancelations    	
    	LocalDate currentDate = SystemDateTime.getCurSystemTime().toLocalDate();
    	LocalDate endDate = DateUtils.convertDateToLocalDate(reservation.getEndDate());

    	// calculate refund
    	Double totalRefund = payProcessingUtil.calculateTotalPrice(
    			currentDate.plusDays(1),  // add one because current day is still charged fully
    			endDate, 
				reservation.getWeekdayPrice(), 
				reservation.getWeekendPrice(), 
				reservation.getDailyParkingPrice()
		);
    	
    	// refund guest 
    	payProcessingUtil.recordPayment(reservation.getId(), ChargeType.GUESTREFUND, totalRefund);
    	
    	// charge penalties for days remaining
    	if (!currentDate.equals(endDate)) {
        	long daysToCharge = currentDate.plusDays(1).until(endDate, ChronoUnit.DAYS);
        	if (daysToCharge == 1) {
        		// charge 30% penalty for next day
        		Double penalty = 0.30 * payProcessingUtil.calculateTotalPrice(
            			currentDate.plusDays(1),
            			endDate, 
        				reservation.getWeekdayPrice(), 
        				reservation.getWeekendPrice(), 
        				reservation.getDailyParkingPrice()
        		);
        		
            	payProcessingUtil.recordPayment(reservation.getId(), ChargeType.GUESTPENALTY, penalty);
        	}
        	else if (daysToCharge > 1) {
        		//charge 30% penalty for next day and day after that 
        		Double penalty = 0.30 * payProcessingUtil.calculateTotalPrice(
            			currentDate.plusDays(1),
            			currentDate.plusDays(3), 
        				reservation.getWeekdayPrice(), 
        				reservation.getWeekendPrice(), 
        				reservation.getDailyParkingPrice()
        		);
        		
            	payProcessingUtil.recordPayment(reservation.getId(), ChargeType.GUESTPENALTY, penalty);
        	}
    	}
    	
    	reservation.setStatus(ReservationStatusEnum.checkedOut);
    	updateReservation(reservation);
    }
    
    @Transactional
    public void guestCancelReservation(Reservation reservation) throws Exception {
    	// ToDo: Decipher Cancellation logic
    	LocalDateTime currentDateTime = SystemDateTime.getCurSystemTime();

    	LocalDateTime startDateTime = DateUtils.convertDateToLocalDateTime(reservation.getStartDate());
    	LocalDateTime endDateTime = DateUtils.convertDateToLocalDateTime(reservation.getEndDate());

    	long hoursBetweenCurrentTimeAndStartTime = currentDateTime.until(startDateTime.withHour(15), ChronoUnit.HOURS); 
    	long minutesBetweenCurrentTimeAndStartTime = currentDateTime.until(startDateTime.withHour(15), ChronoUnit.MINUTES);

    	// if cancelled with more than 24 hours ahead of start date @ 3PM charge nothing
    	if (hoursBetweenCurrentTimeAndStartTime > 24) {
    		// no cancellation fee
    	}
    	else if (minutesBetweenCurrentTimeAndStartTime > 0){
        	// if cancelled before the start date @ 3PM charge 30% for just the Start Date
    		Double penalty = 0.30 * payProcessingUtil.calculateTotalPrice(
    				startDateTime.toLocalDate(),
    				startDateTime.toLocalDate().plusDays(1), 
    				reservation.getWeekdayPrice(), 
    				reservation.getWeekendPrice(), 
    				reservation.getDailyParkingPrice()
    		);
    		
        	payProcessingUtil.recordPayment(reservation.getId(), ChargeType.GUESTPENALTY, penalty);
    	}
    	else {
        	// if cancelled after the start date @ 3PM charge 30% for just the Start Date and Start Date + 1 if applicable
        	long daysBetween = startDateTime.toLocalDate().until(endDateTime.toLocalDate(), ChronoUnit.DAYS);
        	if (daysBetween == 1) {
        		// charge 30% penalty for first day
        		Double penalty = 0.30 * payProcessingUtil.calculateTotalPrice(
        				startDateTime.toLocalDate(),
        				startDateTime.toLocalDate().plusDays(1), 
        				reservation.getWeekdayPrice(), 
        				reservation.getWeekendPrice(), 
        				reservation.getDailyParkingPrice()
        		);
        		
            	payProcessingUtil.recordPayment(reservation.getId(), ChargeType.GUESTPENALTY, penalty);
        	}
        	else if (daysBetween > 1) {
        		// charge 30% penalty for first day and day after that 
        		Double penalty = 0.30 * payProcessingUtil.calculateTotalPrice(
        				startDateTime.toLocalDate(),
        				startDateTime.toLocalDate().plusDays(2), 
        				reservation.getWeekdayPrice(), 
        				reservation.getWeekendPrice(), 
        				reservation.getDailyParkingPrice()
        		);
        		
            	payProcessingUtil.recordPayment(reservation.getId(), ChargeType.GUESTPENALTY, penalty);
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
    public void checkPendingReservations() throws Exception {
    	// this will be a triggered by a background job at 3am 
    	// checks 'pending' reservations where the Start Date is in the past
    	LocalDateTime currentDateTime = SystemDateTime.getCurSystemTime();
    	List<Reservation> pendingReservations = reservationRepository
    			.findAllPendingReservationsThatShouldBeCancelled(DateUtils.convertLocalDateTimeToDate(currentDateTime));
    	
    	for (Reservation reservation : pendingReservations) {
        	// set the reservation statuses to cancelled
        	reservation.setStatus(ReservationStatusEnum.checkedOut);
        	updateReservation(reservation);
        	
        	try {
	    		// charge 30% for just the Start Date and Start Date + 1 if applicable
	        	LocalDateTime startDateTime = DateUtils.convertDateToLocalDateTime(reservation.getStartDate());
	        	LocalDateTime endDateTime = DateUtils.convertDateToLocalDateTime(reservation.getEndDate());
	        	long daysBetween = startDateTime.toLocalDate().until(endDateTime.toLocalDate(), ChronoUnit.DAYS);
	        	if (daysBetween == 1) {
	        		// charge 30% penalty for first day
	        		Double penalty = 0.30 * payProcessingUtil.calculateTotalPrice(
	        				startDateTime.toLocalDate(),
	        				startDateTime.toLocalDate().plusDays(1), 
	        				reservation.getWeekdayPrice(), 
	        				reservation.getWeekendPrice(), 
	        				reservation.getDailyParkingPrice()
	        		);
	        		
	            	payProcessingUtil.recordPayment(reservation.getId(), ChargeType.GUESTPENALTY, penalty);
	        	}
	        	else if (daysBetween > 1) {
	        		// charge 30% penalty for first day and day after that 
	        		Double penalty = 0.30 * payProcessingUtil.calculateTotalPrice(
	        				startDateTime.toLocalDate(),
	        				startDateTime.toLocalDate().plusDays(2), 
	        				reservation.getWeekdayPrice(), 
	        				reservation.getWeekendPrice(), 
	        				reservation.getDailyParkingPrice()
	        		);
	        		
	            	payProcessingUtil.recordPayment(reservation.getId(), ChargeType.GUESTPENALTY, penalty);
	        	}
        	}
        	catch (PayTransactionException pte) {
        		pte.printStackTrace();
        	}
    	}
    }
    
    @Transactional 
    public void checkCheckedInReservations() throws Exception {
    	// this will be a triggered by a background job at 11am 
    	// checks 'checkedIn' reservations where the End Date is the current day
    	LocalDateTime currentDateTime = SystemDateTime.getCurSystemTime();
    	List<Reservation> checkedInReservations = reservationRepository
    			.findAllCheckedInReservationsThatShouldBeCheckedOut(DateUtils.convertLocalDateTimeToDate(currentDateTime));
    	
    	// set the reservation statuses to checkedOut
    	for (Reservation reservation : checkedInReservations) {
        	// set the reservation statuses to cancelled
        	reservation.setStatus(ReservationStatusEnum.checkedOut);
        	updateReservation(reservation);
    	}
    }
}
