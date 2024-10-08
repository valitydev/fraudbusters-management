package dev.vality.fraudbusters.management.dao.payment.wblist;

import dev.vality.mapper.RecordRowMapper;
import dev.vality.fraudbusters.management.dao.AbstractDao;
import dev.vality.fraudbusters.management.dao.condition.ConditionParameterSource;
import dev.vality.fraudbusters.management.domain.enums.ListType;
import dev.vality.fraudbusters.management.domain.request.FilterRequest;
import dev.vality.fraudbusters.management.domain.tables.pojos.WbListRecords;
import dev.vality.fraudbusters.management.domain.tables.records.WbListRecordsRecord;
import lombok.extern.slf4j.Slf4j;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;

import static dev.vality.fraudbusters.management.domain.tables.WbListRecords.WB_LIST_RECORDS;
import static org.jooq.Comparator.EQUALS;

@Slf4j
@Component
public class WbListDaoImpl extends AbstractDao implements WbListDao {

    private static final int LIMIT_TOTAL = 100;
    public static final String EMPTY = "";
    private final RowMapper<WbListRecords> listRecordRowMapper;

    public WbListDaoImpl(DataSource dataSource) {
        super(dataSource);
        listRecordRowMapper = new RecordRowMapper<>(WB_LIST_RECORDS, WbListRecords.class);
    }

    @Override
    public void saveListRecord(WbListRecords listRecord) {
        log.info("WbListDaoImpl saveListRecord listRecord: {}", listRecord);
        Query query = getDslContext()
                .insertInto(WB_LIST_RECORDS)
                .set(getDslContext().newRecord(WB_LIST_RECORDS, listRecord))
                .onConflict(WB_LIST_RECORDS.PARTY_ID, WB_LIST_RECORDS.SHOP_ID, WB_LIST_RECORDS.LIST_TYPE,
                        WB_LIST_RECORDS.LIST_NAME, WB_LIST_RECORDS.VALUE)
                .doNothing();
        execute(query);
    }

    @Override
    public void removeRecord(WbListRecords listRecord) {
        log.info("WbListDaoImpl removeRecord listRecord: {}", listRecord);
        DeleteConditionStep<WbListRecordsRecord> where = getDslContext()
                .delete(WB_LIST_RECORDS)
                .where(isNullOrValueCondition(WB_LIST_RECORDS.PARTY_ID, listRecord.getPartyId())
                        .and(isNullOrValueCondition(WB_LIST_RECORDS.SHOP_ID, listRecord.getShopId())
                                .and(WB_LIST_RECORDS.LIST_TYPE.eq(listRecord.getListType()))
                                .and(WB_LIST_RECORDS.LIST_NAME.eq(listRecord.getListName()))
                                .and(WB_LIST_RECORDS.VALUE.eq(listRecord.getValue()))));
        execute(where);
    }

    private Condition isNullOrValueCondition(TableField<WbListRecordsRecord, String> key, String value) {
        return value == null ? key.isNull().or(key.eq(EMPTY)) : key.eq(value);
    }

    @Override
    public WbListRecords getById(String id) {
        log.info("WbListDaoImpl getById id: {}", id);
        SelectConditionStep<Record8<String, String, String, ListType, String, String, LocalDateTime, LocalDateTime>>
                query =
                getDslContext()
                        .select(WB_LIST_RECORDS.ID,
                                WB_LIST_RECORDS.PARTY_ID,
                                WB_LIST_RECORDS.SHOP_ID,
                                WB_LIST_RECORDS.LIST_TYPE,
                                WB_LIST_RECORDS.LIST_NAME,
                                WB_LIST_RECORDS.VALUE,
                                WB_LIST_RECORDS.INSERT_TIME,
                                WB_LIST_RECORDS.TIME_TO_LIVE)
                        .from(WB_LIST_RECORDS)
                        .where(WB_LIST_RECORDS.ID.eq(id));
        return fetchOne(query, listRecordRowMapper);
    }

    @Override
    public List<WbListRecords> getFilteredListRecords(String partyId, String shopId, ListType listType,
                                                      String listName) {
        log.info("WbListDaoImpl getFilteredListRecords partyId: {} shopId: {} listType: {} listName: {}", partyId,
                shopId, listType, listName);
        Condition condition = DSL.trueCondition();
        SelectLimitPercentStep
                <Record9<String, String, String, ListType, String, String, LocalDateTime, LocalDateTime, String>>
                query =
                getDslContext()
                        .select(WB_LIST_RECORDS.ID,
                                WB_LIST_RECORDS.PARTY_ID,
                                WB_LIST_RECORDS.SHOP_ID,
                                WB_LIST_RECORDS.LIST_TYPE,
                                WB_LIST_RECORDS.LIST_NAME,
                                WB_LIST_RECORDS.VALUE,
                                WB_LIST_RECORDS.INSERT_TIME,
                                WB_LIST_RECORDS.TIME_TO_LIVE,
                                WB_LIST_RECORDS.ROW_INFO)
                        .from(WB_LIST_RECORDS)
                        .where(appendConditions(condition, Operator.AND,
                                new ConditionParameterSource()
                                        .addValue(WB_LIST_RECORDS.PARTY_ID, partyId, EQUALS)
                                        .addValue(WB_LIST_RECORDS.SHOP_ID, shopId, EQUALS)
                                        .addValue(WB_LIST_RECORDS.LIST_TYPE, listType, EQUALS)
                                        .addValue(WB_LIST_RECORDS.LIST_NAME, listName, EQUALS)))
                        .limit(LIMIT_TOTAL);
        return fetch(query, listRecordRowMapper);
    }

    @Override
    public List<WbListRecords> filterListRecords(@NonNull ListType listType,
                                                 @NonNull List<String> listNames,
                                                 FilterRequest filterRequest) {
        SelectWhereStep<WbListRecordsRecord> from = getDslContext()
                .selectFrom(WB_LIST_RECORDS);
        Condition condition = WB_LIST_RECORDS.LIST_NAME.in(listNames).and(WB_LIST_RECORDS.LIST_TYPE.eq(listType));
        SelectConditionStep<WbListRecordsRecord> whereQuery = StringUtils.hasLength(filterRequest.getSearchValue())
                ? from.where(condition.and(
                WB_LIST_RECORDS.VALUE.like(filterRequest.getSearchValue())
                        .or(WB_LIST_RECORDS.PARTY_ID.like(filterRequest.getSearchValue())
                                .or(WB_LIST_RECORDS.SHOP_ID.like(filterRequest.getSearchValue())))))
                : from.where(condition);
        Field field = StringUtils.hasLength(filterRequest.getSortBy())
                ? WB_LIST_RECORDS.field(filterRequest.getSortBy())
                : WB_LIST_RECORDS.INSERT_TIME;
        SelectSeekStep2<WbListRecordsRecord, Object, String> wbListRecordsRecords = addSortCondition(WB_LIST_RECORDS.ID,
                field, filterRequest.getSortOrder(), whereQuery);
        return fetch(
                addSeekIfNeed(
                        filterRequest.getLastId(),
                        filterRequest.getSortFieldValue(),
                        filterRequest.getSize(),
                        wbListRecordsRecords),
                listRecordRowMapper
        );
    }

    @Override
    public Integer countFilterRecords(@NonNull ListType listType, @NonNull List<String> listNames, String filterValue) {
        SelectJoinStep<Record1<Integer>> from = getDslContext()
                .selectCount()
                .from(WB_LIST_RECORDS);
        Condition condition = WB_LIST_RECORDS.LIST_NAME.in(listNames).and(WB_LIST_RECORDS.LIST_TYPE.eq(listType));
        SelectConditionStep<Record1<Integer>> where = StringUtils.hasLength(filterValue)
                ? from.where(condition.and(
                WB_LIST_RECORDS.VALUE.like(filterValue)
                        .or(WB_LIST_RECORDS.PARTY_ID.like(filterValue)
                                .or(WB_LIST_RECORDS.SHOP_ID.like(filterValue)))))
                : from.where(condition);
        return fetchOne(where, Integer.class);
    }

    @Override
    public List<String> getCurrentListNames(ListType listType) {
        SelectConditionStep<Record1<String>> where = getDslContext()
                .selectDistinct(WB_LIST_RECORDS.LIST_NAME)
                .from(WB_LIST_RECORDS)
                .where(WB_LIST_RECORDS.LIST_TYPE.eq(listType));
        return fetch(where, (rs, rowNum) ->
                rs.getString(WB_LIST_RECORDS.LIST_NAME.getName())
        );
    }

    @Override
    public List<WbListRecords> getRottenRecords(LocalDateTime thresholdRotDate) {
        log.info("WbListDaoImpl getRottenRecords older than {}: ", thresholdRotDate);
        SelectConditionStep<Record8<String, String, String, ListType, String, String, LocalDateTime, LocalDateTime>>
                query =
                getDslContext()
                        .select(WB_LIST_RECORDS.ID,
                                WB_LIST_RECORDS.PARTY_ID,
                                WB_LIST_RECORDS.SHOP_ID,
                                WB_LIST_RECORDS.LIST_TYPE,
                                WB_LIST_RECORDS.LIST_NAME,
                                WB_LIST_RECORDS.VALUE,
                                WB_LIST_RECORDS.INSERT_TIME,
                                WB_LIST_RECORDS.TIME_TO_LIVE)
                        .from(WB_LIST_RECORDS)
                        .where(WB_LIST_RECORDS.TIME_TO_LIVE.lessOrEqual(thresholdRotDate));
        return fetch(query, listRecordRowMapper);
    }
}
