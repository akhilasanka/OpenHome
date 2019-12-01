package com.cmpe275.openhome.payload;

public class PostPropertyResponse {
    private boolean success;
    private Long propertyId;

    private String message;

    public PostPropertyResponse(boolean success, Long propertyId, String message) {
        this.success = success;
        this.propertyId = propertyId;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
