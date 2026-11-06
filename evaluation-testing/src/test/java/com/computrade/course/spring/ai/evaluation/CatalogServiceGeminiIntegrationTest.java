package com.computrade.course.spring.ai.evaluation;

import com.computrade.course.spring.ai.evaluation.model.StoreCatalog;
import com.computrade.course.spring.ai.evaluation.service.CatalogService;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.FactCheckingEvaluator;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.document.Document;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CatalogServiceGeminiIntegrationTest {

    @Autowired
    private CatalogService catalogService;
    @Autowired
    private ChatClient.Builder chatClientBuilder;

    @Test
    void testRealGeminiResponseStructure() {
        StoreCatalog catalog = catalogService.getCatalog("Gaming Laptops");

        assertNotNull(catalog);
        assertNotNull(catalog.storeName());
        assertFalse(catalog.storeName().isBlank());
        assertEquals("Gaming Laptops", catalog.recommendationCategory());
    }

    @Test
    void testCatalogRelevancyWithLLMAsAJudge() {
        // 1. Target Execution: Get generated response from your service
        String userQuery = "Gaming Laptops";
        StoreCatalog resultCatalog = catalogService.getCatalog(userQuery);

        // Convert structured result back to string context for evaluation
        String responseContent = resultCatalog.toString();

        // 2. Setup LLM-As-A-Judge Evaluator backed by Gemini ChatClient
        RelevancyEvaluator relevancyEvaluator = new RelevancyEvaluator(chatClientBuilder);

        // 3. Build Evaluation Request: (User Question, Context List, Target LLM Response)
        EvaluationRequest evaluationRequest = new EvaluationRequest(
                userQuery,
                Collections.emptyList(), // Optional RAG context documents
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
    void testFactCheckingWithContext() {
        String userQuery = "What is the warranty policy for Gaming Laptops?";

        // Supporting ground-truth document context
        Document contextDocument = new Document("CyberByte Electronics provides a 2-year hardware warranty on all Gaming Laptops.");

        // AI generated response under test
        String aiGeneratedResponse = "CyberByte Electronics offers 2 years of hardware warranty coverage for gaming laptops.";

        FactCheckingEvaluator factCheckingEvaluator = FactCheckingEvaluator.forBespokeMinicheck(chatClientBuilder);

        EvaluationRequest request = new EvaluationRequest(
                userQuery,
                List.of(contextDocument),
                aiGeneratedResponse
        );

        EvaluationResponse response = factCheckingEvaluator.evaluate(request);

        assertTrue(response.isPass(), "Fact check failed: " + response.getFeedback());
    }
}
