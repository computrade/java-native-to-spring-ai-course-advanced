package com.computrade.course.spring.ai.evaluation;

import com.computrade.course.spring.ai.evaluation.model.StoreCatalog;
import com.computrade.course.spring.ai.evaluation.service.CatalogService;
import org.junit.jupiter.api.DisplayName;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogServiceGeminiTest {

    @Mock
    private ChatModel chatModel;

    @Test
    @DisplayName("Test Catalog With Mocked GeminiLLM")
    void testGetCatalogWithMockedGeminiLLM() {

        // 1. Stub getOptions() to return non-null ChatOptions to prevent NullPointerException
        when(chatModel.getOptions()).thenReturn(ChatOptions.builder().build());

        // Expected JSON payload returned by Gemini model
        String jsonResponse = """
            {
                "storeName": "CyberByte Electronics",
                "recommendationCategory": "Gaming Laptops",
                "featuredProducts": [
                    {
                        "manufacturer": "ASUS",
                        "productName": "ASUS ROG Zephyrus G16",
                        "priceUSD": 1999.99,
                        "status": "AVAILABLE"
                    }
                ]
            }
            """;

        AssistantMessage assistantMessage = new AssistantMessage(jsonResponse);
        Generation generation = new Generation(assistantMessage);
        ChatResponse chatResponse = new ChatResponse(List.of(generation));

        when(chatModel.call(any(Prompt.class))).thenReturn(chatResponse);

        ChatClient.Builder builder = ChatClient.builder(chatModel);
        CatalogService service = new CatalogService(builder);

        StoreCatalog result = service.getCatalog("Gaming Laptops");

        assertNotNull(result);
        assertEquals("CyberByte Electronics", result.storeName());
        assertEquals("Gaming Laptops", result.recommendationCategory());
        // Assertions for featuredProducts
        assertNotNull(result.featuredProducts());
        assertFalse(result.featuredProducts().isEmpty());
        assertEquals(1, result.featuredProducts().size());

        // Detailed product-level assertions
        var firstProduct = result.featuredProducts().get(0);
        assertEquals("ASUS", firstProduct.manufacturer());
        assertEquals("ASUS ROG Zephyrus G16", firstProduct.productName());
        assertEquals(1999.99, firstProduct.priceUSD());
    }
}