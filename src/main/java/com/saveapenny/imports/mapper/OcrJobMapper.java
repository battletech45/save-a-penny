package com.saveapenny.imports.mapper;

import com.saveapenny.imports.dto.OcrJobStatusResponse;
import com.saveapenny.imports.dto.OcrSubmitResponse;
import com.saveapenny.imports.entity.OcrJob;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OcrJobMapper {

    @Mapping(target = "jobId", source = "id")
    OcrSubmitResponse toSubmitResponse(OcrJob job);

    @Mapping(target = "jobId", source = "id")
    @Mapping(target = "transactionCandidates", ignore = true)
    OcrJobStatusResponse toStatusResponse(OcrJob job);
}
