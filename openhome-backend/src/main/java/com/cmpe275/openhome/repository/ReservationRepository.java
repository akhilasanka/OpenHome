package com.cmpe275.openhome.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cmpe275.openhome.model.Reservation;
import com.cmpe275.openhome.model.User;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
	List<Reservation> findByGuest(User guest);
	
	Reservation findReservationById(Long id);
}
