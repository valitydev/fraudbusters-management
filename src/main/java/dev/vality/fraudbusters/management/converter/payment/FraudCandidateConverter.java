package dev.vality.fraudbusters.management.converter.payment;

import dev.vality.fraud_data_crawler.FraudDataCandidate;
import dev.vality.fraudbusters.management.domain.tables.pojos.WbListCandidate;
import dev.vality.swag.fraudbusters.management.model.FraudCandidate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FraudCandidateConverter {

    public List<FraudCandidate> toFraudCandidate(List<WbListCandidate> source) {
        return source.stream()
                .map(this::toFraudCandidate)
                .collect(Collectors.toList());
    }

    public FraudCandidate toFraudCandidate(WbListCandidate source) {
        FraudCandidate candidate = new FraudCandidate();
        // TODO добавить id в swag
        candidate.setValue(source.getValue());
        candidate.setSource(source.getSource());
        String listType = source.getListType().getLiteral();
        candidate.setList(dev.vality.swag.fraudbusters.management.model.ListType.fromValue(listType));
        candidate.setType(FraudCandidate.TypeEnum.valueOf(source.getListName()));
        return candidate;
    }

    public List<FraudDataCandidate> toFraudDataCandidate(List<FraudCandidate> source) {
        return source.stream()
                .map(this::toFraudDataCandidate)
                .collect(Collectors.toList());
    }


    public FraudDataCandidate toFraudDataCandidate(FraudCandidate source) {
        FraudDataCandidate candidate = new FraudDataCandidate();
        candidate.setValue(source.getValue());
        candidate.setSource(source.getSource());
        candidate.setListType(dev.vality.fraud_data_crawler.ListType.valueOf(source.getList().getValue()));
        candidate.setType(source.getType().getValue());
        // TODO shop id, merchant id in swag
        return candidate;
    }

}
