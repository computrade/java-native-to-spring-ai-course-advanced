package com.computrade.course.spring.ai.claude.service;

import com.computrade.course.spring.ai.claude.model.CompanyNews;
import com.computrade.course.spring.ai.claude.model.StockQuote;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FinnHubStockMarketService {

    private final RestClient finnHubRestClient;

    @Value("${app.finnhub.api-key}")
    String apiKey;


    public StockQuote getSymbolQuote(String symbol) {
        StockQuote response = Objects.requireNonNull(finnHubRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/quote")
                        .queryParam("symbol", symbol.toUpperCase())
                        .queryParam("token", apiKey)
                        .build())
                .retrieve()
                .body(StockQuote.class));

        if (response.currentPrice() == 0) {
            throw new RuntimeException("Finnhub failed to return valid data for ticker: " + symbol);
        }
        return response.withTicker(symbol.toUpperCase());
    }


    public @NonNull List<CompanyNews> getCompanyNews(String ticker, String fromDate, String toDate) {
        // 2. Fetch the modern collection payload directly using ParameterizedTypeReference
        List<CompanyNews> newsList = finnHubRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/company-news")
                        .queryParam("symbol", ticker.toUpperCase())
                        .queryParam("from", fromDate)
                        .queryParam("to", toDate)
                        .queryParam("token", apiKey)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<CompanyNews>>() {
                });

        if (newsList == null || newsList.isEmpty()) {
            throw new RuntimeException("No recent news found for ticker: " + ticker);
        }

        // 3. Return the limited stream of structural POJOs directly to the response pipeline
        return newsList.stream().limit(5).toList();
    }

}



