package com.computrade.course.spring.ai.claude.controller;

import com.computrade.course.spring.ai.claude.service.ChatService;
import com.computrade.course.spring.ai.claude.service.VectorDBService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/claude")
@RequiredArgsConstructor
public class ChatClaudeController {

    private final ChatService chatService;
    private final VectorDBService vectorDBService;

    @GetMapping("/chat/{userId}")
    public ResponseEntity<String> chat(
            @PathVariable String userId,
            @RequestParam String prompt) {

        // Keep data for userId, so that the conversation is remembered for the same userId
        String chatResponse = chatService.chat(userId, prompt);
        return ResponseEntity.ok(chatResponse);
    }

    @GetMapping("/chat/{userId}/anthropic")
    public ResponseEntity<String> chatAnthropic(
            @PathVariable String userId,
            @RequestParam String prompt) {

        // Keep data for userId, so that the conversation is remembered for the same userId
        String chatResponse = chatService.chat2(userId, prompt);
        return ResponseEntity.ok(chatResponse);
    }

    @PostMapping("/ingest")
    public ResponseEntity<String> ingestData() {
        vectorDBService.ingestCoursesToVectorStore();
        return ResponseEntity.ok("Data sent to Vector Store successfully!");
    }


    @GetMapping("/embedding")
    public ResponseEntity<Map<String, EmbeddingResponse>> embed(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        Map<String,EmbeddingResponse> embeddingResponse = vectorDBService.embed(message);
        return ResponseEntity.ok(embeddingResponse);
    }

    @GetMapping("/chat/{userId}/vectorStore")
    public ResponseEntity<String> queryVectorStore(@PathVariable String userId,  @RequestParam String prompt) {
        String response = vectorDBService.queryVectorStore(userId, prompt);
        return ResponseEntity.ok(response);
    }

}
