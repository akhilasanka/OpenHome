package com.cmpe275.openhome.repository;

import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cmpe275.openhome.model.Reservation;
import com.cmpe275.openhome.model.User;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
	List<Reservation> findByGuest(User guest);
	
	Reservation findReservationById(Long id);
	
	@Query(value = "SELECT r from Reservation r where property.id =:propertyId AND (startDate BETWEEN :startDate AND :endDate OR endDate BETWEEN :startDate AND :endDate)")
	List<Reservation> findAllReservationsForPropertyBetweenDates(long propertyId, Date startDate, Date endDate);
}
