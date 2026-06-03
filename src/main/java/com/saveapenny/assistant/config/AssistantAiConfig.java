package com.saveapenny.assistant.config;

import io.micrometer.observation.ObservationRegistry;
import java.util.List;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.model.tool.DefaultToolCallingManager;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.tool.execution.DefaultToolExecutionExceptionProcessor;
import org.springframework.ai.tool.resolution.StaticToolCallbackResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@ConditionalOnProperty(prefix = "assistant", name = "enabled", havingValue = "true")
public class AssistantAiConfig {

    @Bean
    public ChatClient assistantChatClient(
            AssistantProperties assistantProperties,
            @Value("${spring.ai.openai.api-key:}") String apiKey,
            RestClient.Builder restClientBuilder,
            WebClient.Builder webClientBuilder,
            ObservationRegistry observationRegistry) {
        OpenAiApi openAiApi = buildOpenAiApi(
                assistantProperties,
                apiKey,
                restClientBuilder,
                webClientBuilder);

        OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
                .model(resolveModel(assistantProperties.model()))
                .build();

        ToolCallingManager toolCallingManager = DefaultToolCallingManager.builder()
                .observationRegistry(observationRegistry)
                .toolCallbackResolver(new StaticToolCallbackResolver(List.of()))
                .toolExecutionExceptionProcessor(new DefaultToolExecutionExceptionProcessor(false))
                .build();

        OpenAiChatModel chatModel = new OpenAiChatModel(
                openAiApi,
                chatOptions,
                toolCallingManager,
                RetryTemplate.defaultInstance(),
                observationRegistry);

        return ChatClient.create(chatModel);
    }

    OpenAiApi buildOpenAiApi(
            AssistantProperties assistantProperties,
            String openAiApiKey,
            RestClient.Builder restClientBuilder,
            WebClient.Builder webClientBuilder) {
        String provider = normalizeProvider(assistantProperties.provider());
        OpenAiApi.Builder builder = OpenAiApi.builder()
                .restClientBuilder(restClientBuilder)
                .webClientBuilder(webClientBuilder);

        return switch (provider) {
            case "openai" -> builder
                    .apiKey(requireApiKey(openAiApiKey, "spring.ai.openai.api-key"))
                    .build();
            case "openrouter" -> builder
                    .apiKey(requireApiKey(assistantProperties.openrouterApiKey(), "assistant.openrouter-api-key"))
                    .baseUrl(assistantProperties.openrouterBaseUrl())
                    .completionsPath("/v1/chat/completions")
                    .embeddingsPath("/v1/embeddings")
                    .headers(buildOpenRouterHeaders(assistantProperties))
                    .build();
            default -> throw new IllegalStateException(
                    "Unsupported assistant provider '" + provider + "'. Expected 'openai' or 'openrouter'.");
        };
    }

    private String normalizeProvider(String provider) {
        return StringUtils.hasText(provider) ? provider.trim().toLowerCase() : "openai";
    }

    private String resolveModel(String model) {
        return StringUtils.hasText(model) ? model.trim() : "gpt-4.1-mini";
    }

    private String requireApiKey(String apiKey, String propertyName) {
        if (!StringUtils.hasText(apiKey)) {
            throw new IllegalStateException("Assistant is enabled but " + propertyName + " is not configured.");
        }
        return apiKey;
    }

    private MultiValueMap<String, String> buildOpenRouterHeaders(AssistantProperties assistantProperties) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        if (StringUtils.hasText(assistantProperties.openrouterSiteUrl())) {
            headers.add("HTTP-Referer", assistantProperties.openrouterSiteUrl().trim());
        }
        if (StringUtils.hasText(assistantProperties.openrouterAppName())) {
            headers.add("X-Title", assistantProperties.openrouterAppName().trim());
        }
        return headers;
    }
}
