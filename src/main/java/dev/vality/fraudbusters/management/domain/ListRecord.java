package dev.vality.fraudbusters.management.domain;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class ListRecord {

    @NotNull
    private String listName;
    @NotNull
    private String value;

}
