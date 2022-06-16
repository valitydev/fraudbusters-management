package dev.vality.fraudbusters.management.resource.payment;

import dev.vality.damsel.wb_list.ListType;
import dev.vality.fraudbusters.management.domain.request.FilterRequest;
import dev.vality.fraudbusters.management.exception.SaveRowsException;
import dev.vality.fraudbusters.management.service.WbListCommandService;
import dev.vality.fraudbusters.management.service.payment.PaymentsListsService;
import dev.vality.fraudbusters.management.utils.PagingDataUtils;
import dev.vality.fraudbusters.management.utils.ParametersService;
import dev.vality.fraudbusters.management.utils.PaymentCountInfoGenerator;
import dev.vality.fraudbusters.management.utils.UserInfoService;
import dev.vality.swag.fraudbusters.management.api.PaymentsListsApi;
import dev.vality.swag.fraudbusters.management.model.IdResponse;
import dev.vality.swag.fraudbusters.management.model.ListResponse;
import dev.vality.swag.fraudbusters.management.model.RowListRequest;
import dev.vality.swag.fraudbusters.management.model.WbListRecordsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentsListsResource implements PaymentsListsApi {

    private final WbListCommandService wbListCommandService;
    private final PaymentCountInfoGenerator paymentCountInfoGenerator;
    private final UserInfoService userInfoService;
    private final ParametersService parametersService;
    private final PaymentsListsService paymentsListsService;

    @Override
    @PreAuthorize("hasAnyRole('fraud-monitoring', 'fraud-officer')")
    public ResponseEntity<WbListRecordsResponse> filterLists(@NotNull @Valid List<String> listNames,
                                                             @NotNull @Valid String listType, @Valid String lastId,
                                                             @Valid String sortOrder, @Valid String searchValue,
                                                             @Valid String sortBy, @Valid String sortFieldValue,
                                                             @Valid Integer size) {
        var filterRequest = new FilterRequest(searchValue, lastId, sortFieldValue, size, sortBy,
                PagingDataUtils.getSortOrder(sortOrder));
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
    @PreAuthorize("hasAnyRole('fraud-monitoring', 'fraud-officer')")
    public ResponseEntity<IdResponse> removeRow(String id) {
        String idMessage = paymentsListsService.removeListRecord(id);
        return ResponseEntity.ok().body(
                new IdResponse()
                        .id(idMessage)
        );
    }

}
