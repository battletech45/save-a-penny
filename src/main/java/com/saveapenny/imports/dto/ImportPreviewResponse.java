package com.saveapenny.imports.dto;

import java.util.List;
import java.util.UUID;
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
public class ImportPreviewResponse {

    private UUID importId;
    private String fileName;
    private Integer totalRows;
    private Integer validRows;
    private Integer invalidRows;
    private List<ImportPreviewRowErrorResponse> errors;
}
