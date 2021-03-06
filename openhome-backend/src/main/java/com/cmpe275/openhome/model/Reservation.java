package com.cmpe275.openhome.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "RESERVATION")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(optional=false, fetch=FetchType.EAGER)
    @JoinColumn(name="PROPERTY_ID")
    private Property property;

    @ManyToOne(optional=false, fetch=FetchType.EAGER)
    @JoinColumn(name="GUEST_ID")
    private User guest;
    
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="START_DATE")
    private Date startDate;
    
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="END_DATE")
    private Date endDate;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="CHECKOUT_DATE")
    private Date checkOutDate;
    
    @NotNull
    @Column(name="STATUS")
    @Enumerated(EnumType.STRING)
    private ReservationStatusEnum status;
    
    @NotNull
    @Column(name = "WEEKDAY_PRICE")
    private Double weekdayPrice;

    @NotNull
    @Column(name = "WEEKEND_PRICE")
    private Double weekendPrice;
    
    @NotNull
    @Column(name = "DAILY_PARKING_PRICE")
    private Double dailyParkingPrice;

	@Column(name = "PRICE")
    private Double totalPrice; // calculated based on the weekday and weekend price
	
	@Column(name = "HOST_CANCELATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date hostCancelationDate; // the date that the host cancelation was initiated from

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
	}

	public User getGuest() {
		return guest;
	}

	public void setGuest(User guest) {
		this.guest = guest;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getCheckOutDate() {
		return checkOutDate;
	}

	public void setCheckOutDate(Date checkOutDate) {
		this.checkOutDate = checkOutDate;
	}

	public ReservationStatusEnum getStatus() {
		return status;
	}

	public void setStatus(ReservationStatusEnum status) {
		this.status = status;
	}
	
	public void setWeekdayPrice(Double weekdayPrice) {
		this.weekdayPrice = weekdayPrice;
	}
	
	public double getWeekdayPrice() {
		return weekdayPrice;
	}
	
    public Double getWeekendPrice() {
		return weekendPrice;
	}

	public void setWeekendPrice(Double weekendPrice) {
		this.weekendPrice = weekendPrice;
	}
	
	public Double getDailyParkingPrice() {
		return dailyParkingPrice;
	}

	public void setDailyParkingPrice(Double dailyParkingPrice) {
		this.dailyParkingPrice = dailyParkingPrice;
	}
	
	public Double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public Date getHostCancelationDate() {
		return hostCancelationDate;
	}

	public void setHostCancelationDate(Date hostCancelationDate) {
		this.hostCancelationDate = hostCancelationDate;
	}
}
