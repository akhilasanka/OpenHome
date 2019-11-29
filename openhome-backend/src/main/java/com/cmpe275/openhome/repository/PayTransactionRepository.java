package com.cmpe275.openhome.repository;

import com.cmpe275.openhome.model.PayTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayTransactionRepository extends JpaRepository<PayTransaction, Long> {
}
