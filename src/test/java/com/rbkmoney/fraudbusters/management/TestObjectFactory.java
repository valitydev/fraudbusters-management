package com.rbkmoney.fraudbusters.management;

import com.rbkmoney.fraudbusters.management.domain.payment.PaymentCountInfo;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentListRecord;
import com.rbkmoney.fraudbusters.management.domain.payment.request.ListRowsInsertRequest;
import dev.vality.damsel.fraudbusters_notificator.ChannelType;
import dev.vality.damsel.fraudbusters_notificator.NotificationStatus;
import dev.vality.damsel.wb_list.*;
import dev.vality.fraudbusters.management.domain.tables.pojos.WbListRecords;
import dev.vality.fraudbusters.management.domain.tables.records.WbListRecordsRecord;
import dev.vality.swag.fraudbusters.management.model.Channel;
import dev.vality.swag.fraudbusters.management.model.Notification;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        row.setListType(ListType.grey);
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

    public static WbListRecordsRecord createWbListRecordsRecord(String id) {
        WbListRecordsRecord listRecord = new WbListRecordsRecord();
        listRecord.setId(id);
        listRecord.setListName(randomString());
        listRecord.setListType(dev.vality.fraudbusters.management.domain.enums.ListType.black);
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
        listRecord.setListType(dev.vality.fraudbusters.management.domain.enums.ListType.black);
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
        return ThreadLocalRandom.current().nextLong(1000);
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
        insertRequest.setListType(ListType.black);
        insertRequest.setRecords(collect);
        return insertRequest;
    }

}
