package com.coredesk.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({"code", "status", "message", "data"})
public class RestResponse {
    private String code;
    private String status;
    private String message;
    private Object data;

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
