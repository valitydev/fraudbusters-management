package dev.vality.fraudbusters.management.service;

import dev.vality.damsel.wb_list.Command;
import dev.vality.damsel.wb_list.ListType;
import dev.vality.damsel.wb_list.Row;
import dev.vality.fraud_data_crawler.FraudDataCandidate;
import dev.vality.fraudbusters.management.TestObjectFactory;
import dev.vality.fraudbusters.management.config.PostgresqlJooqITest;
import dev.vality.fraudbusters.management.converter.payment.WbListCandidateToRowConverter;
import dev.vality.fraudbusters.management.dao.payment.candidate.WbListCandidateDao;
import dev.vality.fraudbusters.management.dao.payment.candidate.WbListCandidateDaoImpl;
import dev.vality.fraudbusters.management.domain.request.FilterRequest;
import dev.vality.fraudbusters.management.domain.response.FilterResponse;
import dev.vality.fraudbusters.management.domain.tables.pojos.WbListCandidate;
import dev.vality.fraudbusters.management.domain.tables.records.WbListCandidateRecord;
import dev.vality.fraudbusters.management.service.iface.WbListCandidateService;
import org.apache.thrift.TBase;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static dev.vality.fraudbusters.management.domain.tables.WbListCandidate.WB_LIST_CANDIDATE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@PostgresqlJooqITest
@ContextConfiguration(classes = {WbListCandidateDaoImpl.class})
class WbListCandidateServiceImplTest {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private WbListCandidateDao wbListCandidateDao;

    private WbListCandidateService wbListCandidateService;

    @Mock
    private KafkaTemplate<String, TBase> kafkaTemplate;

    @Mock
    private WbListCommandService wbListCommandService;
    @Mock
    private SettableListenableFuture<SendResult<String, TBase>> listenableFuture;

    @BeforeEach
    void setUp() {
        WbListCandidateToRowConverter converter = new WbListCandidateToRowConverter();
        wbListCandidateService = new WbListCandidateServiceImpl(
                wbListCandidateDao,
                kafkaTemplate,
                wbListCommandService,
                converter
        );
        ReflectionTestUtils.setField(wbListCandidateService, "topicCandidate", "wb-list-candidate");
    }

    @Test
    void sendToCandidate() throws Exception {
        int count = 3;
        List<FraudDataCandidate> fraudDataCandidates = TestObjectFactory.testFraudDataCandidates(count);
        when(kafkaTemplate.send(anyString(), any(), any())).thenReturn(listenableFuture);

        List<String> ids = wbListCandidateService.sendToCandidate(fraudDataCandidates);

        assertEquals(count, ids.size());
        verify(kafkaTemplate, times(count)).send(anyString(), anyString(), any(FraudDataCandidate.class));
    }

    @Test
    void getList() {
        WbListCandidateRecord record1 = TestObjectFactory.testWbListCandidateRecord();
        record1.setApproved(Boolean.TRUE);
        WbListCandidateRecord record2 = TestObjectFactory.testWbListCandidateRecord();
        record2.setApproved(Boolean.TRUE);
        record2.setSource(record1.getSource());
        WbListCandidateRecord record3 = TestObjectFactory.testWbListCandidateRecord();
        record3.setApproved(Boolean.FALSE);
        record3.setSource(record1.getSource());
        dslContext.insertInto(WB_LIST_CANDIDATE)
                .set(record1)
                .newRecord()
                .set(record2)
                .newRecord()
                .set(record3)
                .execute();

        List<String> ids = dslContext.fetch(WB_LIST_CANDIDATE).stream()
                .map(WbListCandidateRecord::getId)
                .sorted(Comparator.reverseOrder())
                .map(String::valueOf)
                .collect(Collectors.toList());

        FilterRequest filter = new FilterRequest();
        int size = ids.size() - 1;
        filter.setSize(size);
        FilterResponse<WbListCandidate> filterResponse = wbListCandidateService.getList(filter);

        assertEquals(size, filterResponse.getResult().size());
        assertNotNull(filterResponse.getContinuationId());
        String lastId = ids.get(ids.size() - 2);
        assertEquals(String.valueOf(lastId), filterResponse.getContinuationId());
    }

    @Test
    void approve() {
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
        List<String> ids = dslContext.fetch(WB_LIST_CANDIDATE).stream()
                .map(WbListCandidateRecord::getId)
                .map(String::valueOf)
                .collect(Collectors.toList());
        when(wbListCommandService.sendCommandSync(any(Row.class), any(ListType.class), any(Command.class), anyString()))
                .thenReturn(TestObjectFactory.randomString());
        String initiator = "initiator";

        wbListCandidateService.approve(ids, initiator);

        verify(wbListCommandService, times(3))
                .sendCommandSync(any(Row.class), any(ListType.class), any(Command.class), anyString());
        assertTrue(dslContext.fetch(WB_LIST_CANDIDATE).stream()
                .allMatch(WbListCandidateRecord::getApproved));
    }
}