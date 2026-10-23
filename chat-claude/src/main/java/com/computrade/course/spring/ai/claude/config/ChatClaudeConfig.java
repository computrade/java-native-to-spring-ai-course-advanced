package com.computrade.course.spring.ai.claude.config;


import com.computrade.course.spring.ai.claude.advisor.SingleCompletionAdvisor;
import com.computrade.course.spring.ai.claude.service.StockMarketToolService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;


@Configuration
public class ChatClaudeConfig {


    // Exists only when Claude is the selected chat model
    @Bean
    @ConditionalOnProperty(name = "spring.ai.model.chat", havingValue = "anthropic")
    SingleCompletionAdvisor singleCompletionAdvisor() {
        return new SingleCompletionAdvisor();
    }

    @Bean
    public ChatClient defaultChatClient(ChatClient.Builder builder,
                                        ChatMemory chatMemory,
                                        ObjectProvider<SingleCompletionAdvisor> normalizer,
                                        StockMarketToolService stockMarketToolService) {

        List<Advisor> advisors = new ArrayList<>();
        normalizer.ifAvailable(advisors::add);   // Claude only
        advisors.add(MessageChatMemoryAdvisor.builder(chatMemory).build());
        advisors.add(new SimpleLoggerAdvisor());

        return builder
                .defaultAdvisors(advisors)
                .defaultTools(stockMarketToolService)
                .build();
    }


    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public RestClient finnHubRestClient(RestClient.Builder builder) {
        return builder.baseUrl("https://finnhub.io/api/v1").build();
    }

}
