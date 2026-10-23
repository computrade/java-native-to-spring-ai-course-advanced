package com.computrade.course.spring.ai.claude.service;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChatClient chatClient;

    public String chat(String convId, String prompt) {

        String response = chatClient.prompt().user(prompt)
                // Conversation id must be set to something.
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, convId))
                .call()
                .content();

        return response;
    }

    public String chat2(String convId, String prompt) {

        ChatResponse chatResponse = chatClient.prompt()
                .user(prompt)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, convId))
                .call()
                .chatResponse();

        if (chatResponse == null || chatResponse.getResults() == null) {
            return "";
        }

        // Iterate through results to find the non-empty text response block
        return chatResponse.getResults().stream()
                .map(generation -> generation.getOutput().getText())
                .filter(text -> text != null && !text.isBlank())
                .findFirst()
                .orElse("");
    }

    public String chatThinkingDisabled(String convId, String prompt) {

        // disable reasoning tokens, returning standard text in result[0]
        AnthropicChatOptions options = AnthropicChatOptions.builder()
                .thinkingDisabled()
                .build(); // Disables reasoning tokens, returning standard text in result[0]


        return chatClient.prompt()
                .user(prompt)
                //.options(options)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, convId))
                .call()
                .content();
    }


}
