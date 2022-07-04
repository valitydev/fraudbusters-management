package dev.vality.fraudbusters.management.service;

import dev.vality.fraudbusters.management.service.clickhouse.AnalyticsQuery;
import dev.vality.fraudbusters.management.service.iface.BaseAnalyticsService;
import dev.vality.fraudbusters.management.service.iface.SqlTimeSplitService;
import dev.vality.fraudbusters.management.service.iface.WarehouseQueryService;
import dev.vality.fraudbusters.warehouse.Query;
import dev.vality.fraudbusters.warehouse.Result;
import dev.vality.fraudbusters.warehouse.Row;
import dev.vality.swag.fraudbusters.management.model.SplitUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static dev.vality.fraudbusters.management.constant.AnalyticsResultField.*;

@Service
@RequiredArgsConstructor
public class BaseAnalyticsServiceImpl implements BaseAnalyticsService {

    private final WarehouseQueryService warehouseQueryService;
    private final SqlTimeSplitService sqlTimeSplitService;

    @Override
    public Integer getBlockedFraudPaymentsCount(Map<String, String> params) {
        Query query = buildQuery(params, AnalyticsQuery.BLOCKED_FRAUD_PAYMENTS_COUNT);
        Result result = warehouseQueryService.execute(query);
        return getResultIntegerField(result, COUNT);
    }

    private Query buildQuery(Map<String, String> params, String statement) {
        Query query = new Query();
        query.setParams(params);
        query.setStatement(statement);
        return query;
    }

    private Integer getResultIntegerField(Result result, String field) {
        return result.getValues().stream()
                .findFirst()
                .map(Row::getValues)
                .map(rowFieldMap -> rowFieldMap.get(field))
                .map(Integer::parseInt)
                .orElse(-1);
    }

    @Override
    public Float getBlockedFraudPaymentsCountRatio(Map<String, String> params) {
        Query query = buildQuery(params, AnalyticsQuery.BLOCKED_FRAUD_PAYMENTS_COUNT_RATIO);
        Result result = warehouseQueryService.execute(query);
        return getResultFloatField(result);
    }

    private Float getResultFloatField(Result result) {
        return result.getValues().stream()
                .findFirst()
                .map(Row::getValues)
                .map(rowFieldMap -> rowFieldMap.get(RATIO))
                .map(Float::parseFloat)
                .orElse(-1F);
    }

    @Override
    public Integer getBlockedFraudPaymentsSum(Map<String, String> params) {
        Query query = buildQuery(params, AnalyticsQuery.BLOCKED_FRAUD_PAYMENTS_SUM);
        Result result = warehouseQueryService.execute(query);
        return getResultIntegerField(result, SUM);
    }

    @Override
    public Integer getFraudPaymentsCount(Map<String, String> params) {
        Query query = buildQuery(params, AnalyticsQuery.FRAUD_PAYMENTS_COUNT);
        Result result = warehouseQueryService.execute(query);
        return getResultIntegerField(result, COUNT);
    }


    @Override
    public List<Map<String, String>> getFraudPaymentsResultsSummary(Map<String, String> params) {
        Query query = buildQuery(params, AnalyticsQuery.FRAUD_PAYMENTS_RESULTS_SUMMARY);
        Result result = warehouseQueryService.execute(query);
        return result.getValues().stream()
                .map(Row::getValues)
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, String>> getFraudPaymentsScoreSplitCountRatio(Map<String, String> params,
                                                                          SplitUnit splintUnit) {
        String splitStatement = sqlTimeSplitService.getSplitStatement(splintUnit);
        String statement = String.format(AnalyticsQuery.FRAUD_PAYMENTS_SCORE_SPLIT_COUNT_RATIO, splitStatement);
        Query query = buildQuery(params, statement);
        Result result = warehouseQueryService.execute(query);
        return result.getValues().stream()
                .map(Row::getValues)
                .collect(Collectors.toList());
    }
}
