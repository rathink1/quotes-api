package com.circus.quotesapi.repository;

import com.circus.quotesapi.model.Quote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface QuoteRepository extends JpaRepository<Quote, Long> {
    Page<Quote> findByAuthorIgnoreCase(String author, Pageable pageable);
}
