package com.cmpe275.openhome.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cmpe275.openhome.exception.ResourceNotFoundException;
import com.cmpe275.openhome.model.Reservation;
import com.cmpe275.openhome.model.ReservationStatusEnum;
import com.cmpe275.openhome.repository.ReservationRepository;
import com.cmpe275.openhome.repository.UserRepository;

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
    
    @Transactional
    public Reservation createReservation(Reservation reservation) {
    	// ToDo: validate before creating
    	// check reservation date range (must be between 1 to 14 consecutive days)
    	// check reservation date rage (must be within 365 days from today)
    	// check properties are not double booked
    	// throw Exception for any of the above and reservation is not created
    	
    	reservation.setStatus(ReservationStatusEnum.pendingCheckIn);
    	Reservation createdReservation = reservationRepository.save(reservation);
    	return createdReservation;
    }
    
    @Transactional 
    void updateReservation(Reservation reservation) {
    	findReservation(reservation.getId()); // throws exception if entity doesn't exist
    	reservationRepository.save(reservation);
    }
    
    @Transactional
    public void checkInReservation(Reservation reservation) {
    	// ToDo: validate before check-in
    	// check if "current" time is between 3pm of Start Date and Next Day
    	// throw Exception if the above is not true
    	
    	reservation.setStatus(ReservationStatusEnum.checkedIn);
    	updateReservation(reservation);
    }
    
    @Transactional
    public void checkOutReservation(Reservation reservation) {
    	// ToDo: validate before check-out
    	// check if guest checked out with days remaining
    	// if so current day is charged fully and the other days are treated as cancelations 
    	reservation.setStatus(ReservationStatusEnum.checkedOut);
    	updateReservation(reservation);
    }
    
    @Transactional
    public void guestCancelReservation(Reservation reservation) {	
    	// ToDo: Decipher Cancellation logic

    	// if current time is before (Start Date - 1) @ 3PM charge nothing
    	// if current time is between (Start Date - 1) @ 3PM and Start Date 3PM charge 30% of Start Date
    	// if current time is after Start Date @ 3PM charge 30% for Start Date and Start Date + 1 if there are multiple days
    	
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

    	// ToDo: implementation + background job caller
    }
    
    @Transactional 
    void checkCheckedInReservations() {
    	// this will be a triggered by a background job at 11am 
    	// checks 'checkedIn' reservations where the End Date is the current day
    	
    	// set the reservation statuses to checkedOut
    	
    	// ToDo: implementation + background job caller
    }
}
