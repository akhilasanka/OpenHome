package com.cmpe275.openhome.payload;

import com.cmpe275.openhome.util.SystemDateTime;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;

public class AddPayRequest {
    private Long userid;

    @NotBlank
    private String cardNumber;

    private  Integer expiryMonth;

    private Integer expiryYear;

    @NotBlank
    private String cvv;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
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

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public boolean validate() {
        final LocalDateTime curTime = SystemDateTime.getCurSystemTime();
        final int curYear = curTime.getYear();
        final int curMonth = curTime.getMonth().getValue();
        return this.cardNumber.length() == 16   // card number is 16 digits long
                && this.cvv.length()==3         // CVV is 3 digits long
                && this.expiryMonth>0 && this.expiryMonth<13    // expiry month is valid
                && this.expiryYear>=curYear     // expiryYear is in future
                && (this.expiryYear > curYear || this.expiryMonth > curMonth)   // expiryMonth is in future
                && this.userid>0;   // not a zero userid
    }
}
