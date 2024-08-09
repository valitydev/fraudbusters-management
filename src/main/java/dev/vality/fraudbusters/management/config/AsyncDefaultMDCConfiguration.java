package dev.vality.fraudbusters.management.config;

import dev.vality.fraudbusters.management.logging.MDCTaskDecorator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@SuppressWarnings("AbbreviationAsWordInName")
@ConditionalOnProperty(
        value = "spring.threads.virtual.enabled",
        havingValue = "false",
        matchIfMissing = true)
public class AsyncDefaultMDCConfiguration {

    @Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setTaskDecorator(new MDCTaskDecorator());
        executor.initialize();
        return executor;
    }

}
