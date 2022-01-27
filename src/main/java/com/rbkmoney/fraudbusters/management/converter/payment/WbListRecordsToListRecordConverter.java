package com.rbkmoney.fraudbusters.management.converter.payment;


import com.rbkmoney.fraudbusters.management.domain.ListRecord;
import dev.vality.fraudbusters.management.domain.tables.pojos.WbListRecords;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WbListRecordsToListRecordConverter {

    ListRecord destinationToSource(WbListRecords destination);
}