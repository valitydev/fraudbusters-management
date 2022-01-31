package dev.vality.fraudbusters.management.converter.payment;

import dev.vality.damsel.wb_list.Event;
import dev.vality.fraudbusters.management.domain.tables.pojos.WbListRecords;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
@RequiredArgsConstructor
public class EventToListRecordConverter implements Converter<Event, WbListRecords> {

    private final RowToWbListRecordsConverter rowToWbListRecordsConverter;

    @Override
    public WbListRecords convert(Event event) {
        WbListRecords record = rowToWbListRecordsConverter.convert(event.getRow());
        record.setId(UUID.randomUUID().toString());
        return record;
    }
}
