package dev.vality.fraudbusters.management.listener;

import dev.vality.fraud_data_crawler.FraudDataCandidate;
import dev.vality.fraudbusters.management.TestObjectFactory;
import dev.vality.fraudbusters.management.converter.payment.FraudDataEventToFraudCandidateConverter;
import dev.vality.fraudbusters.management.domain.tables.pojos.WbListCandidate;
import dev.vality.fraudbusters.management.service.iface.WbListCandidateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.converter.Converter;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
class WbListCandidateListenerTest {

    private WbListCandidateListener wbListCandidateListener;

    @Mock
    private WbListCandidateService wbListCandidateService;

    private final Converter<FraudDataCandidate, WbListCandidate> candidateConverter =
            new FraudDataEventToFraudCandidateConverter();

    @BeforeEach
    void setUp() {
        wbListCandidateListener = new WbListCandidateListener(candidateConverter, wbListCandidateService);
    }

    @Test
    void listen() {
        wbListCandidateListener.listen(TestObjectFactory.testFraudDataCandidate());

        verify(wbListCandidateService, times(1)).save(any(WbListCandidate.class));
    }
}