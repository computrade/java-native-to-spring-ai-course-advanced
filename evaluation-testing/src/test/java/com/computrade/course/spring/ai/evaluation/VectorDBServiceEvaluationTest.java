package com.computrade.course.spring.ai.evaluation;

import com.computrade.course.spring.ai.evaluation.service.VectorDBService;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.document.Document;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class VectorDBServiceEvaluationTest {

    @Autowired
    private VectorDBService vectorDBService;

    @Autowired
    private ChatClient.Builder chatClientBuilder;

    @Test
    void testVectorStoreRAGResponseRelevancy() {
        String convId = "eval-session-999";
        String userQuery = "Recommend Java courses with high ratings.";

        // Execute RAG query against VectorDBService
        String response = vectorDBService.queryVectorStore(convId, userQuery);

        // Ground-truth context document representing expected domain knowledge
        Document referenceDoc = new Document("Course: Java for Enterprise Apps. Category: Development. Description: Master Java syntax, object-oriented concepts, and Spring Boot framework for robust enterprise software solutions.");

        // Evaluator powered by Gemini ChatClient
        RelevancyEvaluator evaluator = new RelevancyEvaluator(chatClientBuilder);

        EvaluationRequest request = new EvaluationRequest(
                userQuery,
                List.of(referenceDoc),
                response
        );

        EvaluationResponse evalResponse = evaluator.evaluate(request);

        assertTrue(
                evalResponse.isPass(),
                "Relevancy evaluation failed: " + evalResponse.getFeedback()
        );
    }

    @Test
    void testQueryVectorStoreUnknownSubjectFallback() {
        String convId = "eval-session-102";
        String userQuery = "How do I bake a French sourdough bread from scratch?";

        // 1. Execute query for topic completely absent from course dataset
        String actualResponse = vectorDBService.queryVectorStore(convId, userQuery);
        assertNotNull(actualResponse);

        Document noInfo = new Document("I don't have any information about that");

        // 3. Evaluate whether actualResponse adheres to the fallback policy
        RelevancyEvaluator evaluator = new RelevancyEvaluator(chatClientBuilder);
        EvaluationRequest request = new EvaluationRequest(
                userQuery,
                List.of(noInfo),
                actualResponse
        );

        EvaluationResponse evalResponse = evaluator.evaluate(request);

        // Standard String check combined with LLM-As-A-Judge verification
        assertTrue(
                actualResponse.toLowerCase().contains("don't have") ||
                        actualResponse.toLowerCase().contains("any information") ||
                        evalResponse.isPass(),
                "Expected fallback message for unknown topic, but got: " + actualResponse
        );
    }
}
