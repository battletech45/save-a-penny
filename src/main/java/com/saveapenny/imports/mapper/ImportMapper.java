package com.saveapenny.imports.mapper;

import com.saveapenny.imports.dto.ImportPreviewRowErrorResponse;
import com.saveapenny.imports.dto.ImportStatusResponse;
import com.saveapenny.imports.entity.Import;
import com.saveapenny.imports.entity.ImportRow;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ImportMapper {

    @Mapping(target = "importId", source = "id")
    ImportStatusResponse toStatusResponse(Import importEntity);

    ImportPreviewRowErrorResponse toPreviewRowErrorResponse(ImportRow importRow);
}
