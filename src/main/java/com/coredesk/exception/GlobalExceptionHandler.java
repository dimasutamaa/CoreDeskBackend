package com.coredesk.exception;

import com.coredesk.dto.RestResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<RestResponse> handleAppException(AppException e, HttpServletRequest request) {
        log.warn("AppException at {} - {}", request.getRequestURI(), e.getMessage());
        return ResponseEntity.status(e.getStatus())
                .body(new RestResponse(e.getStatus().toString(), e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestResponse> handleException(Exception e, HttpServletRequest request) {
        log.error("Unexpected error occurred at {}", request.getRequestURI(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RestResponse(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Internal server error"));
    }
}
