package dev.vality.fraudbusters.management.converter.payment;

import dev.vality.fraud_data_crawler.FraudDataCandidate;
import dev.vality.fraud_data_crawler.ListType;
import dev.vality.fraudbusters.management.constant.CandidateListName;
import dev.vality.swag.fraudbusters.management.model.Chargeback;
import dev.vality.swag.fraudbusters.management.model.ClientInfo;
import dev.vality.swag.fraudbusters.management.model.MerchantInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ChargebacksToFraudDataCandidatesConverter {

    private static final String CHARGEBACK_SOURCE = "Chargebacks";

    public List<FraudDataCandidate> toCandidates(List<Chargeback> chargebacks, String batchId) {
        return chargebacks.stream()
                .flatMap(chargeback -> toCandidates(chargeback, batchId).stream())
                .collect(Collectors.toList());
    }

    private List<FraudDataCandidate> toCandidates(Chargeback chargeback, String batchId) {
        ClientInfo clientInfo = chargeback.getClientInfo();
        List<FraudDataCandidate> candidates = new ArrayList<>();
        FraudDataCandidate candidateEmail = prepareCandidate(batchId, chargeback.getMerchantInfo());
        candidateEmail.setValue(clientInfo.getEmail());
        candidateEmail.setType(CandidateListName.EMAIL);
        candidates.add(candidateEmail);
        FraudDataCandidate candidateIp = prepareCandidate(batchId, chargeback.getMerchantInfo());
        candidateIp.setValue(clientInfo.getIp());
        candidateIp.setType(CandidateListName.IP);
        candidates.add(candidateIp);
        FraudDataCandidate candidateFingerprint = prepareCandidate(batchId, chargeback.getMerchantInfo());
        candidateFingerprint.setValue(clientInfo.getFingerprint());
        candidateFingerprint.setType(CandidateListName.FINGERPRRINT);
        candidates.add(candidateFingerprint);
        return candidates;
    }

    private FraudDataCandidate prepareCandidate(String batchId, MerchantInfo merchantInfo) {
        FraudDataCandidate candidate = new FraudDataCandidate();
        candidate.setSource(CHARGEBACK_SOURCE);
        candidate.setBatchId(batchId);
        candidate.setListType(ListType.black);
        if (Objects.nonNull(merchantInfo)) {
            candidate.setMerchantId(merchantInfo.getPartyId());
            candidate.setShopId(merchantInfo.getShopId());
        }
        return candidate;
    }


}
