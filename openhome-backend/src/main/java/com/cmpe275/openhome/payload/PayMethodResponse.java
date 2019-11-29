package com.cmpe275.openhome.payload;

import com.cmpe275.openhome.model.PaymentMethod;

public class PayMethodResponse {
    private boolean hasPayMethod;
    private String cardEnding;

    public boolean isHasPayMethod() {
        return hasPayMethod;
    }

    public void setHasPayMethod(boolean hasPayMethod) {
        this.hasPayMethod = hasPayMethod;
    }

    public String getCardEnding() {
        return cardEnding;
    }

    public void setCardEnding(String cardEnding) {
        this.cardEnding = cardEnding;
    }

    public PayMethodResponse(PaymentMethod paymentMethod) {
        this.hasPayMethod = paymentMethod!=null;
        if(hasPayMethod)
            this.cardEnding = paymentMethod.getCardEnding();
        else
            cardEnding = "";
    }
}
