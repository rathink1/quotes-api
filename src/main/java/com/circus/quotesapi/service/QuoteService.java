package com.circus.quotesapi.service;

import com.circus.quotesapi.dto.QuoteRequest;
import com.circus.quotesapi.dto.QuoteResponse;
import com.circus.quotesapi.model.Quote;
import com.circus.quotesapi.repository.QuoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class QuoteService {
    private final QuoteRepository quoteRepository;

    public QuoteResponse createQuote(QuoteRequest request) {
        Quote quote = new Quote();
        quote.setAuthor(request.getAuthor());
        quote.setContent(request.getContent());
        quote.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());

        Quote savedQuote = quoteRepository.save(quote);
        return convertToResponse(savedQuote);
    }

    private QuoteResponse convertToResponse(Quote quote) {
        QuoteResponse response = new QuoteResponse();
//        response.setId(quote.getId());
        response.setAuthor(quote.getAuthor());
        response.setContent(quote.getContent());
//        response.setCreatedBy(quote.getCreatedBy());
        return response;
    }

    public Page<QuoteResponse> getAllQuotes(Pageable pageable) {
        return quoteRepository.findAll(pageable).map(this::convertToResponse);
    }

    public Page<QuoteResponse> getQuotesByAuthor(String author, Pageable pageable) {
        return quoteRepository.findByAuthorIgnoreCase(author, pageable)
                .map(this::convertToResponse);
    }


}

