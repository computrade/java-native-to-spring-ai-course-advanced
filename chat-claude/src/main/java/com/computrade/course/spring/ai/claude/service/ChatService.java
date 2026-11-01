package com.computrade.course.spring.ai.claude.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

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

}
