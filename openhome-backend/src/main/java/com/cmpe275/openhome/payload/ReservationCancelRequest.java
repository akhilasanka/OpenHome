package com.cmpe275.openhome.payload;

import javax.validation.constraints.NotNull;

public class ReservationCancelRequest {
	@NotNull
	private Long reservationId;

	public Long getReservationId() {
		return reservationId;
	}

	public void setReservationId(Long reservationId) {
		this.reservationId = reservationId;
	}
}
