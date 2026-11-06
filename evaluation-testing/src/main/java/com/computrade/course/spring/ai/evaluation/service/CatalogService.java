package com.computrade.course.spring.ai.evaluation.service;

import com.computrade.course.spring.ai.evaluation.model.StoreCatalog;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class CatalogService {

    private final ChatClient chatClient;

    public CatalogService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public StoreCatalog getCatalog(String category) {
        return this.chatClient.prompt()
                .user(u -> u.text("Generate a realistic electronics store recommendations catalog for the category: {category}. " +
                                "Provide a fitting store name and exactly 5 top featured products.")
                        .param("category", category))
                .call()
                .entity(StoreCatalog.class, spec -> spec.useProviderStructuredOutput()
                        // Automatically retries if LLM response fails validation
                        .validateSchema());
    }
}
