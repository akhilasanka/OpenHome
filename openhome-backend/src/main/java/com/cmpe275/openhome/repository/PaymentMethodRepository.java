package com.cmpe275.openhome.repository;

import com.cmpe275.openhome.model.PaymentMethod;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
    @Query("SELECT pm FROM payment_method pm, reservation r WHERE r.guest_id=pm.user_id AND r.id=:rid")
    Optional<PaymentMethod> getPayByReservationId(@Param("rid") Long rid);
}
