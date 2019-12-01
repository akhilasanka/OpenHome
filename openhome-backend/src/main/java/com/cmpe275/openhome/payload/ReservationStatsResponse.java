package com.cmpe275.openhome.payload;

import com.cmpe275.openhome.model.Property;
import com.cmpe275.openhome.model.Reservation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReservationStatsResponse {
    private boolean success = false;
    private List<ReservationItem> past = new ArrayList<>();
    private List<ReservationItem> current = new ArrayList<>();
    private List<ReservationItem> future = new ArrayList<>();

    public List<ReservationItem> getPast() {
        return past;
    }

    public void setPast(ReservationItem pastItem) {
        this.past.add(pastItem);
    }

    public List<ReservationItem> getCurrent() {
        return current;
    }

    public void setCurrent(ReservationItem currentItem) {
        this.current.add(currentItem);
    }

    public List<ReservationItem> getFuture() {
        return future;
    }

    public void setFuture(ReservationItem futureItem) {
        this.future.add(futureItem);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public static class ReservationItem {
        private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM yyyy");
        private Long reservationId;
        private Long PropertyId;
        private String propertyName;
        private String startDate;
        private String endDate;
        private Double price;

        public static ReservationItem newItemFromReservation(Reservation reservation) {
            Property property = reservation.getProperty();
            ReservationItem ri = new ReservationItem();
            ri.setReservationId(reservation.getId());
            ri.setPropertyId(property.getId());
            ri.setPropertyName(property.getPropertyName());
            ri.setStartDate(DATE_FORMAT.format(reservation.getStartDate()));
            ri.setEndDate(DATE_FORMAT.format(reservation.getEndDate()));
            ri.setPrice(reservation.getDailyPrice());
            return ri;
        }

        public Long getReservationId() {
            return reservationId;
        }

        public void setReservationId(Long reservationId) {
            this.reservationId = reservationId;
        }

        public Long getPropertyId() {
            return PropertyId;
        }

        public void setPropertyId(Long propertyId) {
            PropertyId = propertyId;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public void setPropertyName(String propertyName) {
            this.propertyName = propertyName;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }
    }
}
