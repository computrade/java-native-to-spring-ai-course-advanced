package com.computrade.course.spring.ai.evaluation;

import com.computrade.course.spring.ai.evaluation.model.StockQuote;
import com.computrade.course.spring.ai.evaluation.service.FinnHubStockMarketService;
import com.computrade.course.spring.ai.evaluation.service.StockMarketToolService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// deterministic unit test without calling real external APIs or LLMs:
@ExtendWith(MockitoExtension.class)
class StockMarketToolServiceUnitTest {

    @Mock
    private ChatModel chatModel;

    @Mock
    private FinnHubStockMarketService finnHubStockMarketService;

    @Test
    void testFetchStockPriceDirectly() {
        String symbol = "AAPL";
        StockQuote expectedQuote = new StockQuote(symbol, 230.50, 235.00, 228.00, 229.00, 1.50, 0.65, 229.00, System.currentTimeMillis());

        // 1. Stub Finnhub service ground-truth response
        when(finnHubStockMarketService.getSymbolQuote(symbol)).thenReturn(expectedQuote);

        // 2. Prevent NPE on options
        when(chatModel.getOptions()).thenReturn(ChatOptions.builder().build());

        // 3. Mock ChatModel completion matching the tool response
        String mockedLLMText = "The current stock price for AAPL is $230.50.";
        ChatResponse chatResponse = new ChatResponse(List.of(new Generation(new AssistantMessage(mockedLLMText))));
        when(chatModel.call(any(Prompt.class))).thenReturn(chatResponse);

        // 4. Test tool invocation directly
        StockMarketToolService toolService = new StockMarketToolService(finnHubStockMarketService);
        StockQuote toolResult = toolService.fetchStockPrice(symbol);

        // Assert tool method returns exact record values
        assertEquals(expectedQuote.currentPrice(), toolResult.currentPrice());

        // 5. Test ChatClient output
        ChatClient chatClient = ChatClient.builder(chatModel).build();
        String response = chatClient.prompt()
                .user("What is the price of AAPL?")
                .tools(toolService)
                .call()
                .content();

        assertTrue(response.contains(String.valueOf(toolResult.currentPrice())));
        verify(finnHubStockMarketService).getSymbolQuote(symbol);
    }
}