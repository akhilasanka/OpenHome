package com.cmpe275.openhome.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "payment_transaction")
public class PayTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer transactionId;

    @Column(nullable=false)
    private Integer reservationId;

    @Column(nullable=false)
    private ChargeType chargeType;

    @Column(nullable=false)
    private Double amount;

    @Column(nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date transactionDate;

    @Column(nullable=false)
    private String cardUsed;

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getReservationId() {
        return reservationId;
    }

    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }

    public ChargeType getChargeType() {
        return chargeType;
    }

    public void setChargeType(ChargeType chargeType) {
        this.chargeType = chargeType;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getCardUsed() {
        return cardUsed;
    }

    public void setCardUsed(String cardUsed) {
        this.cardUsed = cardUsed;
    }
}
