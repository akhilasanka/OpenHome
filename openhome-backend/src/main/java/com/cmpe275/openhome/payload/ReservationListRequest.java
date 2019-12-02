package com.cmpe275.openhome.payload;

import javax.validation.constraints.NotNull;

public class ReservationListRequest {
    @NotNull
    private Integer currentPage;
    
    @NotNull
    private Integer elementsPerPage;

	public Integer getCurrentPage() {
		return currentPage;
	}
	
	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public Integer getElementsPerPage() {
		return elementsPerPage;
	}

	public void setElementsPerPage(Integer elementsPerPage) {
		this.elementsPerPage = elementsPerPage;
	}
    
}
