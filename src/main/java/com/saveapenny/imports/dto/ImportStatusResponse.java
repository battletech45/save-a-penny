package com.saveapenny.imports.dto;

import com.saveapenny.imports.entity.ImportStatus;
import java.time.OffsetDateTime;
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
public class ImportStatusResponse {

    private UUID importId;
    private ImportStatus status;
    private Integer totalRows;
    private Integer importedRows;
    private Integer failedRows;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
