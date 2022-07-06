package dev.vality.fraudbusters.management.utils;

import dev.vality.fraudbusters.warehouse.Result;
import dev.vality.fraudbusters.warehouse.Row;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ResultExtractorUtil {

    public static Integer getIntegerField(Result result, String field) {
        return result.getValues().stream()
                .findFirst()
                .map(Row::getValues)
                .map(rowFieldMap -> rowFieldMap.get(field))
                .map(Integer::parseInt)
                .orElse(-1);
    }

    public static Float getFloatField(Result result, String field) {
        return result.getValues().stream()
                .findFirst()
                .map(Row::getValues)
                .map(rowFieldMap -> rowFieldMap.get(field))
                .map(Float::parseFloat)
                .orElse(-1F);
    }
}
