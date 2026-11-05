package com.computrade.course.spring.ai.image.config;


import com.computrade.course.spring.ai.image.service.StockMarketToolService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;


@Configuration
public class ChatGeminiConfig {

    @Bean
    public ChatClient defaultChatClient(ChatClient.Builder builder,
                                        ChatMemory chatMemory,
                                        StockMarketToolService stockMarketToolService) {

        List<Advisor> advisors = new ArrayList<>();
        advisors.add(MessageChatMemoryAdvisor.builder(chatMemory).build());
        advisors.add(new SimpleLoggerAdvisor());

        return builder
                .defaultAdvisors(advisors)
                .defaultTools(stockMarketToolService)
                .build();
    }


    // Pin the JDK HttpClient: the Anthropic SDK brings Apache HttpClient 5, which RestClient
    // would otherwise auto-select, and its gzip handling truncates Finnhub responses to "{".
    @Bean
    public RestClient finnHubRestClient() {
        return RestClient.builder()
                .requestFactory(new JdkClientHttpRequestFactory())
                .baseUrl("https://finnhub.io/api/v1")
                .build();
    }

}
