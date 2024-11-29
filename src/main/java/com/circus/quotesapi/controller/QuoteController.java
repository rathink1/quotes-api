package com.circus.quotesapi.controller;

import com.circus.quotesapi.dto.QuoteRequest;
import com.circus.quotesapi.dto.QuoteResponse;
import com.circus.quotesapi.service.QuoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequestMapping("/api/quotes")
@RequiredArgsConstructor
public class QuoteController {
    private final QuoteService quoteService;

    private final PagedResourcesAssembler<QuoteResponse> assembler;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<QuoteResponse> createQuote(@Valid @RequestBody QuoteRequest request) {
        QuoteResponse quote = quoteService.createQuote(request);
        return EntityModel.of(quote)
                .add(linkTo(methodOn(QuoteController.class).getQuotes(quote.getAuthor(), null)).withSelfRel());
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PagedModel<EntityModel<QuoteResponse>> getQuotes(
            @RequestParam(required = false) String author,
            Pageable pageable
    ) {
        Page<QuoteResponse> quotes = author == null ?
                quoteService.getAllQuotes(pageable) :
                quoteService.getQuotesByAuthor(author, pageable);

        return assembler.toModel(quotes);
    }

}
