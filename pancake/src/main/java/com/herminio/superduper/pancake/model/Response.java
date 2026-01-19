package com.herminio.superduper.pancake.model;

import java.util.ArrayList;
import java.util.List;

public class Response {

    private String errorCode;
    private String status;
    private String message;
    private List<String> details = new ArrayList<String>();

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    public void addDetail(String detail) {
        this.details.add(detail);
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getDetails() {
        return details;
    }

}
