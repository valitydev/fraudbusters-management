package dev.vality.fraudbusters.management.dao.payment.wblist;

import dev.vality.fraudbusters.management.config.PostgresqlJooqITest;
import dev.vality.fraudbusters.management.converter.ListRecordToRowConverterImpl;
import dev.vality.fraudbusters.management.converter.payment.PaymentCountInfoRequestToRowConverter;
import dev.vality.fraudbusters.management.converter.payment.PaymentListRecordToRowConverter;
import dev.vality.fraudbusters.management.converter.payment.WbListRecordsToCountInfoListRequestConverter;
import dev.vality.fraudbusters.management.domain.enums.ListType;
import dev.vality.fraudbusters.management.domain.request.FilterRequest;
import dev.vality.fraudbusters.management.domain.tables.pojos.WbListRecords;
import dev.vality.fraudbusters.management.domain.tables.records.WbListRecordsRecord;
import dev.vality.fraudbusters.management.utils.CountInfoApiUtils;
import dev.vality.fraudbusters.management.utils.CountInfoUtils;
import dev.vality.fraudbusters.management.utils.PaymentCountInfoGenerator;
import dev.vality.swag.fraudbusters.management.model.PaymentCountInfo;
import org.jooq.DSLContext;
import org.jooq.SortOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static dev.vality.fraudbusters.management.TestObjectFactory.createWbListRecordsRecord;
import static dev.vality.fraudbusters.management.TestObjectFactory.randomString;
import static dev.vality.fraudbusters.management.domain.tables.WbListRecords.WB_LIST_RECORDS;
import static org.junit.jupiter.api.Assertions.*;

@PostgresqlJooqITest
@ContextConfiguration(classes = {WbListDaoImpl.class, WbListRecordsToCountInfoListRequestConverter.class,
        PaymentListRecordToRowConverter.class, PaymentCountInfoRequestToRowConverter.class,
        ListRecordToRowConverterImpl.class, CountInfoApiUtils.class,
        PaymentCountInfoGenerator.class, JacksonAutoConfiguration.class, CountInfoUtils.class})
public class WbListDaoImplTest {

    public static final String PARTY = "party";
    public static final String SHOP = "shop";
    public static final String LIST_NAME = "ip";
    @Autowired
    WbListDao wbListDao;

    @Autowired
    DSLContext dslContext;

    @Autowired
    WbListRecordsToCountInfoListRequestConverter wbListRecordsToListRecordWithRowConverter;

    @BeforeEach
    void setUp() {
        dslContext.deleteFrom(WB_LIST_RECORDS).execute();
    }

    @Test
    void saveListRecord() {
        String id = "id";
        WbListRecords listRecord = createListRecord(id);

        wbListDao.saveListRecord(listRecord);
        WbListRecords byId = wbListDao.getById(id);
        assertEquals(listRecord.getListType(), byId.getListType());
        assertEquals(listRecord.getListName(), byId.getListName());
        assertEquals(listRecord.getValue(), byId.getValue());
        assertEquals(listRecord.getId(), byId.getId());

        wbListDao.removeRecord(listRecord);
        byId = wbListDao.getById(id);
        assertNull(byId);

        //test null party and shop
        listRecord.setPartyId(null);
        listRecord.setShopId(null);
        wbListDao.saveListRecord(listRecord);
        byId = wbListDao.getById(id);
        assertEquals(listRecord.getListType(), byId.getListType());
        assertEquals(listRecord.getListName(), byId.getListName());
        assertEquals(listRecord.getValue(), byId.getValue());
        assertEquals(listRecord.getId(), byId.getId());

        wbListDao.removeRecord(listRecord);
        byId = wbListDao.getById(id);
        assertNull(byId);

        //test empty party and shop
        listRecord.setPartyId("");
        listRecord.setShopId("");
        wbListDao.saveListRecord(listRecord);
        byId = wbListDao.getById(id);
        assertEquals(listRecord.getListType(), byId.getListType());
        assertEquals(listRecord.getListName(), byId.getListName());
        assertEquals(listRecord.getValue(), byId.getValue());
        assertEquals(listRecord.getId(), byId.getId());

        listRecord.setPartyId(null);
        listRecord.setShopId(null);
        wbListDao.removeRecord(listRecord);
        byId = wbListDao.getById(id);
        assertNull(byId);
    }

    @Test
    void saveEmptyPartyListRecord() {
        String id = "id";
        WbListRecords listRecord = createListRecord(id);
        listRecord.setPartyId(null);
        listRecord.setShopId(null);

        wbListDao.saveListRecord(listRecord);
        WbListRecords byId = wbListDao.getById(id);
        assertEquals(listRecord.getListType(), byId.getListType());
        assertEquals(listRecord.getListName(), byId.getListName());
        assertEquals(listRecord.getValue(), byId.getValue());
        assertEquals(listRecord.getId(), byId.getId());

        wbListDao.removeRecord(listRecord);
        byId = wbListDao.getById(id);
        assertNull(byId);
    }

    private WbListRecords createListRecord(String id) {
        WbListRecords listRecord = new WbListRecords();
        listRecord.setId(id);
        listRecord.setListName(LIST_NAME);
        listRecord.setListType(ListType.black);
        listRecord.setInsertTime(LocalDateTime.now());
        listRecord.setPartyId(PARTY);
        listRecord.setShopId(SHOP);
        listRecord.setValue("192.168.1.1");
        return listRecord;
    }

    @Test
    void getFilteredListRecords() {
        String firstId = "1";
        WbListRecords listRecord = createListRecord(firstId);
        String secondId = "2";
        WbListRecords listRecord2 = createListRecord(secondId);
        listRecord2.setPartyId("party_2");
        WbListRecords listRecord3 = createListRecord("3");

        wbListDao.saveListRecord(listRecord);
        wbListDao.saveListRecord(listRecord2);
        wbListDao.saveListRecord(listRecord3);

        List<WbListRecords> filteredListRecords =
                wbListDao.getFilteredListRecords(PARTY, SHOP, ListType.black, LIST_NAME);

        assertEquals(1, filteredListRecords.size());

        filteredListRecords = wbListDao.getFilteredListRecords(null, SHOP, ListType.black, null);

        assertEquals(2, filteredListRecords.size());

        WbListRecords listRecord4 = createListRecord("4");
        listRecord4.setRowInfo("{ \n" +
                "  \"count\":5, \n" +
                "  \"time_to_live\":\"2019-08-22T13:14:17.443332Z\",\n" +
                "  \"start_count_time\": \"2019-08-22T11:14:17.443332Z\"\n" +
                "}");
        listRecord4.setListType(ListType.grey);
        wbListDao.saveListRecord(listRecord4);

        filteredListRecords = wbListDao.getFilteredListRecords(null, SHOP, ListType.grey, null);
        assertEquals(1, filteredListRecords.size());
        assertFalse(filteredListRecords.get(0).getRowInfo().isEmpty());


        PaymentCountInfo paymentCountInfo =
                wbListRecordsToListRecordWithRowConverter.convert(filteredListRecords.get(0));

        assertEquals(5L, paymentCountInfo.getCountInfo().getCount().longValue());


        //check sorting
        List<WbListRecords> wbListRecordsFirst = wbListDao.filterListRecords(ListType.black, List.of(LIST_NAME),
                FilterRequest.builder()
                        .size(3)
                        .sortOrder(SortOrder.ASC)
                        .build());
        List<WbListRecords> wbListRecordsSecond =
                wbListDao.filterListRecords(ListType.black, List.of(LIST_NAME), FilterRequest.builder()
                        .size(3)
                        .sortOrder(SortOrder.DESC)
                        .build());
        assertEquals(wbListRecordsFirst.get(0).getPartyId(), wbListRecordsSecond.get(1).getPartyId());

        //check paging
        wbListRecordsFirst = wbListDao.filterListRecords(ListType.black, List.of(LIST_NAME), FilterRequest.builder()
                .size(1)
                .sortOrder(SortOrder.ASC)
                .build());
        wbListRecordsSecond = wbListDao.filterListRecords(ListType.black, List.of(LIST_NAME), FilterRequest.builder()
                .lastId(wbListRecordsFirst.get(0).getId())
                .sortFieldValue(wbListRecordsFirst.get(0).getInsertTime().toString())
                .size(3)
                .sortOrder(SortOrder.ASC)
                .build());
        Integer count = wbListDao.countFilterRecords(ListType.black, List.of(LIST_NAME), null);
        assertEquals(Integer.valueOf(2), count);
        assertNotEquals(wbListRecordsFirst.get(0).getPartyId(), wbListRecordsSecond.get(0).getPartyId());

        List<String> currentListNames = wbListDao.getCurrentListNames(ListType.black);
        assertEquals(LIST_NAME, currentListNames.get(0));
    }

    @Test
    void shouldGetNothingWithNotExistRecords() {
        List<WbListRecords> rottenRecords = wbListDao.getRottenRecords(LocalDateTime.now());
        assertTrue(rottenRecords.isEmpty());
    }

    @Test
    void shouldGetNothingRottenRecords() {
        WbListRecordsRecord freshRecord1 = createWbListRecordsRecord(randomString());
        freshRecord1.setTimeToLive(LocalDateTime.now().plusDays(1));
        freshRecord1.setValue(randomString());
        WbListRecordsRecord freshRecord2 = createWbListRecordsRecord(randomString());
        freshRecord2.setTimeToLive(LocalDateTime.now().plusDays(2));
        freshRecord2.setValue(randomString());
        dslContext.insertInto(WB_LIST_RECORDS)
                .set(freshRecord1)
                .newRecord()
                .set(freshRecord2)
                .execute();
        assertEquals(2, dslContext.fetchCount(WB_LIST_RECORDS));
        List<WbListRecords> rottenRecords = wbListDao.getRottenRecords(LocalDateTime.now());
        assertTrue(rottenRecords.isEmpty());
    }

    @Test
    void shouldGetRottenRecords() {
        WbListRecordsRecord rotRecord1 = createWbListRecordsRecord(randomString());
        rotRecord1.setTimeToLive(LocalDateTime.now().minusDays(1));
        rotRecord1.setValue(randomString());
        WbListRecordsRecord rotRecord2 = createWbListRecordsRecord(randomString());
        rotRecord2.setTimeToLive(LocalDateTime.now().minusDays(2));
        rotRecord2.setValue(randomString());
        WbListRecordsRecord freshRecord1 = createWbListRecordsRecord(randomString());
        freshRecord1.setTimeToLive(LocalDateTime.now().plusDays(1));
        freshRecord1.setValue(randomString());
        WbListRecordsRecord freshRecord2 = createWbListRecordsRecord(randomString());
        freshRecord2.setTimeToLive(LocalDateTime.now().plusDays(2));
        freshRecord2.setValue(randomString());
        dslContext.insertInto(WB_LIST_RECORDS)
                .set(rotRecord1)
                .newRecord()
                .set(rotRecord2)
                .newRecord()
                .set(freshRecord1)
                .newRecord()
                .set(freshRecord2)
                .execute();
        assertEquals(4, dslContext.fetchCount(WB_LIST_RECORDS));

        List<WbListRecords> rottenRecords = wbListDao.getRottenRecords(LocalDateTime.now());

        List<String> ids = rottenRecords.stream().map(WbListRecords::getId).collect(Collectors.toList());
        assertTrue(ids.containsAll(List.of(rotRecord1.getId(), rotRecord2.getId())));
    }
}
