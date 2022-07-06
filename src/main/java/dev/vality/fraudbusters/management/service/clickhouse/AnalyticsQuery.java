package dev.vality.fraudbusters.management.service.clickhouse;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnalyticsQuery {

    public static final String BLOCKED_FRAUD_PAYMENTS_COUNT =
            """
                        SELECT
                            countIf(status = 'failed' AND errorCode='no_route_found:risk_score_is_too_high') AS count
                        FROM fraud.fraud_payment
                        WHERE
                            timestamp >= toDate(:from)
                            AND timestamp <= toDate(:to)
                            AND toDateTime(eventTime) >= toDateTime(:from)
                            AND toDateTime(eventTime) <= toDateTime(:to)
                            AND currency = :currency
                            AND like(shopId, :shopId)
                            AND like(partyId, :partyId)
                    """;

    public static final String BLOCKED_FRAUD_PAYMENTS_COUNT_RATIO =
            """
               SELECT
                  countIf(status = 'failed' AND errorCode='no_route_found:risk_score_is_too_high') / count() AS ratio
               FROM fraud.fraud_payment
               WHERE
                  timestamp >= toDate(:from)
                  AND timestamp <= toDate(:to)
                  AND toDateTime(eventTime) >= toDateTime(:from)
                  AND toDateTime(eventTime) <= toDateTime(:to)
                  AND currency = :currency
                  AND like(shopId, :shopId)
                  AND like(partyId, :partyId)
            """;

    public static final String BLOCKED_FRAUD_PAYMENTS_SUM =
            """
                        SELECT
                            sum(amount) / 100 AS sum
                        FROM fraud.fraud_payment
                        WHERE
                            timestamp >= toDate(:from)
                            AND timestamp <= toDate(:to)
                            AND toDateTime(eventTime) >= toDateTime(:from)
                            AND toDateTime(eventTime) <= toDateTime(:to)
                            AND status = 'failed'
                            AND errorCode='no_route_found:risk_score_is_too_high'
                            AND currency = :currency
                            AND like(shopId, :shopId)
                            AND like(partyId, :partyId)
                    """;

    public static final String FRAUD_PAYMENTS_COUNT =
            """
                        SELECT
                            count(*) AS count
                        FROM fraud.fraud_payment
                        WHERE
                            timestamp >= toDate(:from)
                            AND timestamp <= toDate(:to)
                            AND toDateTime(eventTime) >= toDateTime(:from)
                            AND toDateTime(eventTime) <= toDateTime(:to)
                            AND currency = :currency
                            AND like(shopId, :shopId)
                            AND like(partyId, :partyId)
                    """;

    public static final String FRAUD_PAYMENTS_RESULTS_SUMMARY =
            """
                        SELECT
                            resultStatus AS status,
                            checkedRule AS rule,
                            checkedTemplate AS template,
                            count() AS count,
                            sum(amount / 100) AS sum,
                            count() /(SELECT
                                          count() AS all
                                      FROM fraud.fraud_payment
                                      WHERE
                                          timestamp >= toDate(:from)
                                          AND timestamp <= toDate(:to)
                                          AND toDateTime(eventTime) >= toDateTime(:from)
                                          AND toDateTime(eventTime) <= toDateTime(:to)
                                          AND shopId != 'TEST') AS ratio
                        FROM fraud.fraud_payment
                        WHERE
                            timestamp >= toDate(:from)
                            AND timestamp <= toDate(:to)
                            AND toDateTime(eventTime) >= toDateTime(:from)
                            AND toDateTime(eventTime) <= toDateTime(:to)
                            AND currency = :currency
                            AND like(shopId, :shopId)
                            AND like(partyId, :partyId)
                    """;

    public static final String FRAUD_PAYMENTS_SCORE_SPLIT_COUNT_RATIO =
            """
                        SELECT
                            %1$s,
                            low * 100 / all AS low,
                            normal * 100 / all AS high,
                            fatal * 100 / all AS fatal
                        FROM
                        (
                            SELECT
                                %1$s,
                                countIf(resultStatus = 'ACCEPT') AS low,
                                countIf(resultStatus = 'THREE_DS') AS normal,
                                countIf(resultStatus = 'DECLINE') AS fatal,
                                count() as all
                            FROM fraud.fraud_payment
                            WHERE
                                timestamp >= toDate(:from)
                                AND timestamp <= toDate(:to)
                                AND toDateTime(eventTime) >= toDateTime(:from)
                                AND toDateTime(eventTime) <= toDateTime(:to)
                                and shopId!='TEST'
                                AND currency = :currency
                                AND like(shopId, :shopId)
                                AND like(partyId, :partyId)
                            GROUP BY %1$s
                            ORDER BY %1$s
                        )
                    """;

    public static final String CURRENCIES =
            """
                        SELECT DISTINCT
                            currency
                        FROM fraud.fraud_payment
                    """;
}
