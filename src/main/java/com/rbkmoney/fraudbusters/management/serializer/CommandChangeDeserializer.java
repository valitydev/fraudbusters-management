package com.rbkmoney.fraudbusters.management.serializer;


import com.rbkmoney.kafka.common.serialization.AbstractThriftDeserializer;
import dev.vality.damsel.wb_list.ChangeCommand;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommandChangeDeserializer extends AbstractThriftDeserializer<ChangeCommand> {

    @Override
    public ChangeCommand deserialize(String topic, byte[] data) {
        return deserialize(data, new ChangeCommand());
    }
}