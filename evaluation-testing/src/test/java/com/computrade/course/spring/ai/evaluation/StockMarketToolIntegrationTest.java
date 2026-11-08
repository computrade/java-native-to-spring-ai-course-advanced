package com.computrade.course.spring.ai.evaluation;

import com.computrade.course.spring.ai.evaluation.model.StockQuote;
import com.computrade.course.spring.ai.evaluation.service.FinnHubStockMarketService;
import com.computrade.course.spring.ai.evaluation.service.StockMarketToolService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StockMarketToolIntegrationTest {

    @Autowired
    private ChatClient.Builder chatClientBuilder;

    @Autowired
    private StockMarketToolService stockMarketToolService;

    @Autowired
    private FinnHubStockMarketService finnHubStockMarketService;

    @Test
    @DisplayName("Should Compare LLM Fetch stock @Tool vs Direct API call")
    void testCompareLLMResponseWithActualToolCall() {
        String symbol = "AAPL";

        // 1. Fetch ground-truth data directly from the underlying service
        StockQuote actualQuote = finnHubStockMarketService.getSymbolQuote(symbol);

        assertNotNull(actualQuote, "Ground truth quote should not be null");
        assertTrue(actualQuote.currentPrice() > 0, "Current price must be positive");

        // 2. Execute the ChatClient query with the tool registered
        ChatClient chatClient = chatClientBuilder.build();

        String llmResponse = chatClient.prompt()
                .user("What is the current stock price of " + symbol + "?")
                .tools(stockMarketToolService) // Registers fetchStockPrice @Tool
                .call()
                .content();

        // 3. Compare LLM output against actualQuote values
        assertNotNull(llmResponse);
        assertTrue(
                llmResponse.contains(symbol),
                "LLM response should contain ticker symbol " + symbol
        );

        // Convert the current price to String and assert its presence in the response
        String expectedPriceString = String.valueOf(actualQuote.currentPrice());
        assertTrue(
                llmResponse.contains(expectedPriceString),
                String.format("LLM response should contain the exact price [%s] from tool call. Full response: %s",
                        expectedPriceString, llmResponse)
        );
    }
}