package com.rbkmoney.fraudbusters.management.config;

import com.rbkmoney.fraudbusters.management.serializer.CommandChangeDeserializer;
import dev.vality.damsel.wb_list.ChangeCommand;
import dev.vality.testcontainers.annotations.kafka.config.KafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsumerConfig {

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public KafkaConsumer<ChangeCommand> testChangeCommandKafkaConsumer() {
        return new KafkaConsumer<>(bootstrapServers, new CommandChangeDeserializer());
    }
}
