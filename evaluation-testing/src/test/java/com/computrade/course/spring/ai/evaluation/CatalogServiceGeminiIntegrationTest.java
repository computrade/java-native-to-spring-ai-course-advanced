package com.computrade.course.spring.ai.evaluation;

import com.computrade.course.spring.ai.evaluation.model.StoreCatalog;
import com.computrade.course.spring.ai.evaluation.service.CatalogService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.Timeout;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.FactCheckingEvaluator;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.document.Document;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // use same instance fot all tests in this class.
class CatalogServiceGeminiIntegrationTest {

    @Autowired
    private CatalogService catalogService;
    @Autowired
    private ChatClient.Builder chatClientBuilder;

    @Test
    @DisplayName("Should test Real Gemini Response Structure")
    @Timeout(value = 30)
    void testRealGeminiResponseStructure() {
        StoreCatalog catalog = catalogService.getCatalog("Gaming Laptops");

        assertNotNull(catalog);
        assertNotNull(catalog.storeName());
        assertFalse(catalog.storeName().isBlank());
        assertEquals("Gaming Laptops", catalog.recommendationCategory());
    }

    @Test
    @DisplayName("Should test Catalog with LLM As a Judge")
    void testCatalogRelevancyWithLLMAsAJudge() {
        // 1. Target Execution: Get generated response from your service
        String userQuery = "Gaming Laptops";
        StoreCatalog resultCatalog = catalogService.getCatalog(userQuery);

        // Convert structured result back to string context for evaluation
        String responseContent = resultCatalog.toString();

        // 2. Provide ground-truth context documents so the evaluator has reference context
        Document contextDocument = new Document("""
            Electronics catalog for Gaming Laptops must recommend a valid store name 
            and featured products related strictly to high-performance gaming laptops.
            """);

        // 3. Setup LLM-As-A-Judge Evaluator backed by Gemini ChatClient
        RelevancyEvaluator relevancyEvaluator = new RelevancyEvaluator(chatClientBuilder);

        // 3. Build Evaluation Request: (User Question, Context List, Target LLM Response)
        EvaluationRequest evaluationRequest = new EvaluationRequest(
                userQuery,
                List.of(contextDocument),
                responseContent
        );

        // 4. Evaluate target response
        EvaluationResponse evaluationResponse = relevancyEvaluator.evaluate(evaluationRequest);

        // 5. Assert that the evaluator marked the response as passing/relevant
        assertTrue(
                evaluationResponse.isPass(),
                "Evaluator rejected response: " + evaluationResponse.getFeedback()
        );
    }

    @Test
    @DisplayName("Should test Fact Checking with context")
    void testFactCheckingWithContext() {
        String userQuery = "What is the warranty policy for Gaming Laptops?";

        // Supporting ground-truth document context
        Document contextDocument = new Document("CyberByte Electronics provides a 2-year hardware warranty on all Gaming Laptops.");

        // AI generated response under test
        String aiGeneratedResponse = "CyberByte Electronics offers 2 years of hardware warranty coverage for gaming laptops.";

        FactCheckingEvaluator factCheckingEvaluator = FactCheckingEvaluator.builder (chatClientBuilder).build();

        EvaluationRequest request = new EvaluationRequest(
                userQuery,
                List.of(contextDocument),
                aiGeneratedResponse
        );

        EvaluationResponse response = factCheckingEvaluator.evaluate(request);

        assertTrue(response.isPass(), "Fact check failed: " + response.getFeedback());
    }
}
