package dev.vality.fraudbusters.management;

import dev.vality.damsel.fraudbusters_notificator.ChannelType;
import dev.vality.damsel.fraudbusters_notificator.NotificationStatus;
import dev.vality.damsel.wb_list.*;
import dev.vality.fraud_data_crawler.FraudDataCandidate;
import dev.vality.fraudbusters.management.domain.enums.ListType;
import dev.vality.fraudbusters.management.domain.payment.PaymentCountInfo;
import dev.vality.fraudbusters.management.domain.payment.PaymentListRecord;
import dev.vality.fraudbusters.management.domain.payment.request.ListRowsInsertRequest;
import dev.vality.fraudbusters.management.domain.tables.pojos.WbListCandidate;
import dev.vality.fraudbusters.management.domain.tables.pojos.WbListRecords;
import dev.vality.fraudbusters.management.domain.tables.records.WbListCandidateRecord;
import dev.vality.fraudbusters.management.domain.tables.records.WbListRecordsRecord;
import dev.vality.swag.fraudbusters.management.model.Channel;
import dev.vality.swag.fraudbusters.management.model.FraudCandidate;
import dev.vality.swag.fraudbusters.management.model.Notification;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static dev.vality.fraudbusters.management.constant.AnalyticsResultField.*;
import static dev.vality.swag.fraudbusters.management.model.SplitUnit.*;

public abstract class TestObjectFactory {

    public static Row buildRow() {
        PaymentId paymentId = new PaymentId();
        paymentId.setPartyId(randomString());
        paymentId.setShopId(randomString());
        IdInfo idInfo = new IdInfo();
        idInfo.setPaymentId(paymentId);
        Row row = new Row();
        row.setId(idInfo);
        row.setListName(randomString());
        row.setValue(randomString());
        row.setListType(dev.vality.damsel.wb_list.ListType.grey);
        CountInfo countInfo = new CountInfo();
        countInfo.setTimeToLive(Instant.now().toString());
        RowInfo rowInfo = new RowInfo();
        rowInfo.setCountInfo(countInfo);
        row.setRowInfo(rowInfo);
        return row;
    }

    public static String randomString() {
        return UUID.randomUUID().toString();
    }

    public static List<String> listRandomStrings(int i) {
        return IntStream.rangeClosed(1, i)
                .mapToObj(value -> randomString())
                .collect(Collectors.toList());
    }

    public static WbListRecordsRecord createWbListRecordsRecord(String id) {
        WbListRecordsRecord listRecord = new WbListRecordsRecord();
        listRecord.setId(id);
        listRecord.setListName(randomString());
        listRecord.setListType(ListType.black);
        listRecord.setInsertTime(LocalDateTime.now());
        listRecord.setPartyId(randomString());
        listRecord.setShopId(randomString());
        listRecord.setValue("192.168.1.1");
        return listRecord;
    }

    public static WbListRecords createWbListRecords(String id) {
        WbListRecords listRecord = new WbListRecords();
        listRecord.setId(id);
        listRecord.setListName(randomString());
        listRecord.setListType(ListType.black);
        listRecord.setInsertTime(LocalDateTime.now());
        listRecord.setPartyId(randomString());
        listRecord.setShopId(randomString());
        listRecord.setValue("192.168.1.1");
        return listRecord;
    }

    public static Channel testChannel() {
        Channel channel = new Channel();
        channel.setType(Channel.TypeEnum.MAIL);
        channel.setDestination(randomString());
        channel.setName(randomString());
        channel.setCreatedAt(LocalDateTime.now());
        return channel;
    }

    public static Notification testNotification() {
        Notification notification = new Notification();
        notification.setName(randomString());
        notification.setSubject(randomString());
        notification.setStatus(Notification.StatusEnum.ACTIVE);
        notification.setChannel(randomString());
        notification.setPeriod(randomString());
        notification.setFrequency(randomString());
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUpdatedAt(LocalDateTime.now());
        notification.setTemplateId(randomLong());
        return notification;
    }

    public static Long randomLong() {
        return ThreadLocalRandom.current().nextLong(200);
    }

    public static Float randomFloat() {
        return ThreadLocalRandom.current().nextFloat();
    }

    public static dev.vality.damsel.fraudbusters_notificator.Notification testInternalNotification() {
        var notification = new dev.vality.damsel.fraudbusters_notificator.Notification();
        notification.setId(randomLong());
        notification.setName(randomString());
        notification.setSubject(randomString());
        notification.setStatus(NotificationStatus.ACTIVE);
        notification.setChannel(randomString());
        notification.setPeriod(randomString());
        notification.setFrequency(randomString());
        notification.setCreatedAt(LocalDateTime.now().toString());
        notification.setUpdatedAt(LocalDateTime.now().toString());
        notification.setTemplateId(randomLong());
        return notification;
    }

    public static List<dev.vality.damsel.fraudbusters_notificator.Notification> testInternalNotifications(int i) {
        return IntStream.rangeClosed(1, i)
                .mapToObj(value -> testInternalNotification())
                .collect(Collectors.toList());
    }

    public static dev.vality.damsel.fraudbusters_notificator.Channel testInternalChannel() {
        var channel = new dev.vality.damsel.fraudbusters_notificator.Channel();
        channel.setType(ChannelType.mail);
        channel.setDestination(randomString());
        channel.setName(randomString());
        channel.setCreatedAt(LocalDateTime.now().toString());
        return channel;
    }

    public static List<dev.vality.damsel.fraudbusters_notificator.Channel> testInternalChannels(int i) {
        return IntStream.rangeClosed(1, i)
                .mapToObj(value -> testInternalChannel())
                .collect(Collectors.toList());
    }

    public static dev.vality.damsel.fraudbusters_notificator.NotificationTemplate testNotificationTemplate() {
        var notificationTemplate = new dev.vality.damsel.fraudbusters_notificator.NotificationTemplate();
        notificationTemplate.setId(randomLong());
        notificationTemplate.setName(randomString());
        notificationTemplate.setBasicParams(randomString());
        notificationTemplate.setQueryText(randomString());
        notificationTemplate.setType(randomString());
        notificationTemplate.setSkeleton(randomString());
        notificationTemplate.setCreatedAt(LocalDateTime.now().toString());
        notificationTemplate.setUpdatedAt(LocalDateTime.now().toString());
        return notificationTemplate;
    }

    public static List<dev.vality.damsel.fraudbusters_notificator.NotificationTemplate> testNotificationTemplates(
            int i) {
        return IntStream.rangeClosed(1, i)
                .mapToObj(value -> testNotificationTemplate())
                .collect(Collectors.toList());
    }

    public static ListRowsInsertRequest testListRowsInsertRequest(PaymentListRecord... values) {
        List<PaymentCountInfo> collect = List.of(values).stream()
                .map(paymentListRecord -> {
                    PaymentCountInfo paymentCountInfo = new PaymentCountInfo();
                    paymentCountInfo.setListRecord(paymentListRecord);
                    return paymentCountInfo;
                })
                .collect(Collectors.toList());
        ListRowsInsertRequest insertRequest = new ListRowsInsertRequest();
        insertRequest.setListType(dev.vality.damsel.wb_list.ListType.black);
        insertRequest.setRecords(collect);
        return insertRequest;
    }

    public static dev.vality.fraudbusters.warehouse.Result testResult(String nameField, Number value) {
        var result = new dev.vality.fraudbusters.warehouse.Result();
        result.setValues(
                List.of(testRow(Map.of(nameField, value.toString()))));
        return result;
    }

    public static dev.vality.fraudbusters.warehouse.Result testResult(
            List<dev.vality.fraudbusters.warehouse.Row> rows) {
        var result = new dev.vality.fraudbusters.warehouse.Result();
        result.setValues(rows);
        return result;
    }

    public static dev.vality.fraudbusters.warehouse.Row testRow(Map<String, String> fields) {
        return new dev.vality.fraudbusters.warehouse.Row()
                .setValues(fields);
    }

    public static Map<String, String> testSummaryRowFieldsMap() {
        return Map.of(
                TEMPLATE, TestObjectFactory.randomString(),
                RULE, TestObjectFactory.randomString(),
                STATUS, TestObjectFactory.randomString(),
                COUNT, TestObjectFactory.randomLong().toString(),
                SUM, TestObjectFactory.randomFloat().toString(),
                RATIO, TestObjectFactory.randomFloat().toString()
        );
    }

    public static Map<String, String> testRiskScoreOffsetCountRatioByDayRowFieldsMap() {
        return Map.of(
                LOW_SCORE, TestObjectFactory.randomFloat().toString(),
                HIGH_SCORE, TestObjectFactory.randomFloat().toString(),
                FATAL_SCORE, TestObjectFactory.randomFloat().toString(),
                DAY.getValue(), LocalDate.now().plusDays(randomLong()).toString()
        );
    }

    public static Map<String, String> testRiskScoreOffsetCountRatioByMonthRowFieldsMap(int monthOffset) {
        return Map.of(
                LOW_SCORE, TestObjectFactory.randomFloat().toString(),
                HIGH_SCORE, TestObjectFactory.randomFloat().toString(),
                FATAL_SCORE, TestObjectFactory.randomFloat().toString(),
                YEAR.getValue(), String.valueOf(LocalDate.now().getYear()),
                MONTH.getValue(), String.valueOf(LocalDate.now().getMonth().getValue() - monthOffset)
        );
    }

    public static WbListCandidate testWbListCandidate() {
        WbListCandidate wbListCandidate = new WbListCandidate();
        wbListCandidate.setListName(FraudCandidate.TypeEnum.BIN.getValue());
        wbListCandidate.setListType(ListType.black);
        wbListCandidate.setValue(randomString());
        wbListCandidate.setSource(randomString());
        wbListCandidate.setApproved(Boolean.FALSE);
        wbListCandidate.setShopId(randomString());
        wbListCandidate.setPartyId(randomString());
        wbListCandidate.setUpdateTime(LocalDateTime.now());
        return wbListCandidate;
    }

    public static List<WbListCandidate> testWbListCandidates(int i) {
        return IntStream.rangeClosed(1, i)
                .mapToObj(value -> testWbListCandidate())
                .collect(Collectors.toList());
    }

    public static WbListCandidateRecord testWbListCandidateRecord() {
        WbListCandidateRecord record = new WbListCandidateRecord();
        record.setListType(ListType.black);
        record.setListName(randomString());
        record.setApproved(Boolean.FALSE);
        record.setSource(randomString());
        record.setValue(randomString());
        return record;
    }

    public static FraudCandidate testFraudCandidate() {
        FraudCandidate fraudCandidate = new FraudCandidate();
        fraudCandidate.setType(FraudCandidate.TypeEnum.IP);
        fraudCandidate.setList(dev.vality.swag.fraudbusters.management.model.ListType.BLACK);
        fraudCandidate.setValue(randomString());
        fraudCandidate.setSource(randomString());
        return fraudCandidate;
    }

    public static List<FraudCandidate> testFraudCandidates(int i) {
        return IntStream.rangeClosed(1, i)
                .mapToObj(value -> testFraudCandidate())
                .collect(Collectors.toList());
    }

    public static FraudDataCandidate testFraudDataCandidate() {
        FraudDataCandidate fraudDataCandidate = new FraudDataCandidate();
        fraudDataCandidate.setSource(randomString());
        fraudDataCandidate.setType(randomString());
        fraudDataCandidate.setListType(dev.vality.fraud_data_crawler.ListType.black);
        fraudDataCandidate.setValue(randomString());
        fraudDataCandidate.setShopId(randomString());
        fraudDataCandidate.setMerchantId(randomString());
        return fraudDataCandidate;
    }

    public static List<FraudDataCandidate> testFraudDataCandidates(int i) {
        return IntStream.rangeClosed(1, i)
                .mapToObj(value -> testFraudDataCandidate())
                .collect(Collectors.toList());
    }
}
