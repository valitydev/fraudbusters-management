package dev.vality.fraudbusters.management.dao.payment.candidate;

import dev.vality.fraudbusters.management.TestObjectFactory;
import dev.vality.fraudbusters.management.config.PostgresqlJooqITest;
import dev.vality.fraudbusters.management.domain.request.FilterRequest;
import dev.vality.fraudbusters.management.domain.tables.pojos.WbListCandidate;
import dev.vality.fraudbusters.management.domain.tables.records.WbListCandidateRecord;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static dev.vality.fraudbusters.management.domain.tables.WbListCandidate.WB_LIST_CANDIDATE;
import static org.junit.jupiter.api.Assertions.*;

@PostgresqlJooqITest
@ContextConfiguration(classes = {WbListCandidateDaoImpl.class})
class WbListCandidateDaoImplTest {

    @Autowired
    WbListCandidateDao wbListCandidateDao;

    @Autowired
    DSLContext dslContext;

    @BeforeEach
    void setUp() {
        dslContext.deleteFrom(WB_LIST_CANDIDATE).execute();
    }

    @Test
    void save() {
        WbListCandidate wbListCandidate = TestObjectFactory.testWbListCandidate();

        wbListCandidateDao.save(wbListCandidate);

        assertEquals(1, dslContext.fetchCount(WB_LIST_CANDIDATE));
    }

    @Test
    void getListWithoutFilter() {
        WbListCandidateRecord record1 = TestObjectFactory.testWbListCandidateRecord();
        WbListCandidateRecord record2 = TestObjectFactory.testWbListCandidateRecord();

        dslContext.insertInto(WB_LIST_CANDIDATE)
                .set(record1)
                .newRecord()
                .set(record2)
                .execute();

        List<WbListCandidate> candidates = wbListCandidateDao.getList(new FilterRequest());

        assertEquals(2, candidates.size());
        assertTrue(candidates.stream().map(WbListCandidate::getListName).anyMatch(s -> s.equals(record1.getListName())));
        assertTrue(candidates.stream().map(WbListCandidate::getListName).anyMatch(s -> s.equals(record2.getListName())));
    }

    @Test
    void getListWithSearchValueFilter() {
        WbListCandidateRecord record1 = TestObjectFactory.testWbListCandidateRecord();
        WbListCandidateRecord record2 = TestObjectFactory.testWbListCandidateRecord();

        dslContext.insertInto(WB_LIST_CANDIDATE)
                .set(record1)
                .newRecord()
                .set(record2)
                .execute();

        FilterRequest filter = new FilterRequest();
        filter.setSearchValue(record1.getValue());
        List<WbListCandidate> candidates = wbListCandidateDao.getList(filter);

        assertEquals(1, candidates.size());
        assertTrue(candidates.stream().map(WbListCandidate::getListName).anyMatch(s -> s.equals(record1.getListName())));
    }

    @Test
    void getListWithLastIdFilter() {
        WbListCandidateRecord record1 = TestObjectFactory.testWbListCandidateRecord();
        record1.setValue("a");
        WbListCandidateRecord record2 = TestObjectFactory.testWbListCandidateRecord();
        record2.setValue("b");
        WbListCandidateRecord record3 = TestObjectFactory.testWbListCandidateRecord();
        record3.setValue("c");

        dslContext.insertInto(WB_LIST_CANDIDATE)
                .set(record1)
                .newRecord()
                .set(record2)
                .newRecord()
                .set(record3)
                .execute();

        FilterRequest filter = new FilterRequest();
        filter.setLastId("1");
        filter.setSortBy("value");
        filter.setSortFieldValue("a");
        List<WbListCandidate> candidates = wbListCandidateDao.getList(filter);

        assertEquals(2, candidates.size());
        assertTrue(candidates.stream().map(WbListCandidate::getListName).anyMatch(s -> s.equals(record2.getListName())));
        assertTrue(candidates.stream().map(WbListCandidate::getListName).anyMatch(s -> s.equals(record3.getListName())));
    }

    @Test
    void approve() {
        WbListCandidateRecord wbListCandidate = TestObjectFactory.testWbListCandidateRecord();
        dslContext.insertInto(WB_LIST_CANDIDATE)
                .set(wbListCandidate)
                .execute();
        WbListCandidateRecord savedWbListCandidate = dslContext.fetchAny(WB_LIST_CANDIDATE);

        assertFalse(savedWbListCandidate.getApproved());

        wbListCandidateDao.approve(List.of(savedWbListCandidate.getId()));

        WbListCandidateRecord approvedWbListCandidate = dslContext.fetchAny(WB_LIST_CANDIDATE);

        assertTrue(approvedWbListCandidate.getApproved());
    }

    @Test
    void getByIds() {
        WbListCandidateRecord record1 = TestObjectFactory.testWbListCandidateRecord();
        record1.setApproved(Boolean.TRUE);
        WbListCandidateRecord record2 = TestObjectFactory.testWbListCandidateRecord();
        record2.setApproved(Boolean.TRUE);
        WbListCandidateRecord record3 = TestObjectFactory.testWbListCandidateRecord();
        record3.setApproved(Boolean.FALSE);
        dslContext.insertInto(WB_LIST_CANDIDATE)
                .set(record1)
                .newRecord()
                .set(record2)
                .newRecord()
                .set(record3)
                .execute();

        Result<WbListCandidateRecord> actualCandidates = dslContext.fetch(WB_LIST_CANDIDATE);
        WbListCandidateRecord wbListCandidateRecord1 = actualCandidates.stream()
                .filter(wbListCandidateRecord -> wbListCandidateRecord.getListName().equals(record1.getListName()))
                .findFirst()
                .get();
        WbListCandidateRecord wbListCandidateRecord2 = actualCandidates.stream()
                .filter(wbListCandidateRecord -> wbListCandidateRecord.getListName().equals(record2.getListName()))
                .findFirst()
                .get();

        List<Long> ids = List.of(wbListCandidateRecord1.getId(), wbListCandidateRecord2.getId());
        List<WbListCandidate> candidates = wbListCandidateDao.getByIds(ids);

        assertEquals(2, candidates.size());
        assertTrue(candidates.stream().map(WbListCandidate::getListName).anyMatch(s -> s.equals(record1.getListName())));
        assertTrue(candidates.stream().map(WbListCandidate::getListName).anyMatch(s -> s.equals(record2.getListName())));
    }
}