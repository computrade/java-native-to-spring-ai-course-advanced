package com.computrade.course.spring.ai.claude.service;

import com.computrade.course.spring.ai.claude.model.CompanyNews;
import com.computrade.course.spring.ai.claude.model.StockQuote;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockMarketToolService  {

    private final FinnHubStockMarketService finnHubStockMarketService;


    @Tool(
            name = "fetchStockPrice",
            description = "Retrieves the latest real-time stock quote metrics (current, high, low) for a given equity ticker symbol."
    )
    public StockQuote fetchStockPrice(
            @ToolParam(description = "The uppercase stock exchange ticker symbol, for example 'AAPL', 'GOOG', 'TSLA'.") String symbol
    ) {
        return finnHubStockMarketService.getSymbolQuote(symbol);
    }

    @Tool(
            name = "exportCompanyNews",
            description = "Downloads and exports the raw recent company news articles for a given symbol."
    )
    public List<CompanyNews> exportCompanyNews(
            @ToolParam(description = "The uppercase equity symbol e.g. 'TSLA', 'MSFT'") String symbol
    ) {
        // 1. Calculate the dynamic 30-day date range automatically using local Java time
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysAgo = today.minusDays(30);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String toDate = today.format(formatter);
        String fromDate = thirtyDaysAgo.format(formatter);

        return finnHubStockMarketService.getCompanyNews(symbol, fromDate, toDate);
    }


}



