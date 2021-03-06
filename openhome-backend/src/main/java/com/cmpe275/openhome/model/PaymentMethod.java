package com.cmpe275.openhome.model;

import com.cmpe275.openhome.payload.AddPayRequest;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "payment_method")
public class PaymentMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name="USER_ID")
    private User user;

    @Column(nullable=false)
    private String cardNumber;

    @Column(nullable=false)
    private String cardEnding;

    @Column(nullable = false)
    private Integer expiryMonth;

    @Column(nullable = false)
    private Integer expiryYear;

    @Column(nullable = false)
    private String cvv;

    public void parseAddPayRequest(final AddPayRequest payRequest, User user){
        this.setUser(user);
        this.setCardNumber(payRequest.getCardNumber());
        this.setCardEnding(payRequest.getCardNumber().substring(12,16));
        this.setExpiryMonth(payRequest.getExpiryMonth());
        this.setExpiryYear(payRequest.getExpiryYear());
        this.setCvv(payRequest.getCvv());
    }

    public Long getUser() {
        return user.getId();
    }

    public void setUser(User user) {

        this.user = user;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardEnding() {
        return cardEnding;
    }

    public void setCardEnding(String cardEnding) {
        this.cardEnding = cardEnding;
    }

    public Integer getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(Integer expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public Integer getExpiryYear() {
        return expiryYear;
    }

    public void setExpiryYear(Integer expiryYear) {
        this.expiryYear = expiryYear;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
}
