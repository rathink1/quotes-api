package com.circus.quotesapi.service;

import com.circus.quotesapi.dto.QuoteRequest;
import com.circus.quotesapi.dto.QuoteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuoteService {
    QuoteResponse createQuote(QuoteRequest request);

    Page<QuoteResponse> getAllQuotes(Pageable pageable);

    Page<QuoteResponse> getQuotesByAuthor(String author, Pageable pageable);
}
