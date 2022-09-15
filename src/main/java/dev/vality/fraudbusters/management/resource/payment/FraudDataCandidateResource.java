package dev.vality.fraudbusters.management.resource.payment;

import dev.vality.fraud_data_crawler.FraudDataCandidate;
import dev.vality.fraudbusters.management.converter.payment.FraudCandidateConverter;
import dev.vality.fraudbusters.management.domain.request.FilterRequest;
import dev.vality.fraudbusters.management.domain.response.FilterResponse;
import dev.vality.fraudbusters.management.domain.tables.pojos.WbListCandidate;
import dev.vality.fraudbusters.management.service.iface.WbListCandidateService;
import dev.vality.fraudbusters.management.utils.PagingDataUtils;
import dev.vality.fraudbusters.management.utils.UserInfoService;
import dev.vality.swag.fraudbusters.management.api.FraudCandidatesApi;
import dev.vality.swag.fraudbusters.management.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FraudDataCandidateResource implements FraudCandidatesApi {

    private final WbListCandidateService wbListCandidateService;
    private final FraudCandidateConverter fraudCandidateConverter;
    private final UserInfoService userInfoService;

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<ListResponse> approveFraudCandidates(@Valid ListRequest listRequest) {
        List<String> ids = listRequest.getRecords();
        log.info("approveFraudCandidates with ids: {}", String.join(",", ids));
        wbListCandidateService.approve(ids, userInfoService.getUserName());
        log.info("Success approveFraudCandidates with ids: {}", String.join(",", ids));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ListResponse().result(ids)); // TODO нужны ли здесь id в ответе?

    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<FraudCandidatesResponse> filterFraudCandidates(@Valid String continuationId,
                                                                         @Valid String sortOrder,
                                                                         @Valid String sortBy,
                                                                         @Valid Integer size) {
        FilterRequest filter = FilterRequest.builder() // TODO добавить остальные поля в swag
                .lastId(continuationId)
                .sortOrder(PagingDataUtils.getSortOrder(sortOrder))
                .sortBy(sortBy)
                .size(size)
                .build();
        log.info("filterFraudCandidates with filter: {}", filter);
        FilterResponse<WbListCandidate> filterResponse = wbListCandidateService.getList(filter);
        FraudCandidatesResponse result = new FraudCandidatesResponse()
                .continuationId(filterResponse.getContinuationId())
                .result(fraudCandidateConverter.toFraudCandidate(filterResponse.getResult()));
        log.info("Success filterFraudCandidates with result: {}", result);
        return ResponseEntity.ok(result);
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<ListResponse> insertFraudCandidates(@Valid FraudCandidatesRequest fraudCandidatesRequest) {
        List<FraudCandidate> records = fraudCandidatesRequest.getRecords();
        log.info("insertFraudCandidates with request: {}", fraudCandidatesRequest);
        List<FraudDataCandidate> fraudDataCandidates = fraudCandidateConverter.toFraudDataCandidate(records);
        List<String> uuids = wbListCandidateService.sendToCandidate(fraudDataCandidates);
        log.info("Success insertFraudCandidates with ids: {}", uuids);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ListResponse()
                        .result(uuids)); // TODO нужны ли здесь id в ответе?
    }
}
