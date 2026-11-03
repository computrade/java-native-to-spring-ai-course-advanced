package com.computrade.course.spring.ai.anthropic.config;


import com.anthropic.aws.backends.AwsBackend;
import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.computrade.course.spring.ai.anthropic.service.StockMarketToolService;
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
public class ChatAnthropicConfig {

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


    // Claude Platform on AWS client (SigV4). Reads AWS_REGION and ANTHROPIC_AWS_WORKSPACE_ID from env.
    // Used for message threads, which Spring AI's Bedrock Converse model cannot send.
    @Bean(destroyMethod = "close")
    public AnthropicClient anthropicAwsClient() {
        return AnthropicOkHttpClient.builder()
                .backend(AwsBackend.fromEnv())
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
