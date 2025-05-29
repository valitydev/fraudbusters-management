package dev.vality.fraudbusters.management.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestKafkaProducer<T> {

    private final KafkaTemplate<String, T> kafkaTemplate;

    public void send(String topic, T payload) throws ExecutionException, InterruptedException {
        log.info("Sending payload='{}' to topic='{}'", payload, topic);
        kafkaTemplate.send(topic, payload).get();
        kafkaTemplate.getProducerFactory().reset();
    }

    public void send(String topic, String key, T payload) throws ExecutionException, InterruptedException {
        log.info("Sending key='{}' payload='{}' to topic='{}'", key, payload, topic);
        kafkaTemplate.send(topic, key, payload).get();
        kafkaTemplate.getProducerFactory().reset();
    }

}