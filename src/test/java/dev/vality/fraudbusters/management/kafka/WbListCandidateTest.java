package dev.vality.fraudbusters.management.kafka;

import dev.vality.fraud_data_crawler.FraudDataCandidate;
import dev.vality.fraudbusters.management.TestObjectFactory;
import dev.vality.fraudbusters.management.config.KafkaITest;
import dev.vality.fraudbusters.management.domain.tables.pojos.WbListCandidate;
import dev.vality.fraudbusters.management.service.iface.WbListCandidateBatchService;
import dev.vality.fraudbusters.management.service.iface.WbListCandidateService;
import dev.vality.testcontainers.annotations.kafka.config.KafkaProducer;
import org.apache.thrift.TBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.*;

@KafkaITest
@SpringBootTest
class WbListCandidateTest {

    @Value("${kafka.topic.wblist.candidate}")
    public String topicCandidate;
    @MockBean
    public WbListCandidateService wbListCandidateService;

    @MockBean
    public WbListCandidateBatchService wbListCandidateBatchService;

    @Autowired
    private KafkaProducer<TBase<?, ?>> testThriftKafkaProducer;

    @Test
    void listenCandidate() {
        FraudDataCandidate fraudDataCandidate = TestObjectFactory.testFraudDataCandidate();
        doNothing().when(wbListCandidateService).save(any(WbListCandidate.class));
        doNothing().when(wbListCandidateBatchService).save(anyString(), anyString());

        testThriftKafkaProducer.send(topicCandidate, fraudDataCandidate);

        verify(wbListCandidateService, timeout(5000).times(1)).save(any(WbListCandidate.class));
    }
}
