package com.cmpe275.openhome.payload;

import com.cmpe275.openhome.model.PayTransaction;
import com.cmpe275.openhome.util.DateUtils;
import com.cmpe275.openhome.util.SystemDateTime;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BillingSummaryResponse {
    private boolean success = false;
    private Map<Long, String> validProperties = new HashMap<>();
    private List<String> allMonths = null;
    private List<LineItem> lineItems = new ArrayList<>();

    public BillingSummaryResponse() {
        validProperties.put(0L, "All properties");
    }

    public void addLineItem(LineItem lineItem) {
        if(lineItem == null)
            return;
        lineItems.add(lineItem);
        validProperties.put(lineItem.propertyId, lineItem.propertyName);
    }

    public List<String> getAllMonths() {
        if(allMonths == null) {
            allMonths = new ArrayList<>();
            final LocalDate curLocalDate = SystemDateTime.getCurSystemTime().toLocalDate();
            for (int i = 11; i >=0; i--) {
                final LocalDate loopLocalDate = curLocalDate.minusMonths(i);
                allMonths.add(DateUtils.formatMonthForDisplay(loopLocalDate));
            }
        }
        return allMonths;
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

    public List<LineItem> getLineItems() {
        return lineItems;
    }

    public static class LineItem {
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
            final LocalDate curLocalDate = SystemDateTime.getCurSystemTime().toLocalDate();
            final LocalDate transactionLocalDate = DateUtils.convertDateToLocalDate(payTransaction.getTransactionDate());
            final long monthsBetween = ChronoUnit.MONTHS.between(transactionLocalDate, curLocalDate);
            if(monthsBetween <0 || monthsBetween >= 12) {
                return null;
            }

            final LineItem li = new LineItem();
            li.transactionId = payTransaction.getTransactionId();
            li.reservationId = payTransaction.getReservation().getId();
            li.propertyId = payTransaction.getReservation().getProperty().getId();
            li.propertyName = payTransaction.getReservation().getProperty().getPropertyName();
            li.startDate = DateUtils.formatForDisplay(payTransaction.getReservation().getStartDate());
            li.endDate = DateUtils.formatForDisplay(payTransaction.getReservation().getEndDate());
            li.chargedDate = DateUtils.formatForDisplay(payTransaction.getTransactionDate());
            li.transactionMonth = DateUtils.formatMonthForDisplay(payTransaction.getTransactionDate());
            li.card = payTransaction.getCardUsed();
            switch (payTransaction.getChargeType()) {
                case GUESTPENALTY:
                    li.amount = isGuest ? -1.0* payTransaction.getAmount() : payTransaction.getAmount();
                    li.type = isGuest ? "Change/Cancel Penalty" : "Guest Change/Cancel Credit";
                    break;
                case GUESTCHECKIN:
                    li.amount = isGuest ? -1.0* payTransaction.getAmount() : payTransaction.getAmount();
                    li.type = isGuest ? "Check-in charge" : "Guest Check-in credit";
                    break;
                case GUESTREFUND:
                    li.amount = isGuest ? payTransaction.getAmount() : -1.0 * payTransaction.getAmount();
                    li.type = isGuest ? "Refund credit" : "Guest Refund";
                    break;
                case HOSTPENALTY:
                    li.amount = isGuest ? payTransaction.getAmount() : -1.0 * payTransaction.getAmount();
                    li.type = isGuest ? "Host Change/Cancel credit" : "Change/Cancel Penalty";
                    break;
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
