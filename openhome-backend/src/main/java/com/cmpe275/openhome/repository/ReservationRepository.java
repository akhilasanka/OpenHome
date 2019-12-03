package com.cmpe275.openhome.repository;

import java.util.Date;
import java.util.List;
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

	@Query(value = "SELECT r from Reservation r where r.status<>'cancelled' AND (startDate BETWEEN :startDate AND :endDate OR endDate BETWEEN :startDate AND :endDate)")
	List<Reservation> findAllReservationsBetweenDates(Date startDate, Date endDate);

	@Query(value = "SELECT r from Reservation r where r.status='pendingCheckIn' AND startDate < :currentDate")
	List<Reservation> findAllPendingReservationsThatShouldBeCancelled(Date currentDate);

	@Query(value = "SELECT r from Reservation r where r.status='checkedIn' AND endDate < :currentDate")
	List<Reservation> findAllCheckedInReservationsThatShouldBeCheckedOut(Date currentDate);
	
	@Query(value = "SELECT r from Reservation r where r.status='pendingHostCancelation'")
	List<Reservation> findAllReservationsThatShouldBeCanceled();

	List<Reservation> findAllByProperty(Property property);
	
	/* FOR RESERVATION INTEGRITY CHECK*/
	@Query(value = "SELECT r from Reservation r where r.property=:property AND r.status='pendingCheckIn' AND (r.startDate <= :endDate AND :startDate <= r.endDate)")
	List<Reservation> findConflictingReservationsThatArePendingCheckIn(Property property, Date startDate, Date endDate);
	
	@Query(value = "SELECT r from Reservation r where r.property=:property AND r.status='checkedIn' AND (r.startDate <= :endDate AND :startDate <= r.endDate)")
	List<Reservation> findConflictingReservationsThatAreCheckedIn(Property property, Date startDate, Date endDate);
	
	@Query(value = "SELECT r from Reservation r where r.property=:property AND r.status='canceledAutomatically' AND (r.startDate <= :endDate AND :startDate <= r.checkOutDate)")
	List<Reservation> findConflictingReservationsThatWereCanceledAutomatically(Property property, Date startDate, Date endDate);
	
	@Query(value = "SELECT r from Reservation r where r.property=:property AND r.status='guestCanceledAfterCheckIn' AND (r.startDate <= :endDate AND :startDate <= r.checkOutDate)")
	List<Reservation> findConflictingReservationsThatWereCanceledByGuestAfterCheckIn(Property property, Date startDate, Date endDate);
	
	@Query(value = "SELECT r from Reservation r where r.property=:property AND r.status='hostCanceledAfterCheckIn' AND (r.startDate <= :endDate AND :startDate <= r.checkOutDate)")
	List<Reservation> findConflictingReservationsThatWereCanceledByHostAfterCheckIn(Property property, Date startDate, Date endDate);
	
	@Query(value = "SELECT r from Reservation r where r.property=:property AND r.status='pendingHostCancelation' AND (r.startDate <= :endDate AND :startDate <= r.checkOutDate)")
	List<Reservation> findConflictingReservationsThatArePendingCancelationByHost(Property property, Date startDate, Date endDate);
	/* END FOR RESERVATION INTEGRITY CHECK*/

}
