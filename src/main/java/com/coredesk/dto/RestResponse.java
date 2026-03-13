package com.coredesk.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestResponse {
    private String code;
    private String status;
    private String message;
    private Object data;

    public RestResponse() {
        this.code = "00";
        this.status = "Success";
        this.message = "Success";
        this.data = null;
    }

    public RestResponse(Object data) {
        this.code = "00";
        this.status = "Success";
        this.message = "Success";
        this.data = data;
    }

    public RestResponse(String code, String message) {
        this.code = code;
        this.status = "Error";
        this.message = message;
        this.data = null;
    }
}
