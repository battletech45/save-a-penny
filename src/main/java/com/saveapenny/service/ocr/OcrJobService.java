package com.saveapenny.service.ocr;

import com.saveapenny.imports.dto.OcrJobStatusResponse;
import com.saveapenny.imports.dto.OcrSubmitResponse;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface OcrJobService {

    OcrSubmitResponse createJob(UUID currentUserId, MultipartFile file);

    OcrJobStatusResponse getJobStatus(UUID currentUserId, UUID jobId);
}
