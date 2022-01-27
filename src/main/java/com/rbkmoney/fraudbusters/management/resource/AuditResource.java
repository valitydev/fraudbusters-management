package com.rbkmoney.fraudbusters.management.resource;

import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.fraudbusters.management.service.iface.AuditService;
import com.rbkmoney.fraudbusters.management.utils.PagingDataUtils;
import com.rbkmoney.fraudbusters.management.utils.UserInfoService;
import dev.vality.damsel.fraudbusters.CommandType;
import dev.vality.fraudbusters.management.domain.enums.ObjectType;
import dev.vality.swag.fraudbusters.management.api.AuditApi;
import dev.vality.swag.fraudbusters.management.model.FilterLogsResponse;
import dev.vality.swag.fraudbusters.management.model.ListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuditResource implements AuditApi {

    private final UserInfoService userInfoService;
    private final AuditService auditService;

    @Override
    @PreAuthorize("hasAnyRole('fraud-monitoring', 'fraud-officer')")
    public ResponseEntity<FilterLogsResponse> filterLogs(@NotNull @Valid List<String> commandTypes,
                                                         @NotNull @Valid List<String> objectTypes,
                                                         @NotNull @Valid String from, @NotNull @Valid String to,
                                                         @Valid String lastId, @Valid String sortOrder,
                                                         @Valid String searchValue, @Valid String sortBy,
                                                         @Valid String sortFieldValue, @Valid Integer size) {
        var filterRequest = new FilterRequest(searchValue, lastId, sortFieldValue, size, sortBy,
                PagingDataUtils.getSortOrder(sortOrder));
        log.info("-> filterLogs initiator: {} from: {} to: {} commandTypes: {} objectTypes: {} filterRequest: {}",
                userInfoService.getUserName(), from, to, commandTypes, objectTypes, filterRequest);
        var filterLogsResponse = auditService.filterRecords(commandTypes, objectTypes, from, to, filterRequest);
        log.info("<- filterLogs filterLogsResponse: {}", filterLogsResponse);
        return ResponseEntity.ok().body(filterLogsResponse);
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-monitoring', 'fraud-officer')")
    public ResponseEntity<ListResponse> getCommandTypes() {
        return ResponseEntity.ok().body(new ListResponse()
                .result(Arrays.stream(CommandType.values())
                        .map(Enum::name)
                        .collect(Collectors.toList())));
    }

    @Override
    @PreAuthorize("hasAnyRole('fraud-monitoring', 'fraud-officer')")
    public ResponseEntity<ListResponse> getObjectTypes() {
        return ResponseEntity.ok().body(new ListResponse()
                .result(Arrays.stream(ObjectType.values())
                        .map(Enum::name)
                        .collect(Collectors.toList()))
        );
    }


}
