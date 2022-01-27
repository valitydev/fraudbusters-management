package com.rbkmoney.fraudbusters.management.converter;


import com.rbkmoney.fraudbusters.management.domain.ListRecord;
import dev.vality.damsel.wb_list.Row;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ListRecordToRowConverter {

    Row destinationToSource(ListRecord destination);

}
