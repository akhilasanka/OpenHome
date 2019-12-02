package com.cmpe275.openhome.payload;

import java.util.Date;

public class ReservationListResponseEntity {
	private Long id;
	private Date startDate;
	
	
	public ReservationListResponseEntity(Long id, Date startDate) {
		this.id = id;
		this.startDate = startDate;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Date getStartDate() {
		return startDate;
	}


	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	
}