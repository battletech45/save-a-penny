package com.saveapenny.assistant.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AssistantTimeConfig {

    @Bean
    public Clock assistantClock() {
        return Clock.systemDefaultZone();
    }
}
