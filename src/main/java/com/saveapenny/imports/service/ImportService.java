package com.saveapenny.imports.service;

import com.saveapenny.imports.dto.ImportPreviewResponse;
import com.saveapenny.imports.dto.ImportStatusResponse;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface ImportService {

    ImportPreviewResponse preview(UUID currentUserId, MultipartFile file);

    ImportStatusResponse confirm(UUID currentUserId, UUID importId);

    ImportStatusResponse getStatus(UUID currentUserId, UUID importId);
}
