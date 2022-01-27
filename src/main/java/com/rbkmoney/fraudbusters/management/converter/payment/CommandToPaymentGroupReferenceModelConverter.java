package com.rbkmoney.fraudbusters.management.converter.payment;

import com.rbkmoney.fraudbusters.management.domain.payment.PaymentGroupReferenceModel;
import dev.vality.damsel.fraudbusters.Command;
import dev.vality.damsel.fraudbusters.GroupReference;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class CommandToPaymentGroupReferenceModelConverter implements Converter<Command, PaymentGroupReferenceModel> {

    @Override
    public PaymentGroupReferenceModel convert(Command command) {
        PaymentGroupReferenceModel model = new PaymentGroupReferenceModel();
        GroupReference groupReference = command.getCommandBody().getGroupReference();
        model.setPartyId(groupReference.getPartyId());
        model.setShopId(groupReference.getShopId());
        model.setGroupId(groupReference.getGroupId());
        return model;
    }
}
