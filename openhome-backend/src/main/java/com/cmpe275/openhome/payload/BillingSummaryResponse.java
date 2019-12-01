package com.cmpe275.openhome.payload;

import com.cmpe275.openhome.model.ChargeType;
import com.cmpe275.openhome.model.PayTransaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BillingSummaryResponse {
    private boolean success = false;
    private Map<Long, String> validProperties = new HashMap<>();
    private Set<String> validMonths = new HashSet<>();
    private List<LineItem> lineItems = new ArrayList<>();

    public void addLineItem(LineItem lineItem) {
        lineItems.add(lineItem);
        validMonths.add(lineItem.transactionMonth);
        validProperties.put(lineItem.propertyId, lineItem.propertyName);
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public Map<Long, String> getValidProperties() {
        return validProperties;
    }

    public Set<String> getValidMonths() {
        return validMonths;
    }

    public List<LineItem> getLineItems() {
        return lineItems;
    }

    public static class LineItem {
        private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM yyyy");
        private static final SimpleDateFormat T_DATE_FORMAT = new SimpleDateFormat("dd MMM yy");
        private static final SimpleDateFormat MONTH_NAME = new SimpleDateFormat("MMMM");
        private String transactionMonth;
        private Long transactionId;
        private Long reservationId;
        private Long propertyId;
        private String propertyName;
        private String startDate;
        private String endDate;
        private Double amount;
        private String chargedDate;
        private String type;
        private String card;

        public static LineItem parseLineItemFromPayTransaction(PayTransaction payTransaction,
                                                               boolean isGuest) {
            final LineItem li = new LineItem();
            li.transactionId = payTransaction.getTransactionId();
            li.reservationId = payTransaction.getReservation().getId();
            li.propertyId = payTransaction.getReservation().getProperty().getId();
            li.propertyName = payTransaction.getReservation().getProperty().getPropertyName();
            li.startDate = DATE_FORMAT.format(payTransaction.getReservation().getStartDate());
            li.endDate = DATE_FORMAT.format(payTransaction.getReservation().getEndDate());
            li.chargedDate = T_DATE_FORMAT.format(payTransaction.getTransactionDate());
            li.transactionMonth = MONTH_NAME.format(payTransaction.getTransactionDate());
            li.card = payTransaction.getCardUsed();
            if(isGuest) {
                if(payTransaction.getChargeType() == ChargeType.GUESTCHECKIN) {
                    li.amount = -1.0* payTransaction.getAmount();
                    li.type = "Check-in";
                } else if(payTransaction.getChargeType() == ChargeType.GUESTPENALTY) {
                    li.amount = -1.0* payTransaction.getAmount();
                    li.type = "Change/Cancel Penalty";
                } else {
                    li.amount = payTransaction.getAmount();
                    li.type = "Host Change/Cancel credit";
                }
            } else {
                if(payTransaction.getChargeType() == ChargeType.GUESTCHECKIN) {
                    li.amount = payTransaction.getAmount();
                    li.type = "Guest Check-in";
                } else if(payTransaction.getChargeType() == ChargeType.GUESTPENALTY) {
                    li.amount = payTransaction.getAmount();
                    li.type = "Guest Change/Cancel Credit";
                } else {
                    li.amount = -1.0 * payTransaction.getAmount();
                    li.type = "Change/Cancel Penalty";
                }
            }
            return li;
        }
        public String getTransactionMonth() {
            return transactionMonth;
        }

        public Long getTransactionId() {
            return transactionId;
        }

        public Long getReservationId() {
            return reservationId;
        }

        public Long getPropertyId() {
            return propertyId;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public String getStartDate() {
            return startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public Double getAmount() {
            return amount;
        }

        public String getChargedDate() {
            return chargedDate;
        }

        public String getType() {
            return type;
        }

        public String getCard() {
            return card;
        }
    }
}
