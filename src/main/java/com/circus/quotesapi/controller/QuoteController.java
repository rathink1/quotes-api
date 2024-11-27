package com.circus.quotesapi.controller;

import com.circus.quotesapi.dto.QuoteRequest;
import com.circus.quotesapi.dto.QuoteResponse;
import com.circus.quotesapi.service.QuoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/quotes")
@RequiredArgsConstructor
public class QuoteController {
    private final QuoteService quoteService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QuoteResponse createQuote(@Valid @RequestBody QuoteRequest request) {
        return quoteService.createQuote(request);
    }

    @GetMapping
    public Page<QuoteResponse> getQuotes(@RequestParam(required = false) String author,
                                         Pageable pageable) {
        return author == null ?
                quoteService.getAllQuotes(pageable) :
                quoteService.getQuotesByAuthor(author, pageable);
    }

}
