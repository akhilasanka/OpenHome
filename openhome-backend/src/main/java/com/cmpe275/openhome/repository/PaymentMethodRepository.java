package com.cmpe275.openhome.repository;

import com.cmpe275.openhome.model.PaymentMethod;
import com.cmpe275.openhome.model.Reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
    @Query("SELECT pm FROM PaymentMethod pm, Reservation r WHERE r.guest.id=pm.userId AND r.id=:rid")
    PaymentMethod getPayByReservationId(@Param("rid") Long rid);
}
