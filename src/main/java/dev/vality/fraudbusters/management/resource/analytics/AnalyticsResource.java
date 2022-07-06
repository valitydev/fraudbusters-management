package dev.vality.fraudbusters.management.resource.analytics;

import dev.vality.fraudbusters.management.converter.QueryParamsToAnalyticParamsConverter;
import dev.vality.fraudbusters.management.converter.RowListToFraudResultSummaryListConverter;
import dev.vality.fraudbusters.management.converter.RowListToRiskScoreOffsetCountRatioListConverter;
import dev.vality.fraudbusters.management.service.iface.BaseAnalyticsService;
import dev.vality.swag.fraudbusters.management.api.AnalyticsApi;
import dev.vality.swag.fraudbusters.management.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AnalyticsResource implements AnalyticsApi {

    private final BaseAnalyticsService baseAnalyticsService;
    private final RowListToFraudResultSummaryListConverter fraudResultSummaryListConverter;
    private final RowListToRiskScoreOffsetCountRatioListConverter countRatioListConverter;
    private final QueryParamsToAnalyticParamsConverter paramsConverter;

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<CountResponse> getBlockedFraudPaymentsCount(
            @NotNull @Valid String fromTime,
            @NotNull @Valid String toTime,
            @NotNull @Valid String currency,
            @Valid String merchantId,
            @Valid String shopId) {
        Map<String, String> params = paramsConverter.convert(fromTime, toTime, currency, merchantId, shopId);
        log.info("-> getBlockedFraudPaymentsCount with params: {}", params);
        Integer blockedFraudPaymentsCount = baseAnalyticsService.getBlockedFraudPaymentsCount(params);
        log.info("<- getBlockedFraudPaymentsCount result: {}", blockedFraudPaymentsCount);
        return ResponseEntity.ok(
                new CountResponse()
                        .count(blockedFraudPaymentsCount)
        );
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<RatioResponse> getBlockedFraudPaymentsCountRatio(
            @NotNull @Valid String fromTime,
            @NotNull @Valid String toTime,
            @NotNull @Valid String currency,
            @Valid String merchantId,
            @Valid String shopId) {
        Map<String, String> params = paramsConverter.convert(fromTime, toTime, currency, merchantId, shopId);
        log.info("-> getBlockedFraudPaymentsCountRatio with params: {}", params);
        Float blockedFraudPaymentsCountRatio = baseAnalyticsService.getBlockedFraudPaymentsCountRatio(params);
        log.info("<- getBlockedFraudPaymentsCountRatio result: {}", blockedFraudPaymentsCountRatio);
        return ResponseEntity.ok(
                new RatioResponse()
                        .ratio(blockedFraudPaymentsCountRatio)
        );
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<SumResponse> getBlockedFraudPaymentsSum(
            @NotNull @Valid String fromTime,
            @NotNull @Valid String toTime,
            @NotNull @Valid String currency,
            @Valid String merchantId,
            @Valid String shopId) {
        Map<String, String> params = paramsConverter.convert(fromTime, toTime, currency, merchantId, shopId);
        log.info("-> getBlockedFraudPaymentsSum with params: {}", params);
        Integer blockedFraudPaymentsSum = baseAnalyticsService.getBlockedFraudPaymentsSum(params);
        log.info("<- getBlockedFraudPaymentsSum result: {}", blockedFraudPaymentsSum);
        return ResponseEntity.ok(
                new SumResponse()
                        .sum(blockedFraudPaymentsSum)
        );
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<CountResponse> getFraudPaymentsCount(
            @NotNull @Valid String fromTime,
            @NotNull @Valid String toTime,
            @NotNull @Valid String currency,
            @Valid String merchantId,
            @Valid String shopId) {
        Map<String, String> params = paramsConverter.convert(fromTime, toTime, currency, merchantId, shopId);
        log.info("-> getFraudPaymentsCount with params: {}", params);
        Integer fraudPaymentsCount = baseAnalyticsService.getFraudPaymentsCount(params);
        log.info("<- getFraudPaymentsCount result: {}", fraudPaymentsCount);
        return ResponseEntity.ok(
                new CountResponse()
                        .count(fraudPaymentsCount)
        );
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<FraudResultListSummaryResponse> getFraudPaymentsResultsSummary(
            @NotNull @Valid String fromTime,
            @NotNull @Valid String toTime,
            @NotNull @Valid String currency,
            @Valid String merchantId,
            @Valid String shopId) {
        Map<String, String> params = paramsConverter.convert(fromTime, toTime, currency, merchantId, shopId);
        log.info("-> getFraudPaymentsResultsSummary with params: {}", params);
        List<Map<String, String>> rows = baseAnalyticsService.getFraudPaymentsResultsSummary(params);
        List<FraudResultSummary> fraudResultSummaries = fraudResultSummaryListConverter.convert(rows);
        log.info("<- getFraudPaymentsResultsSummary result: {}", logList(fraudResultSummaries));
        return ResponseEntity.ok(
                new FraudResultListSummaryResponse()
                        .result(fraudResultSummaries)
        );
    }

    private <T> String logList(List<T> list) {
        if (list.isEmpty()) {
            return "{}";
        }
        return list.stream()
                .map(Objects::toString)
                .collect(Collectors.joining(",\n"));
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<SplitRiskScoreCountRatioResponse> getFraudPaymentsScoreSplitCountRatio(
            @NotNull @Valid String fromTime,
            @NotNull @Valid String toTime,
            @NotNull @Valid String currency,
            @NotNull @Valid String splitUnit,
            @Valid String merchantId,
            @Valid String shopId) {
        Map<String, String> params = paramsConverter.convert(fromTime, toTime, currency, merchantId, shopId);
        log.info("-> getFraudPaymentsScoreSplitCountRatio with params: {}", params);
        SplitUnit unit = SplitUnit.fromValue(splitUnit);
        List<Map<String, String>> rows = baseAnalyticsService.getFraudPaymentsScoreSplitCountRatio(params, unit);
        List<RiskScoreOffsetCountRatio> offsetCountRatios = countRatioListConverter.convert(rows, unit);
        log.info("<- getFraudPaymentsScoreSplitCountRatio result: {}", logList(offsetCountRatios));
        return ResponseEntity.ok(
                new SplitRiskScoreCountRatioResponse()
                        .splitUnit(unit)
                        .offsetCountRatios(offsetCountRatios)
        );
    }

    @Override
    public ResponseEntity<ListResponse> getCurrencies() {
        log.info("-> getCurrencies");
        List<String> currencies = baseAnalyticsService.getCurrencies();
        log.info("<- getCurrencies result: {}", logList(currencies));
        return ResponseEntity.ok(new ListResponse()
                .result(currencies));
    }
}
