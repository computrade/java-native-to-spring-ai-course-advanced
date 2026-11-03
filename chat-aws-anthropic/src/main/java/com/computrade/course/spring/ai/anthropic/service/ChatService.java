package com.computrade.course.spring.ai.anthropic.service;

import com.anthropic.client.AnthropicClient;
import com.anthropic.helpers.BetaMessageThread;
import com.anthropic.models.beta.messages.BetaMessage;
import com.anthropic.models.beta.messages.BetaStopReason;
import com.anthropic.models.beta.messages.ThreadCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ChatService {

    // In-memory store: Session ID -> Message Thread helper (holds the last response id and local history).
    private final Map<String, BetaMessageThread> sessionThreadStore = new ConcurrentHashMap<>();
    private final ChatClient chatClient;
    private final AnthropicClient anthropicClient;

    public String chat(String convId, String prompt) {

        String response = chatClient.prompt().user(prompt)
                // Conversation id must be set to something.
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, convId))
                .call()
                .content();

        return response;
    }


    public String processAgentTurn(String sessionId, String userMessage) {
        // The helper sends thread={"type":"create"} on turn 1 and thread={"type":"continue",
        // "previous_message_id":...} with only the new messages afterwards. It adds the
        // message-threads beta header, and replays the full history if the thread expired (404).
        BetaMessageThread thread = sessionThreadStore.computeIfAbsent(sessionId, id ->
                anthropicClient.beta().messages().thread(
                        ThreadCreateParams.builder()
                                .model("claude-opus-5-5")
                                .maxTokens(16000L)
                                .system("You are a helpful assistant.")
                                .build()));

        // Each response id can be continued only once, so turns on the same session must not overlap.
        BetaMessage response;
        synchronized (thread) {
            response = thread.send(userMessage);
        }

        if (response.stopReason().filter(BetaStopReason.REFUSAL::equals).isPresent()) {
            return "The request was declined.";
        }

        return response.content().stream()
                .flatMap(block -> block.text().stream())
                .map(textBlock -> textBlock.text())
                .collect(Collectors.joining());
    }

}
