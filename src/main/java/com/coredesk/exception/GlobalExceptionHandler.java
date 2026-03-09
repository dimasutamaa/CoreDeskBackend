package com.coredesk.exception;

import com.coredesk.dto.RestResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<RestResponse> handleAppException(AppException e) {
        return ResponseEntity.status(e.getStatus())
                .body(new RestResponse(e.getStatus().toString(), e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestResponse handleException(Exception e) {
        return new RestResponse(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage());
    }
}
