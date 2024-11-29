package com.circus.quotesapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class QuoteRequest {

    @NotBlank(message = "Author name is required")
    @Size(max = 50, message = "Author name must not exceed 50 characters")
    private String author;

    @NotBlank(message = "Quote content is required")
    @Size(max = 1000, message = "Quote content must not exceed 1000 characters")
    private String content;
}