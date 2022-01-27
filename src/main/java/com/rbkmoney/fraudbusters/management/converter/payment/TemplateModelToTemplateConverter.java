package com.rbkmoney.fraudbusters.management.converter.payment;


import com.rbkmoney.fraudbusters.management.domain.TemplateModel;
import dev.vality.swag.fraudbusters.management.model.Template;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TemplateModelToTemplateConverter {

    Template destinationToSource(TemplateModel destination);

}
