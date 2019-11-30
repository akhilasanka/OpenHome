package com.cmpe275.openhome.payload;

import javax.validation.constraints.NotNull;

public class SystemDateTimeAddRequest {
    @NotNull
    private Long timeOffset;

	public Long getTimeOffset() {
		return timeOffset;
	}

	public void setTimeOffset(Long timeOffset) {
		this.timeOffset = timeOffset;
	}
}
