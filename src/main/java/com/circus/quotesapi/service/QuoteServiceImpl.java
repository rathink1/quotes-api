package com.circus.quotesapi.service;

import com.circus.quotesapi.dto.QuoteRequest;
import com.circus.quotesapi.dto.QuoteResponse;
import com.circus.quotesapi.exception.QuoteNotFoundException;
import com.circus.quotesapi.model.Quote;
import com.circus.quotesapi.repository.QuoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class QuoteServiceImpl implements QuoteService {
    private final QuoteRepository quoteRepository;

    @Override
    public QuoteResponse createQuote(QuoteRequest request) {
        Quote quote = new Quote();
        quote.setAuthor(request.getAuthor().trim());
        quote.setContent(request.getContent());
        quote.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());

        Quote savedQuote = quoteRepository.save(quote);
        return convertToResponse(savedQuote);
    }

    private QuoteResponse convertToResponse(Quote quote) {
        QuoteResponse response = new QuoteResponse();
        response.setId(quote.getId());
        response.setAuthor(quote.getAuthor());
        response.setContent(quote.getContent());
        return response;
    }

    @Override
    public Page<QuoteResponse> getAllQuotes(Pageable pageable) {
        Page<Quote> quotes = quoteRepository.findAll(pageable);
        if (quotes.isEmpty()) {
            throw new QuoteNotFoundException("No quotes found");
        }
        return quotes.map(this::convertToResponse);
    }

    @Override
    public Page<QuoteResponse> getQuotesByAuthor(String author, Pageable pageable) {
        Page<Quote> quotes = quoteRepository.findByAuthorIgnoreCase(author.trim(), pageable);
        if (quotes.isEmpty()) {
            throw new QuoteNotFoundException("No quotes found for author: " + author);
        }
        return quotes.map(this::convertToResponse);
    }
}

