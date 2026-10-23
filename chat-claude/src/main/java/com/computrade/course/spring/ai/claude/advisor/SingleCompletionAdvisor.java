package com.computrade.course.spring.ai.claude.advisor;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.core.Ordered;

import java.util.List;
import java.util.stream.Collectors;

public class SingleCompletionAdvisor implements CallAdvisor {

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
        ChatClientResponse response = chain.nextCall(request);
        ChatResponse chat = response.chatResponse();
        if (chat == null || chat.getResults().size() <= 1) return response;

        String text = chat.getResults().stream()
                .map(Generation::getOutput)
                .filter(m -> !m.getMetadata().containsKey("signature")
                        && !m.getMetadata().containsKey("data"))
                .map(AssistantMessage::getText)
                .filter(t -> t != null && !t.isBlank())
                .collect(Collectors.joining());

        ChatResponse merged = ChatResponse.builder()
                .from(chat)
                .generations(List.of(new Generation(
                        AssistantMessage.builder().content(text).build())))
                .build();
        return response.mutate().chatResponse(merged).build();
    }

    @Override public String getName()  { return "SingleCompletionAdvisor"; }
    @Override public int    getOrder() { return Ordered.HIGHEST_PRECEDENCE; }
}