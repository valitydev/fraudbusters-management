package com.rbkmoney.fraudbusters.management.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.fraudbusters.management.domain.CountInfo;
import dev.vality.damsel.wb_list.Row;
import dev.vality.damsel.wb_list.RowInfo;
import io.micrometer.shaded.io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CountInfoUtils {

    private final ObjectMapper objectMapper;

    public CountInfo initRowCountInfo(String rowInfo) {
        var countInfoValue = new CountInfo();
        try {
            var countInfo = objectMapper.readValue(rowInfo, dev.vality.damsel.wb_list.CountInfo.class);
            countInfoValue.setCount(countInfo.getCount());
            countInfoValue.setEndCountTime(countInfo.getTimeToLive());
            countInfoValue.setStartCountTime(countInfo.getStartCountTime());
        } catch (IOException e) {
            throw new RuntimeException("Error when read countInfo for rowInfo: " + rowInfo, e);
        }
        return countInfoValue;
    }

    public void initRowCountInfo(CountInfo countInfo, Row row) {
        String startCountTime = StringUtil.isNullOrEmpty(countInfo.getStartCountTime())
                ? Instant.now().toString()
                : countInfo.getStartCountTime();
        row.setRowInfo(RowInfo.count_info(new dev.vality.damsel.wb_list.CountInfo()
                .setCount(countInfo.getCount())
                .setStartCountTime(startCountTime)
                .setTimeToLive(countInfo.getEndCountTime())));
    }

}
