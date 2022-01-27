package com.rbkmoney.fraudbusters.management.serializer;


import com.rbkmoney.kafka.common.serialization.AbstractThriftDeserializer;
import dev.vality.damsel.fraudbusters.Command;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommandFraudDeserializer extends AbstractThriftDeserializer<Command> {

    @Override
    public Command deserialize(String topic, byte[] data) {
        return deserialize(data, new Command());
    }
}