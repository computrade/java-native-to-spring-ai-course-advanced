package com.computrade.course.spring.ai.agent.brwose.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatService {

    @Value("classpath:prompts/visual-agent-system.st")
    private Resource visualAgentResource;

    @Value("classpath:prompts/ecommerce-agent-system.st")
    private Resource eCommerceAgentResource;


    @Value("classpath:prompts/form-assistant-system.st")
    private Resource formAssistantResource;


    @Value("classpath:prompts/linkedin-agent-system.st")
    private Resource linkedInAssistantResource;

    private final ChatClient chatClient;

    public String chatWithVisualAgent(String prompt) {
        return this.chatClient.prompt()
                .user(prompt)
                .system(visualAgentResource)
                .call()
                .content();
    }

    public String chatEcommerceAgent(String prompt) {
        return this.chatClient.prompt()
                .user(prompt)
                .system(eCommerceAgentResource)
                .call()
                .content();
    }

    public String chatFormAssistantAgent(String prompt) {
        return this.chatClient.prompt()
                .user(prompt)
                .system(formAssistantResource)
                .call()
                .content();
    }

    public String chatLinkedInAgent(String convId, String prompt) {

        String response = chatClient.prompt().user(prompt)
                // Conversation id must be set to something.
                .system(linkedInAssistantResource)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, convId))
                .call()
                .content();

        return response;
    }

}
