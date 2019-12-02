package com.cmpe275.openhome.payload;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.cmpe275.openhome.model.Reservation;

public class ReservationListResponse {
    @NotNull
    private List<ReservationListResponseEntity> reservations;
    
    @NotNull
    private Integer pageCount;

	public List<ReservationListResponseEntity> getReservations() {
		return reservations;
	}

	public void setReservations(List<ReservationListResponseEntity> reservations) {
		this.reservations = reservations;
	}

	public Integer getPageCount() {
		return pageCount;
	}

	public void setPageCount(Integer pageCount) {
		this.pageCount = pageCount;
	}
	
}