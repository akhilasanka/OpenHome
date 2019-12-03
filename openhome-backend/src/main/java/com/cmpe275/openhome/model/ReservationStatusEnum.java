package com.cmpe275.openhome.model;

public enum ReservationStatusEnum {
	pendingCheckIn,
	checkedIn,
	checkedOut,
	automaticallyCanceled,
	guestCanceledBeforeCheckIn,
	guestCanceledAfterCheckIn,
	hostCanceledBeforeCheckIn,
	hostCanceledAfterCheckIn,
	pendingHostCancelation
}