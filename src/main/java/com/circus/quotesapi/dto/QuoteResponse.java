package com.circus.quotesapi.dto;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;


@Data
public class QuoteResponse extends RepresentationModel<QuoteResponse> {
    private Long id;
    private String author;
    private String content;
}
