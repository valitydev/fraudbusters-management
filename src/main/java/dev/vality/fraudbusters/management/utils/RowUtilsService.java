package dev.vality.fraudbusters.management.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.vality.damsel.wb_list.CountInfo;
import dev.vality.damsel.wb_list.Row;
import dev.vality.damsel.wb_list.RowInfo;
import dev.vality.fraudbusters.management.domain.enums.ListType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RowUtilsService {

    private final ObjectMapper objectMapper;

    public ListType initListType(Row destination) {
        return switch (destination.getListType()) {
            case grey -> ListType.grey;
            case black -> ListType.black;
            case white -> ListType.white;
            case naming -> ListType.naming;
        };
    }

    public String initRowInfo(Row destination) {
        if (destination.getRowInfo() != null && destination.getRowInfo().isSetCountInfo()) {
            try {
                return objectMapper.writeValueAsString(destination.getRowInfo().getCountInfo());
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Unknown list type!");
            }
        }
        return null;
    }

    public LocalDateTime getTimeToLive(Row destination) {
        return Optional.ofNullable(destination)
                .map(Row::getRowInfo)
                .map(RowInfo::getCountInfo)
                .map(CountInfo::getTimeToLive)
                .map(Instant::parse)
                .map(instant -> LocalDateTime.ofInstant(instant, ZoneOffset.UTC))
                .orElseGet(() -> null);
    }

}
