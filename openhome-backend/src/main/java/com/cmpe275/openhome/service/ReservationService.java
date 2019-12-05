package com.cmpe275.openhome.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cmpe275.openhome.exception.PayTransactionException;
import com.cmpe275.openhome.exception.ResourceNotFoundException;
import com.cmpe275.openhome.model.ChargeType;
import com.cmpe275.openhome.model.Property;
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
    
    @Autowired
    PropertyService propertyService;
    
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
    		throw new Exception("Reservation Date Range must be between 1 and 14 days.");
    	}
    	
    	// check reservation date range (must be within 365 days from today)
    	LocalDate currentDate = SystemDateTime.getCurSystemTime().toLocalDate().plusDays(365);
    	boolean isAfter365Days = endDate.isAfter(currentDate);
    	if (isAfter365Days) {
    		throw new Exception("Reservation Dates must be within 365 days from the current date");
    	}
    	
    	// check date is valid range
    	Property property = reservation.getProperty();
    	if (!propertyService.isDateRangeValid(property, startDate, endDate)) {
    		throw new Exception("Reservation date range is not valid.");
    	}
    	
    	// check if there are any conflicting reservations to prevent double booking
    	Date startDateAt3PM = DateUtils.convertLocalDateTimeToDate(startDate.atTime(15, 0));
    	Date endDateAt11AM = DateUtils.convertLocalDateTimeToDate(endDate.atTime(11, 0));

    	List<Reservation> pending = reservationRepository.findConflictingReservationsThatArePendingCheckIn(property, startDateAt3PM, endDateAt11AM);
    	List<Reservation> checkedIn = reservationRepository.findConflictingReservationsThatAreCheckedIn(property, startDateAt3PM, endDateAt11AM);
    	List<Reservation> canceledAuto = reservationRepository.findConflictingReservationsThatWereCanceledAutomatically(property, startDateAt3PM, endDateAt11AM);
    	List<Reservation> canceledByGuestAfterCheckIn = reservationRepository.findConflictingReservationsThatWereCanceledByGuestAfterCheckIn(property, startDateAt3PM, endDateAt11AM);
    	List<Reservation> canceledByHostAfterCheckIn = reservationRepository.findConflictingReservationsThatWereCanceledByHostAfterCheckIn(property, startDateAt3PM, endDateAt11AM);
    	List<Reservation> pendingCancelationByHost = reservationRepository.findConflictingReservationsThatArePendingCancelationByHost(property, startDateAt3PM, endDateAt11AM);

    	boolean hasNoConflict = pending.isEmpty() && checkedIn.isEmpty() && canceledAuto.isEmpty() && canceledByGuestAfterCheckIn.isEmpty() && canceledByHostAfterCheckIn.isEmpty() && pendingCancelationByHost.isEmpty();
    	if (!hasNoConflict) {
    		throw new Exception("Reservations are already booked within the specified Date Range.");
    	}
    	
    	reservation.setStatus(ReservationStatusEnum.pendingCheckIn);
    	Reservation createdReservation = reservationRepository.save(reservation);
    	return createdReservation;
    }

    // made public for AOP;
    @Transactional 
    public void updateReservation(Reservation reservation) {
    	findReservation(reservation.getId()); // throws exception if entity doesn't exist
    	reservationRepository.save(reservation);
    }
    
    @Transactional(rollbackOn=Exception.class)
    public void checkInReservation(Reservation reservation) throws Exception {
    	// reservation must be pending check-in
    	if (!reservation.getStatus().equals(ReservationStatusEnum.pendingCheckIn)) {
    		throw new Exception("Check-ins are only allowed for reservations that are pending check in.");
    	}
    	
    	// check if "current" time is between 3pm of Start Date and Next Day
    	LocalDateTime startDate = DateUtils.convertDateToLocalDateTime(reservation.getStartDate());

    	LocalDateTime validRangeStart = startDate.withHour(15);
    	LocalDateTime validRangeEnd = startDate.plusDays(1).withHour(3);
    	
    	LocalDateTime currentSystemTime = SystemDateTime.getCurSystemTime();
    	if (currentSystemTime.isBefore(validRangeStart) || currentSystemTime.isAfter(validRangeEnd)) {
    		throw new Exception("Check-ins can only be made during between 3pm of the Start Date and 3am of the next day.");
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
    		throw new Exception("Check-outs are only allowed for Reservations that are checked-in.");
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
        	
        	// set status to cancelled and set the actual checkout date
        	reservation.setStatus(ReservationStatusEnum.guestCanceledAfterCheckIn);
        	Date actualCheckOutDate = DateUtils.convertLocalDateTimeToDate(currentDate.plusDays(1).atTime(11, 0));
        	reservation.setCheckOutDate(actualCheckOutDate);
    	}
    	else {
        	reservation.setStatus(ReservationStatusEnum.checkedOut);
        	reservation.setCheckOutDate(reservation.getEndDate());
    	}
    	
    	updateReservation(reservation);
    }
    
    @Transactional
    public Double guestCancelReservation(Reservation reservation) throws Exception {
    	Double penalty = new Double(0);
    	// reservation must not be canceled already
    	List<ReservationStatusEnum> invalidStatuses = new ArrayList<ReservationStatusEnum>();
    	invalidStatuses.add(ReservationStatusEnum.guestCanceledBeforeCheckIn);
    	invalidStatuses.add(ReservationStatusEnum.guestCanceledAfterCheckIn);
    	invalidStatuses.add(ReservationStatusEnum.hostCanceledBeforeCheckIn);
    	invalidStatuses.add(ReservationStatusEnum.hostCanceledAfterCheckIn);
    	invalidStatuses.add(ReservationStatusEnum.automaticallyCanceled);
    	invalidStatuses.add(ReservationStatusEnum.pendingHostCancelation);
    	
    	if (invalidStatuses.contains(reservation.getStatus())) {
    		throw new Exception("Reservation has already been canceled or is pending cancelation.");
    	}
    	
    	// ToDo: Decipher Cancellation logic
    	LocalDateTime currentDateTime = SystemDateTime.getCurSystemTime();

    	LocalDateTime startDateTime = DateUtils.convertDateToLocalDateTime(reservation.getStartDate());
    	LocalDateTime endDateTime = DateUtils.convertDateToLocalDateTime(reservation.getEndDate());

    	long hoursBetweenCurrentTimeAndStartTime = currentDateTime.until(startDateTime.withHour(15), ChronoUnit.HOURS); 
    	long minutesBetweenCurrentTimeAndStartTime = currentDateTime.until(startDateTime.withHour(15), ChronoUnit.MINUTES);

    	// if cancelled with more than 24 hours ahead of start date @ 3PM charge nothing
    	if (hoursBetweenCurrentTimeAndStartTime > 24) {
    		// no cancellation fee
        	reservation.setStatus(ReservationStatusEnum.guestCanceledBeforeCheckIn);
    	}
    	else if (minutesBetweenCurrentTimeAndStartTime > 0){
        	// if cancelled before the start date @ 3PM charge 30% for just the Start Date
    		penalty = 0.30 * payProcessingUtil.calculateTotalPrice(
    				startDateTime.toLocalDate(),
    				startDateTime.toLocalDate().plusDays(1), 
    				reservation.getWeekdayPrice(), 
    				reservation.getWeekendPrice(), 
    				reservation.getDailyParkingPrice()
    		);
    		
        	payProcessingUtil.recordPayment(reservation.getId(), ChargeType.GUESTPENALTY, penalty);
        	reservation.setStatus(ReservationStatusEnum.guestCanceledBeforeCheckIn);
    	}
    	else {
        	// if cancelled after the start date @ 3PM charge 30% for just the Start Date and Start Date + 1 if applicable
        	long daysBetween = currentDateTime.toLocalDate().until(endDateTime.toLocalDate(), ChronoUnit.DAYS);
        	if (daysBetween == 1) {
        		// charge 30% penalty for first day
        		penalty = 0.30 * payProcessingUtil.calculateTotalPrice(
        				currentDateTime.toLocalDate(),
        				startDateTime.toLocalDate().plusDays(1), 
        				reservation.getWeekdayPrice(), 
        				reservation.getWeekendPrice(), 
        				reservation.getDailyParkingPrice()
        		);
        		
            	payProcessingUtil.recordPayment(reservation.getId(), ChargeType.GUESTPENALTY, penalty);
        	}
        	else if (daysBetween > 1) {
        		// charge 30% penalty for first day and day after that 
        		penalty = 0.30 * payProcessingUtil.calculateTotalPrice(
        				currentDateTime.toLocalDate(),
        				startDateTime.toLocalDate().plusDays(2), 
        				reservation.getWeekdayPrice(), 
        				reservation.getWeekendPrice(), 
        				reservation.getDailyParkingPrice()
        		);
        		
            	payProcessingUtil.recordPayment(reservation.getId(), ChargeType.GUESTPENALTY, penalty);
        	}
        	
        	// set status to cancelled and set the actual checkout date
        	reservation.setStatus(ReservationStatusEnum.guestCanceledAfterCheckIn);
        	Date actualCheckOutDate = DateUtils.convertLocalDateTimeToDate(currentDateTime.plusDays(1).toLocalDate().atTime(11, 0));
        	reservation.setCheckOutDate(actualCheckOutDate);
        	
    	}
       	updateReservation(reservation);
       	
       	return penalty;
    }
    
    @Transactional
    public Double hostCancelReservation(Reservation reservation) throws Exception {
    	Double penalty = new Double(0.0);
    	
    	List<ReservationStatusEnum> invalidStatuses = new ArrayList<ReservationStatusEnum>();
    	invalidStatuses.add(ReservationStatusEnum.guestCanceledBeforeCheckIn);
    	invalidStatuses.add(ReservationStatusEnum.guestCanceledAfterCheckIn);
    	invalidStatuses.add(ReservationStatusEnum.hostCanceledBeforeCheckIn);
    	invalidStatuses.add(ReservationStatusEnum.hostCanceledAfterCheckIn);
    	invalidStatuses.add(ReservationStatusEnum.automaticallyCanceled);
    	invalidStatuses.add(ReservationStatusEnum.pendingHostCancelation);
    	
    	if (invalidStatuses.contains(reservation.getStatus())) {
    		throw new Exception("Reservation has already been canceled or is pending cancelation.");
    	}
    	/*
    	 * A host can cancel any future reservation or the remaining days a reservation for guests who already checked in.
		   If a cancelled day is within 7 days from now, host pays 15% penalty of the total fee for that day, including parking.
           If a guest has checked in, then the earliest cancellation by the host that can take effect is the closest 3PM in the future. E.g, the guest checked in at 5pm, with 8 night to stay. The host initiates the cancellation at 10pm, then the cancellation will take effect at 3pm the next day, i.e., the guest can stay until 3pm the next day. The guest pays for one night, gets money back for the remaining 7 nights, and 15% compensation for 6 nights. 
    	 */
    	
    	LocalDateTime currentDateTime = SystemDateTime.getCurSystemTime();
    	LocalDateTime currentDateTimePlus7Days = currentDateTime.plusDays(7);
    	
    	LocalDateTime startDateTime = DateUtils.convertDateToLocalDateTime(reservation.getStartDate());
    	LocalDateTime endDateTime = DateUtils.convertDateToLocalDateTime(reservation.getEndDate());

    	boolean guestHasCheckedIn = reservation.getStatus().equals(ReservationStatusEnum.checkedIn);
    	// guest has not checked in and cancellation can take place immediately
    	if (!guestHasCheckedIn) {
    		// set status to canceled
        	reservation.setStatus(ReservationStatusEnum.hostCanceledBeforeCheckIn);
        	updateReservation(reservation);
        	
        	// check if there is an overlap between the dates
        	boolean datesOverlap = startDateTime.isBefore(currentDateTimePlus7Days) && currentDateTime.isBefore(endDateTime);
        	if (datesOverlap) {
    			// if there are days within the 7 day range, host needs to pay 15% fee
    			
    			// determine if Reservation End Date is before 7 Day Period
    			// pick the one that ends sooner
    			LocalDate penaltyEndDate;
    			if (currentDateTimePlus7Days.toLocalDate().isBefore(endDateTime.toLocalDate())) {
    				penaltyEndDate = currentDateTimePlus7Days.toLocalDate();
    			}
    			else {
    				penaltyEndDate = endDateTime.toLocalDate();
    			}
    			
        		penalty = 0.15 * payProcessingUtil.calculateTotalPrice(
        				startDateTime.toLocalDate(),
        				penaltyEndDate, 
        				reservation.getWeekdayPrice(), 
        				reservation.getWeekendPrice(), 
        				reservation.getDailyParkingPrice()
        		);
        		
            	payProcessingUtil.recordPayment(reservation.getId(), ChargeType.HOSTPENALTY, penalty);
    		}
    	}
    	// guest has checked in and cancellation can take place the next day
    	else {
    		// set cancelation to current date + 1
    		Date cancelationDate = DateUtils.convertLocalDateToDate(currentDateTime.toLocalDate().plusDays(1));
    		reservation.setHostCancelationDate(cancelationDate);
        	reservation.setStatus(ReservationStatusEnum.pendingHostCancelation);
        	
        	Date actualCheckOutDate = DateUtils.convertLocalDateTimeToDate(currentDateTime.plusDays(1).toLocalDate().atTime(11, 0));
        	reservation.setCheckOutDate(actualCheckOutDate);
        	updateReservation(reservation); 
        	
    		// refund guest remainder of days
        	Double totalRefund = payProcessingUtil.calculateTotalPrice(
        			currentDateTime.toLocalDate().plusDays(1),  // add one day
        			endDateTime.toLocalDate(), 
    				reservation.getWeekdayPrice(), 
    				reservation.getWeekendPrice(), 
    				reservation.getDailyParkingPrice()
    		);
        	payProcessingUtil.recordPayment(reservation.getId(), ChargeType.GUESTREFUND, totalRefund);
        	
        	// charge penalty to host 
			LocalDate penaltyEndDate;
			if (currentDateTimePlus7Days.toLocalDate().isBefore(endDateTime.toLocalDate())) {
				penaltyEndDate = currentDateTimePlus7Days.toLocalDate();
			}
			else {
				penaltyEndDate = endDateTime.toLocalDate();
			}
			
    		penalty = 0.15 * payProcessingUtil.calculateTotalPrice(
    				currentDateTime.toLocalDate().plusDays(1),
    				penaltyEndDate, 
    				reservation.getWeekdayPrice(), 
    				reservation.getWeekendPrice(), 
    				reservation.getDailyParkingPrice()
    		);
    		
        	payProcessingUtil.recordPayment(reservation.getId(), ChargeType.HOSTPENALTY, penalty);
        	
    	}
    	
    	return penalty;
    }

	public List<Reservation> findAllReservationsBetweenDates(Date startDate, Date endDate) {
		return reservationRepository.findAllReservationsBetweenDates(startDate, endDate);
	}

	// For search START ************

	public List<Reservation> findAllReservationsPendingBasedOnEndDate(Date startDate, Date endDate){
    	List statuses = new ArrayList();
    	statuses.add(ReservationStatusEnum.pendingCheckIn);
    	statuses.add(ReservationStatusEnum.checkedIn);
		return reservationRepository.findAllReservationsBasedOnEndDate(startDate, endDate, statuses);
	}

	public List<Reservation> findAllReservationsPendingBasedOnCheckoutDate(Date startDate, Date endDate){
		List statuses = new ArrayList();
		statuses.add(ReservationStatusEnum.automaticallyCanceled);
		statuses.add(ReservationStatusEnum.guestCanceledAfterCheckIn);
		statuses.add(ReservationStatusEnum.hostCanceledAfterCheckIn);
		statuses.add(ReservationStatusEnum.pendingHostCancelation);

		return reservationRepository.findAllReservationsBasedOnCheckoutDate(startDate, endDate, statuses);
	}

	// For search END **************
    
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
        	// set the reservation statuses to automatically cancelled and set end date
        	reservation.setStatus(ReservationStatusEnum.automaticallyCanceled);
        	
        	Date actualCheckOutDate = DateUtils.convertLocalDateTimeToDate(DateUtils.convertDateToLocalDate(reservation.getStartDate()).plusDays(1).atTime(11, 0));
        	reservation.setCheckOutDate(actualCheckOutDate);
        	updateReservation(reservation);
    	}
    }
    
    @Transactional 
    public void checkPendingHostCancelationReservations() throws Exception {
    	// this will be a triggered by a background job at 3pm 
    	// checks 'pendingHostCancelation' that Host has initiated cancel request for 
    	List<Reservation> pendingHostCancelationReservations = reservationRepository
    			.findAllReservationsThatShouldBeCanceled();
    	
    	// set the reservation statuses to checkedOut
    	for (Reservation reservation : pendingHostCancelationReservations) {
        	// set the reservation statuses to cancelled
        	reservation.setStatus(ReservationStatusEnum.hostCanceledAfterCheckIn);
        	updateReservation(reservation);
        	
        	LocalDateTime cancelationDate = DateUtils.convertDateToLocalDateTime(reservation.getHostCancelationDate());
        	LocalDateTime cancelationDateTimePlus6Days = cancelationDate.plusDays(6);

        	LocalDateTime endDateTime = DateUtils.convertDateToLocalDateTime(reservation.getEndDate());
        	
        	// calculate refund to guest
        	Double totalRefund = payProcessingUtil.calculateTotalPrice(
        			cancelationDate.toLocalDate(),  // add one because current day is still charged fully
        			endDateTime.toLocalDate(), 
    				reservation.getWeekdayPrice(), 
    				reservation.getWeekendPrice(), 
    				reservation.getDailyParkingPrice()
    		);
        	payProcessingUtil.recordPayment(reservation.getId(), ChargeType.GUESTREFUND, totalRefund);

        	// calculate penalty for host
			// determine if Reservation End Date is before 6 Day Period
			// pick the one that ends sooner
			LocalDate penaltyEndDate;
			if (cancelationDateTimePlus6Days.toLocalDate().isBefore(endDateTime.toLocalDate())) {
				penaltyEndDate = cancelationDateTimePlus6Days.toLocalDate();
			}
			else {
				penaltyEndDate = endDateTime.toLocalDate();
			}
			
    		Double penalty = 0.15 * payProcessingUtil.calculateTotalPrice(
    				cancelationDate.toLocalDate(),
    				penaltyEndDate, 
    				reservation.getWeekdayPrice(), 
    				reservation.getWeekendPrice(), 
    				reservation.getDailyParkingPrice()
    		);
    		
        	payProcessingUtil.recordPayment(reservation.getId(), ChargeType.HOSTPENALTY, penalty);
    	}


    }
}
