package dev.vality.fraudbusters.management.resource.payment;

import dev.vality.damsel.wb_list.ListType;
import dev.vality.fraudbusters.management.converter.payment.*;
import dev.vality.fraudbusters.management.dao.payment.wblist.WbListDao;
import dev.vality.fraudbusters.management.service.WbListCommandService;
import dev.vality.fraudbusters.management.service.iface.WbListCandidateBatchService;
import dev.vality.fraudbusters.management.service.iface.WbListCandidateService;
import dev.vality.fraudbusters.management.service.payment.PaymentsListsService;
import dev.vality.fraudbusters.management.utils.ParametersService;
import dev.vality.fraudbusters.management.utils.PaymentCountInfoGenerator;
import dev.vality.fraudbusters.management.utils.UserInfoService;
import dev.vality.fraudbusters.management.utils.parser.CsvPaymentCountInfoParser;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.thrift.TException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PaymentsListsResource.class, CsvPaymentCountInfoParser.class,
        WbListRecordsModelToWbListRecordConverter.class, PaymentsListsService.class})
public class PaymentListLoadDataResourceTest {

    @MockBean
    WbListDao wbListDao;
    @MockBean
    WbListCommandService wbListCommandService;
    @MockBean
    WbListRecordToRowConverter wbListRecordToRowConverter;
    @MockBean
    PaymentCountInfoGenerator paymentCountInfoGenerator;
    @MockBean
    UserInfoService userInfoService;
    @MockBean
    ParametersService parametersService;
    @MockBean
    WbListCandidateBatchService wbListCandidateBatchService;
    @MockBean
    WbListCandidateService wbListCandidateService;
    @MockBean
    WbListCandidateToWbListRecordConverter wbListCandidateToWbListRecordConverter;
    @MockBean
    CandidateBatchModelToCandidateBatchConverter candidateBatchConverter;
    @MockBean
    ChargebacksToFraudDataCandidatesConverter chargebackConverter;

    @Autowired
    PaymentsListsResource paymentsListsResource;

    @Test
    void loadFraudOperation() throws IOException, TException {
        File file = new File("src/test/resources/csv/list-test.csv");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile =
                new MockMultipartFile("file", file.getName(), "text/csv", IOUtils.toByteArray(input));

        paymentsListsResource.insertFromCsv(ListType.black.name(), multipartFile);

        Mockito.verify(wbListCommandService, Mockito.times(1)).sendListRecords(any(), any(), any(), any());
    }
}
