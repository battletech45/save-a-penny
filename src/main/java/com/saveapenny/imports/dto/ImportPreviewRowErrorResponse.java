package com.saveapenny.imports.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportPreviewRowErrorResponse {

    private Integer rowNumber;
    private String errorMessage;
    private String rawData;
}
