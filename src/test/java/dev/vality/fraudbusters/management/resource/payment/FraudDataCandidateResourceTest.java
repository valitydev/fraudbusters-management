package dev.vality.fraudbusters.management.resource.payment;

import dev.vality.fraudbusters.management.TestObjectFactory;
import dev.vality.fraudbusters.management.domain.request.FilterRequest;
import dev.vality.fraudbusters.management.domain.response.FilterResponse;
import dev.vality.fraudbusters.management.domain.tables.pojos.WbListCandidate;
import dev.vality.fraudbusters.management.service.iface.WbListCandidateService;
import dev.vality.swag.fraudbusters.management.model.FraudCandidatesRequest;
import dev.vality.swag.fraudbusters.management.model.ListRequest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = {FlywayAutoConfiguration.class, JooqAutoConfiguration.class})
class FraudDataCandidateResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WbListCandidateService wbListCandidateService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void approveFraudCandidates() throws Exception {
        List<String> ids = TestObjectFactory.listRandomStrings(3);
        doNothing().when(wbListCandidateService).approve(anyList(), anyString());

        this.mockMvc.perform(post("/fraud-candidates/approved")
                        .content(objectMapper.writeValueAsString(new ListRequest().records(ids)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.result", hasSize(ids.size())))
                .andExpect(jsonPath("$.result", Matchers.contains(ids.toArray())));

    }

    @Test
    void filterFraudCandidates() throws Exception {
        int count = 3;
        List<WbListCandidate> wbListCandidates = TestObjectFactory.testWbListCandidates(count);
        FilterResponse<WbListCandidate> response = new FilterResponse<>();
        response.setResult(wbListCandidates);
        String continuationId = TestObjectFactory.randomString();
        response.setContinuationId(continuationId);
        when(wbListCandidateService.getList(any(FilterRequest.class))).thenReturn(response);

        this.mockMvc.perform(get("/fraud-candidates")
                        .queryParams(createParams()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", hasSize(count)))
                .andExpect(jsonPath("$.continuationId", equalTo(continuationId)));

    }

    private LinkedMultiValueMap<String, String> createParams() {
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("continuationId", "1");
        params.add("size", "100");
        params.add("sortOrder", "DESC");
        return params;
    }

    @Test
    void insertFraudCandidates() throws Exception {
        FraudCandidatesRequest request = new FraudCandidatesRequest();
        int count = 3;
        request.setRecords(TestObjectFactory.testFraudCandidates(count));
        List<String> uuids = TestObjectFactory.listRandomStrings(count);
        when(wbListCandidateService.sendToCandidate(anyList())).thenReturn(uuids);

        this.mockMvc.perform(post("/fraud-candidates")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.result", hasSize(count)))
                .andExpect(jsonPath("$.result", Matchers.contains(uuids.toArray())));
    }
}