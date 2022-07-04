package dev.vality.fraudbusters.management.service;

import dev.vality.fraudbusters.management.service.iface.SqlTimeSplitService;
import dev.vality.swag.fraudbusters.management.model.SplitUnit;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import static dev.vality.swag.fraudbusters.management.model.SplitUnit.*;
import static java.time.ZoneOffset.UTC;

@Service
public class ClickhouseTimeSplitService implements SqlTimeSplitService {

    @Override
    public String getSplitStatement(SplitUnit splitUnit) {
        return switch (splitUnit) {
            case MINUTE -> "timestamp as day, toHour(toDateTime(eventTime, 'UTC')) as hour, " +
                    "toMinute(toDateTime(eventTime, 'UTC')) as minutes";
            case HOUR -> "timestamp as day, toHour(toDateTime(eventTime, 'UTC')) as hour";
            case DAY -> "timestamp as day";
            case WEEK -> "toStartOfWeek(timestamp, 1) as week";
            case MONTH -> "toYear(timestamp) as year, toMonth(timestamp) as month";
            case YEAR -> "toYear(timestamp) as year";
        };
    }

    @Override
    public Long calculateSplitOffset(Map<String, String> row, SplitUnit splitUnit) {
        return switch (splitUnit) {
            case MINUTE -> {
                int hour = Integer.parseInt(row.get(HOUR.getValue()));
                int minutes = Integer.parseInt(row.get(MINUTE.getValue()));
                Date date = new Date(Long.parseLong(row.get(DAY.getValue())));
                LocalDateTime localDateTime = date.toLocalDate()
                        .atStartOfDay()
                        .plusHours(hour)
                        .plusMinutes(minutes);
                yield localDateTime
                        .atZone(UTC)
                        .toInstant().toEpochMilli();
            }
            case HOUR -> {
                int hour = Integer.parseInt(row.get(HOUR.getValue()));
                Date date = new Date(Long.parseLong(row.get(DAY.getValue())));
                LocalDateTime localDateTime = date.toLocalDate()
                        .atStartOfDay()
                        .plusHours(hour);
                yield localDateTime
                        .atZone(UTC)
                        .toInstant().toEpochMilli();
            }
            case DAY -> {
                Date date = new Date(Long.parseLong(row.get(DAY.getValue())));
                yield date.toLocalDate()
                        .atStartOfDay()
                        .atZone(UTC)
                        .toInstant().toEpochMilli();
            }
            case WEEK -> {
                Date date = new Date(Long.parseLong(row.get(WEEK.getValue())));
                yield date.toLocalDate()
                        .atStartOfDay()
                        .atZone(UTC)
                        .toInstant().toEpochMilli();
            }
            case MONTH -> {
                int year = Integer.parseInt(row.get(YEAR.getValue()));
                int months = Integer.parseInt(row.get(MONTH.getValue()));
                yield LocalDate.of(year, months, 1)
                        .atStartOfDay()
                        .atZone(UTC)
                        .toInstant().toEpochMilli();
            }
            case YEAR -> {
                int date = Integer.parseInt(row.get(YEAR.getValue()));
                yield LocalDate.of(date, 1, 1)
                        .atStartOfDay()
                        .atZone(UTC)
                        .toInstant().toEpochMilli();
            }
        };
    }
}
