package com.circus.quotesapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
public class Quote {

    @Id
    @GeneratedValue
    private Long id;

    private String author;
    private String content;
    private String createdBy;
}

