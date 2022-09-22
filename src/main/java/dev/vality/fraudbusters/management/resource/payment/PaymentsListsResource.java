package dev.vality.fraudbusters.management.resource.payment;

import dev.vality.damsel.wb_list.ListType;
import dev.vality.fraud_data_crawler.FraudDataCandidate;
import dev.vality.fraudbusters.management.converter.payment.CandidateBatchModelToCandidateBatchConverter;
import dev.vality.fraudbusters.management.converter.payment.ChargebacksToFraudDataCandidatesConverter;
import dev.vality.fraudbusters.management.converter.payment.WbListCandidateToWbListRecordConverter;
import dev.vality.fraudbusters.management.domain.WbListCandidateBatchModel;
import dev.vality.fraudbusters.management.domain.request.FilterRequest;
import dev.vality.fraudbusters.management.domain.response.FilterResponse;
import dev.vality.fraudbusters.management.domain.tables.pojos.WbListCandidate;
import dev.vality.fraudbusters.management.exception.SaveRowsException;
import dev.vality.fraudbusters.management.service.WbListCommandService;
import dev.vality.fraudbusters.management.service.iface.WbListCandidateBatchService;
import dev.vality.fraudbusters.management.service.iface.WbListCandidateService;
import dev.vality.fraudbusters.management.service.payment.PaymentsListsService;
import dev.vality.fraudbusters.management.utils.PagingDataUtils;
import dev.vality.fraudbusters.management.utils.ParametersService;
import dev.vality.fraudbusters.management.utils.PaymentCountInfoGenerator;
import dev.vality.fraudbusters.management.utils.UserInfoService;
import dev.vality.swag.fraudbusters.management.api.PaymentsListsApi;
import dev.vality.swag.fraudbusters.management.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentsListsResource implements PaymentsListsApi {

    private final WbListCommandService wbListCommandService;
    private final PaymentCountInfoGenerator paymentCountInfoGenerator;
    private final UserInfoService userInfoService;
    private final ParametersService parametersService;
    private final PaymentsListsService paymentsListsService;
    private final WbListCandidateService wbListCandidateService;
    private final WbListCandidateBatchService wbListCandidateBatchService;
    private final WbListCandidateToWbListRecordConverter candidateConverter;
    private final CandidateBatchModelToCandidateBatchConverter candidateBatchConverter;
    private final ChargebacksToFraudDataCandidatesConverter chargebackConverter;

    @Override
    @PreAuthorize("hasAnyRole('fraud-monitoring', 'fraud-officer')")
    public ResponseEntity<Void> approveListCandidates(@Valid IdListRequest listRequest) {
        List<Long> ids = listRequest.getIds();
        log.info("approveFraudCandidates with ids: {}", Arrays.toString(ids.toArray()));
        wbListCandidateService.approve(ids, userInfoService.getUserName());
        log.info("Success approveFraudCandidates with ids: {}", Arrays.toString(ids.toArray()));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-monitoring', 'fraud-officer')")
    public ResponseEntity<WbListRecordsResponse> filterLists(@NotNull @Valid List<String> listNames,
                                                             @NotNull @Valid String listType, @Valid String lastId,
                                                             @Valid String sortOrder, @Valid String searchValue,
                                                             @Valid String sortBy, @Valid String sortFieldValue,
                                                             @Valid Integer size) {
        var filterRequest = FilterRequest.builder()
                .lastId(lastId)
                .sortOrder(PagingDataUtils.getSortOrder(sortOrder))
                .searchValue(searchValue)
                .sortBy(sortBy)
                .sortFieldValue(sortFieldValue)
                .size(size)
                .build();
        String userName = userInfoService.getUserName();
        log.info("filterList initiator: {} listType: {} listNames: {} filterRequest: {}",
                userName, listType, listNames, filterRequest);
        WbListRecordsResponse result = paymentsListsService.filterLists(listNames, listType, filterRequest);
        return ResponseEntity.ok(result
        );
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-monitoring', 'fraud-officer')")
    public ResponseEntity<ListResponse> getAvailableListNames() {
        var listResponse = new ListResponse();
        listResponse.setResult(parametersService.getAvailableListNames());
        return ResponseEntity.ok().body(listResponse);
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-monitoring', 'fraud-officer')")
    public ResponseEntity<ListResponse> insertRow(@Valid RowListRequest request) {
        log.info("insertRowsToList initiator: {} request {}", userInfoService.getUserName(), request);
        try {
            List<String> ids = wbListCommandService.sendListRecords(
                    request.getRecords(),
                    ListType.valueOf(request.getListType().getValue()),
                    paymentCountInfoGenerator::initRow,
                    userInfoService.getUserName());
            return ResponseEntity.ok().body(new ListResponse()
                    .result(ids));
        } catch (SaveRowsException e) {
            return ResponseEntity.status(500).build();
        }
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-monitoring', 'fraud-officer')")
    public ResponseEntity<WbListCandidatesResponse> listCandidates(@Valid Long lastId,
                                                                   @Valid String sortOrder,
                                                                   @Valid String searchValue,
                                                                   @Valid String sortBy,
                                                                   @Valid String sortFieldValue,
                                                                   @Valid Integer size) {
        FilterRequest filter = FilterRequest.builder()
                .numericLastId(lastId)
                .sortOrder(PagingDataUtils.getSortOrder(sortOrder))
                .searchValue(searchValue)
                .sortBy(sortBy)
                .sortFieldValue(sortFieldValue)
                .size(size)
                .build();
        log.info("listCandidates with filter: {}", filter);
        FilterResponse<WbListCandidate> filterResponse = wbListCandidateService.getList(filter);
        WbListCandidatesResponse result = new WbListCandidatesResponse()
                .lastId(filterResponse.getNumericLastId())
                .source(filterResponse.getResult().get(0).getSource())
                .result(candidateConverter.toWbListRecord(filterResponse.getResult()));
        log.info("Success listCandidates with result: {}", result);
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<WbListCandidatesBatchesResponse> listCandidatesBatches(@Valid String lastId,
                                                                                 @Valid String sortOrder,
                                                                                 @Valid String searchValue,
                                                                                 @Valid String sortBy,
                                                                                 @Valid String sortFieldValue,
                                                                                 @Valid Integer size) {
        FilterRequest filter = FilterRequest.builder()
                .lastId(lastId)
                .sortOrder(PagingDataUtils.getSortOrder(sortOrder))
                .searchValue(searchValue)
                .sortBy(sortBy)
                .sortFieldValue(sortFieldValue)
                .size(size)
                .build();
        log.info("listCandidatesBatches with filter: {}", filter);
        FilterResponse<WbListCandidateBatchModel> filterResponse = wbListCandidateBatchService.getList(filter);
        WbListCandidatesBatchesResponse result = new WbListCandidatesBatchesResponse()
                .lastId(filterResponse.getLastId())
                .result(candidateBatchConverter.toWbListCandidateBatch(filterResponse.getResult()));
        log.info("Success listCandidatesBatches with result: {}", result);
        return ResponseEntity.ok(result);
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-monitoring', 'fraud-officer')")
    public ResponseEntity<ListResponse> getCurrentListNames(@NotNull @Valid String listType) {
        var listResponse = new ListResponse();
        listResponse.setResult(paymentsListsService.getCurrentListNames(listType));
        return ResponseEntity.ok().body(listResponse);
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-monitoring', 'fraud-officer')")
    public ResponseEntity<Void> insertFromCsv(@Valid String listType, @Valid MultipartFile file) {
        String userName = userInfoService.getUserName();
        log.info("Insert from csv initiator: {} listType: {}", userName, listType);
        paymentsListsService.insertCsv(listType, file, userName);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> insertListCandidates(@Valid WbListCandidatesRequest wbListCandidatesRequest) {
        String batchId = UUID.randomUUID().toString();
        log.info("insertListCandidates with request: {}, batchId: {}", wbListCandidatesRequest, batchId);
        List<FraudDataCandidate> fraudDataCandidates =
                chargebackConverter.toCandidates(wbListCandidatesRequest.getRecords(), batchId);
        List<String> uuids = wbListCandidateService.sendToCandidate(fraudDataCandidates);
        log.info("Success insertListCandidates with ids: {}", uuids);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-monitoring', 'fraud-officer')")
    public ResponseEntity<IdResponse> removeRow(String id) {
        String idMessage = paymentsListsService.removeListRecord(id);
        return ResponseEntity.ok().body(
                new IdResponse()
                        .id(idMessage)
        );
    }

}
