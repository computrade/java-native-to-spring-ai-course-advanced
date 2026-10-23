package com.computrade.course.spring.ai.agent.brwose.config;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.List;


@Configuration
public class McpClientConfig {


    @Bean
    public ChatClient defaultChatClient(ChatClient.Builder builder,
                                        ChatMemory chatMemory,
                                        ToolCallbackProvider toolCallbackProvider) {
        Advisor loggerAdvisor = new SimpleLoggerAdvisor();
        Advisor messageChatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();

        return builder
                .defaultAdvisors(List.of(loggerAdvisor, messageChatMemoryAdvisor))
                .defaultTools(toolCallbackProvider)
                .build();
    }


}
