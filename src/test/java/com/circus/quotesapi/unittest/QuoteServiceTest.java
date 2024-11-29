package com.circus.quotesapi.unittest;

import com.circus.quotesapi.dto.QuoteRequest;
import com.circus.quotesapi.dto.QuoteResponse;
import com.circus.quotesapi.exception.QuoteNotFoundException;
import com.circus.quotesapi.model.Quote;
import com.circus.quotesapi.repository.QuoteRepository;
import com.circus.quotesapi.service.QuoteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuoteServiceTest {
    @Mock
    private QuoteRepository quoteRepository;

    @Mock
    private Authentication auth;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private QuoteServiceImpl quoteService;

    private QuoteRequest quoteRequest;
    private Quote quote;

    @BeforeEach
    void setUp() {
        quoteRequest = new QuoteRequest();
        quoteRequest.setAuthor("Jeff Winger");
        quoteRequest.setContent("It's called chemistry. I have it with everyone.");

        quote = new Quote();
        quote.setId(1L);
        quote.setAuthor(quoteRequest.getAuthor());
        quote.setContent(quoteRequest.getContent());
        quote.setCreatedBy("admin");

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void createQuote_ValidRequest_ReturnsQuoteResponse() {
        when(quoteRepository.save(any(Quote.class))).thenReturn(quote);
        when(auth.getName()).thenReturn("admin");
        when(securityContext.getAuthentication()).thenReturn(auth);

        QuoteResponse response = quoteService.createQuote(quoteRequest);

        assertNotNull(response);
        assertEquals(quote.getId(), response.getId());
        assertEquals(quote.getAuthor(), response.getAuthor());
        assertEquals(quote.getContent(), response.getContent());
    }

    @Test
    void getAllQuotes_ValidRequest_ReturnsQuotePage() {
        Page<Quote> quotePage = new PageImpl<>(List.of(quote));
        when(quoteRepository.findAll(any(Pageable.class)))
                .thenReturn(quotePage);

        Page<QuoteResponse> response = quoteService.getAllQuotes(PageRequest.of(0, 10));

        assertFalse(response.isEmpty());
        assertEquals(quote.getId(), response.getContent().get(0).getId());
        assertEquals(quote.getAuthor(), response.getContent().get(0).getAuthor());
        assertEquals(quote.getContent(), response.getContent().get(0).getContent());
    }

    @Test
    void getAllQuotes_EmptyPage_ThrowsQuoteNotFoundException() {
        when(quoteRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        assertThrows(QuoteNotFoundException.class, () ->
                quoteService.getAllQuotes(PageRequest.of(0, 10)));
    }

    @Test
    void getQuotesByAuthor_ValidAuthor_ReturnsQuotePage() {
        Page<Quote> quotePage = new PageImpl<>(List.of(quote));
        when(quoteRepository.findByAuthorIgnoreCase(eq("Jeff Winger"), any(Pageable.class)))
                .thenReturn(quotePage);

        Page<QuoteResponse> response = quoteService.getQuotesByAuthor("Jeff Winger", PageRequest.of(0, 10));

        assertFalse(response.isEmpty());
        assertEquals(quote.getId(), response.getContent().get(0).getId());
        assertEquals(quote.getAuthor(), response.getContent().get(0).getAuthor());
        assertEquals(quote.getContent(), response.getContent().get(0).getContent());
    }

    @Test
    void getQuotesByAuthor_NonexistentAuthor_ThrowsQuoteNotFoundException() {
        when(quoteRepository.findByAuthorIgnoreCase(anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        assertThrows(QuoteNotFoundException.class, () ->
                quoteService.getQuotesByAuthor("NonexistentAuthor", PageRequest.of(0, 10)));
    }
}