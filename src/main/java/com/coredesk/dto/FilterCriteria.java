package com.coredesk.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class FilterCriteria {
    private String title;
    private String status;
    private String priority;
    private String assignedTo;
    private String createdBy;
    private LocalDate from;
    private LocalDate to;
}
