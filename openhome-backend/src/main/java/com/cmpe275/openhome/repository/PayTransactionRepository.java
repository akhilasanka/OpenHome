package com.cmpe275.openhome.repository;

import com.cmpe275.openhome.model.PayTransaction;
import com.cmpe275.openhome.model.Reservation;
import com.cmpe275.openhome.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PayTransactionRepository extends JpaRepository<PayTransaction, Long> {
    @Query(value = "SELECT pt FROM PayTransaction pt WHERE pt.reservation.guest=:user")
    List<PayTransaction> findTransactionsForGuest(User user);

    @Query(value = "SELECT pt FROM PayTransaction pt WHERE pt.reservation.property.owner=:user")
    List<PayTransaction> findTransactionsForHost(User user);
}
