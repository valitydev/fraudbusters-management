package dev.vality.fraudbusters.management.domain.payment.request;

import dev.vality.fraudbusters.management.domain.payment.PaymentCountInfo;
import dev.vality.damsel.wb_list.ListType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListRowsInsertRequest {

    private ListType listType;
    private List<PaymentCountInfo> records;
}
