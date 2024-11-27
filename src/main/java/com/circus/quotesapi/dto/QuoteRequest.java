package com.circus.quotesapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class QuoteRequest {
    @NotBlank(message = "Author is required")
    private String author;

    @NotBlank(message = "Quote content is required")
    private String content;
}