package dev.vality.fraudbusters.management.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestKafkaProducer<T> {

    private final KafkaTemplate<String, T> kafkaTemplate;

    public void send(String topic, T payload) {
        log.info("Sending payload='{}' to topic='{}'", payload, topic);
        kafkaTemplate.send(topic, payload).completable().join();
        kafkaTemplate.getProducerFactory().reset();
    }

    public void send(String topic, String key, T payload) {
        log.info("Sending key='{}' payload='{}' to topic='{}'", key, payload, topic);
        kafkaTemplate.send(topic, key, payload).completable().join();
        kafkaTemplate.getProducerFactory().reset();
    }

}