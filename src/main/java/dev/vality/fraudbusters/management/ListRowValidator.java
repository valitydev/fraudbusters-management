package dev.vality.fraudbusters.management;

import dev.vality.fraudbusters.management.exception.SaveRowsException;
import dev.vality.swag.fraudbusters.management.model.PaymentCountInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ListRowValidator {

    public void validate(List<PaymentCountInfo> records) {
        Optional<PaymentCountInfo> invalidData = records.stream()
                .filter(paymentCountInfo -> !StringUtils.hasText(paymentCountInfo.getListRecord().getListName())
                        || !StringUtils.hasText(paymentCountInfo.getListRecord().getValue()))
                .findFirst();
        if (invalidData.isPresent()) {
            log.error("Has invalid data: {}", invalidData);
            throw new SaveRowsException("Has invalid data: " + invalidData);
        }
    }

}
