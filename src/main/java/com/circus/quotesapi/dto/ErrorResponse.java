package com.circus.quotesapi.dto;

import lombok.Data;


@Data
public class ErrorResponse {
    private final int status;
    private final String error;
    private final String message;
    private final String timestamp;
}
