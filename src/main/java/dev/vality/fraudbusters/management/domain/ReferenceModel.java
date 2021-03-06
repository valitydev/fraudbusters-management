package dev.vality.fraudbusters.management.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReferenceModel {

    private String id;
    private String templateId;
    private Boolean isGlobal;
    private String lastUpdateDate;
    private String modifiedByUser;

}
