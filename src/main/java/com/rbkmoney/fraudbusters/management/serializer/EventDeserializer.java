package com.rbkmoney.fraudbusters.management.serializer;


import com.rbkmoney.kafka.common.serialization.AbstractThriftDeserializer;
import dev.vality.damsel.wb_list.Event;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventDeserializer extends AbstractThriftDeserializer<Event> {

    @Override
    public Event deserialize(String topic, byte[] data) {
        return deserialize(data, new Event());
    }
}