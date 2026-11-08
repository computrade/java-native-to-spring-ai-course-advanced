package com.computrade.course.spring.ai.evaluation.advisor;

import com.computrade.course.spring.ai.evaluation.model.SensitiveWordsConfig;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class CaseInsensitiveSafeGuardAdvisor implements CallAdvisor {

    private static final String DEFAULT_FAILURE_RESPONSE = "🛑 Security Exception: Prompt blocked due to sensitive data policies.";

    private final JsonMapper jsonMapper; // in Jackson 3, ObjectMapper is replaced with JsonMapper for better performance and features
    private List<String> lowercaseSensitiveWords = new ArrayList<>();

    @Value("classpath:configs/sensitive-words.json")
    private Resource sensitiveWordsFile;


    
    @Override
    public @NonNull ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, @NonNull CallAdvisorChain callAdvisorChain) {

        String promptContent = chatClientRequest.prompt().getContents();
        // 2. Normalize user input to lowercase
        String lowerCaseContent = promptContent.toLowerCase();

        // 3. Perform case-insensitive matching against our pre-processed list
        if (!this.lowercaseSensitiveWords.isEmpty()
                && this.lowercaseSensitiveWords.stream().anyMatch(lowerCaseContent::contains)) {
            // Short-circuit the pipeline and return the fallback message
            return createFailureResponse(chatClientRequest);
        }

        // 4. Content is clean, move to the next advisor or call the LLM
        return callAdvisorChain.nextCall(chatClientRequest);
    }

    private ChatClientResponse createFailureResponse(ChatClientRequest chatClientRequest) {
        return ChatClientResponse.builder()
                .chatResponse(ChatResponse.builder()
                        .generations(List.of(new Generation(new AssistantMessage(DEFAULT_FAILURE_RESPONSE))))
                        .build())
                .context(Map.copyOf(chatClientRequest.context()))
                .build();
    }

    @Override
    public @NonNull String getName() {
        return CaseInsensitiveSafeGuardAdvisor.class.getName();
    }

    @Override
    public int getOrder() {
        // Always run at the absolute front of the line to catch unsafe content early
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @PostConstruct
    private void initSensitiveWords() {

        try (
            InputStream inputStream = sensitiveWordsFile.getInputStream()) {
            // 2. Use the injected mapper to read the file into a SensitiveWordsConfig object
            SensitiveWordsConfig config = jsonMapper.readValue(inputStream, SensitiveWordsConfig.class);
            List<String> sensitiveWordsList = config.getAllWords();

            if (!CollectionUtils.isEmpty(sensitiveWordsList)) {
                this.lowercaseSensitiveWords = sensitiveWordsList.stream()
                        .map(String::toLowerCase)
                        .collect(Collectors.toList());
            }

        } catch (IOException e) {
            log.error("Failed to load sensitive words JSON file configs/sensitive-words.json", e);
            throw new RuntimeException("Failed to load sensitive words JSON file", e);
        }
    }
}

