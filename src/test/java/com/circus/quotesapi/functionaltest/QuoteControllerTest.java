package com.circus.quotesapi.functionaltest;

import com.circus.quotesapi.controller.QuoteController;
import com.circus.quotesapi.dto.QuoteRequest;
import com.circus.quotesapi.dto.QuoteResponse;
import com.circus.quotesapi.service.QuoteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuoteController.class)
class QuoteControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private QuoteService quoteService;

    @MockBean
    private PagedResourcesAssembler<QuoteResponse> assembler;

    private QuoteRequest validQuoteRequest;
    private QuoteResponse quoteResponse;
    private List<QuoteResponse> quoteResponses;

    @BeforeEach
    void setUp() {
        validQuoteRequest = new QuoteRequest();
        validQuoteRequest.setAuthor("Jeff Winger");
        validQuoteRequest.setContent("I am always willing to go the extra mile to avoid doing something.");

        quoteResponse = new QuoteResponse();
        quoteResponse.setId(1L);
        quoteResponse.setAuthor(validQuoteRequest.getAuthor());
        quoteResponse.setContent(validQuoteRequest.getContent());

        quoteResponses = List.of(quoteResponse);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createQuote_ValidRequest_Returns201() throws Exception {
        when(quoteService.createQuote(any(QuoteRequest.class)))
                .thenReturn(quoteResponse);

        mockMvc.perform(post("/api/quotes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validQuoteRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "READER")
    void createQuote_AsReader_Returns403() throws Exception {
        mockMvc.perform(post("/api/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validQuoteRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createQuote_InvalidRequest_Returns400() throws Exception {
        QuoteRequest invalidRequest = new QuoteRequest();
        invalidRequest.setAuthor("");
        invalidRequest.setContent("");

        mockMvc.perform(post("/api/quotes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "READER")
    void getQuotes_ReturnsPagedQuotes() throws Exception {
        Page<QuoteResponse> quotePage = new PageImpl<>(quoteResponses);
        when(quoteService.getAllQuotes(any(PageRequest.class)))
                .thenReturn(quotePage);

        mockMvc.perform(get("/api/quotes")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "READER")
    void getQuotesByAuthor_ValidAuthor_ReturnsFilteredQuotes() throws Exception {
        Page<QuoteResponse> quotePage = new PageImpl<>(quoteResponses);
        when(quoteService.getQuotesByAuthor(eq("Jeff Winger"), any(PageRequest.class)))
                .thenReturn(quotePage);

        mockMvc.perform(get("/api/quotes")
                        .param("author", "Jeff Winger")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void getQuotes_NoAuthentication_Returns401() throws Exception {
        mockMvc.perform(get("/api/quotes"))
                .andExpect(status().isUnauthorized());
    }
}
