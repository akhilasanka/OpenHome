package com.cmpe275.openhome.payload;

public class EditPropertyResponse {

    private EditPropertyStatus status;

    private String message;


    public EditPropertyStatus getStatus() {
        return status;
    }

    public EditPropertyResponse(EditPropertyStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public void setStatus(EditPropertyStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
