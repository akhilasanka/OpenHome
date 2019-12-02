package com.cmpe275.openhome.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cmpe275.openhome.model.Property;
import com.cmpe275.openhome.model.Reservation;
import com.cmpe275.openhome.model.User;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
	@Query(value = "SELECT r FROM Reservation r WHERE r.guest.id=:userId AND r.guest.emailVerified=true")
	List<Reservation> findByVerifiedGuestId(Long userId);

	@Query(value = "SELECT r FROM Reservation r WHERE r.property.owner.id=:userId AND r.property.owner.emailVerified=true")
	List<Reservation> findByVerifiedHostId(Long userId);

	List<Reservation> findByGuest(User guest);
	
	Reservation findReservationById(Long id);
	
	@Query(value = "SELECT r from Reservation r where property.id =:propertyId AND (startDate BETWEEN :startDate AND :endDate OR endDate BETWEEN :startDate AND :endDate)")
	List<Reservation> findAllReservationsForPropertyBetweenDates(long propertyId, Date startDate, Date endDate);

	@Query(value = "SELECT r from Reservation r where r.status='pendingCheckIn' AND startDate < :currentDate")
	List<Reservation> findAllPendingReservationsThatShouldBeCancelled(Date currentDate);

	@Query(value = "SELECT r from Reservation r where r.status='checkedIn' AND endDate < :currentDate")
	List<Reservation> findAllCheckedInReservationsThatShouldBeCheckedOut(Date currentDate);
	
	@Query(value = "SELECT r from Reservation r where r.status='pendingHostCancelation'")
	List<Reservation> findAllReservationsThatShouldBeCanceled();

	List<Reservation> findAllByProperty(Property property);
	
	@Query( value = "SELECT r from Reservation r where r.guest = :guest",
		    countQuery = "SELECT count(*) from Reservation r where r.guest = :guest"
		    )
	Page<Reservation> findByGuest(User guest, Pageable pageable);
}
