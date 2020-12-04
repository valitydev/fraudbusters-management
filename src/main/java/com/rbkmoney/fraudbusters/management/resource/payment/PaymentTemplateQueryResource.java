package com.rbkmoney.fraudbusters.management.resource.payment;

import com.rbkmoney.fraudbusters.management.dao.TemplateDao;
import com.rbkmoney.fraudbusters.management.dao.payment.reference.PaymentReferenceDao;
import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import com.rbkmoney.fraudbusters.management.domain.payment.PaymentReferenceModel;
import com.rbkmoney.fraudbusters.management.domain.request.FilterRequest;
import com.rbkmoney.fraudbusters.management.domain.response.FilterTemplateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentTemplateQueryResource {

    private final TemplateDao paymentTemplateDao;
    private final PaymentReferenceDao referenceDao;

    @GetMapping(value = "/template/{id}/reference")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<List<PaymentReferenceModel>> getReferences(Principal principal,
                                                                     @PathVariable(value = "id") String id,
                                                                     @Validated @RequestParam(required = false) Integer limit) {
        log.info("getReferences initiator: {} id: {} limit: {}", principal.getName(), id, limit);
        List<PaymentReferenceModel> listByTemplateId = referenceDao.getListByTemplateId(id, limit);
        return ResponseEntity.ok().body(listByTemplateId);
    }

    @GetMapping(value = "/template")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<List<TemplateModel>> getListTemplate(Principal principal,
                                                               @Validated @RequestParam(required = false) Integer limit) {
        log.info("getListTemplate initiator: {} limit: {}", principal.getName(), limit);
        List<TemplateModel> list = paymentTemplateDao.getList(limit);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/template/names")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<List<String>> getTemplatesName(Principal principal,
                                                         @Validated @RequestParam(required = false) String regexpName) {
        log.info("getTemplatesName initiator: {} regexpName: {}", principal.getName(), regexpName);
        List<String> list = paymentTemplateDao.getListNames(regexpName);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/template/filter")
    @PreAuthorize("hasAnyRole('fraud-officer')")
    public ResponseEntity<FilterTemplateResponse> filterTemplates(Principal principal,
                                                                  FilterRequest filterRequest) {
        log.info("filterTemplates initiator: {} filterRequest: {}", principal.getName(), filterRequest);
        List<TemplateModel> templateModels = paymentTemplateDao.filterModel(filterRequest);
        Integer count = paymentTemplateDao.countFilterModel(filterRequest.getSearchValue());
        return ResponseEntity.ok().body(FilterTemplateResponse.builder()
                .count(count)
                .templateModels(templateModels)
                .build());
    }

}
