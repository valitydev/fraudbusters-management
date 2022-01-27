package com.rbkmoney.fraudbusters.management.converter.payment;

import dev.vality.swag.fraudbusters.management.model.FraudPayment;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class FraudPaymentInfoToFraudPaymentConverter
        implements Converter<dev.vality.damsel.fraudbusters.FraudPaymentInfo, FraudPayment> {

    private final PaymentToApiPaymentConverter paymentInfoToPaymentConverter;

    @NonNull
    @Override
    public FraudPayment convert(dev.vality.damsel.fraudbusters.FraudPaymentInfo fraudPayment) {
        return new FraudPayment()
                .payment(paymentInfoToPaymentConverter.convert(fraudPayment.getPayment()))
                .comment(fraudPayment.getComment())
                .type(fraudPayment.getType());
    }
}
