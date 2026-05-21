package com.saveapenny.service.ocr;

import com.saveapenny.config.OcrProperties;
import com.saveapenny.imports.dto.OcrJobStatusResponse;
import com.saveapenny.imports.dto.OcrSubmitResponse;
import com.saveapenny.imports.entity.OcrJob;
import com.saveapenny.imports.entity.OcrJobStatus;
import com.saveapenny.imports.exception.InvalidOcrFileException;
import com.saveapenny.imports.exception.OcrJobNotFoundException;
import com.saveapenny.imports.mapper.OcrJobMapper;
import com.saveapenny.imports.repository.OcrJobRepository;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class OcrJobServiceImpl implements OcrJobService {

    private static final Set<String> ALLOWED_TYPES = Set.of("image/png", "image/jpeg", "application/pdf");

    private final OcrProperties ocrProperties;
    private final OcrJobRepository ocrJobRepository;
    private final OcrJobMapper ocrJobMapper;
    private final OcrTextParserService ocrTextParserService;
    private final OcrJobAsyncProcessor ocrJobAsyncProcessor;

    public OcrJobServiceImpl(
            OcrProperties ocrProperties,
            OcrJobRepository ocrJobRepository,
            OcrJobMapper ocrJobMapper,
            OcrTextParserService ocrTextParserService,
            OcrJobAsyncProcessor ocrJobAsyncProcessor) {
        this.ocrProperties = ocrProperties;
        this.ocrJobRepository = ocrJobRepository;
        this.ocrJobMapper = ocrJobMapper;
        this.ocrTextParserService = ocrTextParserService;
        this.ocrJobAsyncProcessor = ocrJobAsyncProcessor;
    }

    @Override
    public OcrSubmitResponse createJob(UUID currentUserId, MultipartFile file) {
        validateUpload(file);

        OcrJob job = OcrJob.builder()
                .userId(currentUserId)
                .originalFileName(normalizeFileName(file.getOriginalFilename()))
                .contentType(file.getContentType().toLowerCase(Locale.ROOT))
                .status(OcrJobStatus.PENDING)
                .build();
        OcrJob saved = ocrJobRepository.saveAndFlush(job);
        ocrJobAsyncProcessor.process(saved.getId(), file);

        return ocrJobMapper.toSubmitResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public OcrJobStatusResponse getJobStatus(UUID currentUserId, UUID jobId) {
        OcrJob job = ocrJobRepository.findByIdAndUserId(jobId, currentUserId)
                .orElseThrow(() -> new OcrJobNotFoundException(jobId));

        List<com.saveapenny.imports.dto.OcrTransactionCandidateResponse> candidates =
                ocrTextParserService.parseTransactionCandidates(job.getRawText());

        OcrJobStatusResponse response = ocrJobMapper.toStatusResponse(job);
        response.setTransactionCandidates(candidates);
        return response;
    }

    private void validateUpload(MultipartFile file) {
        if (!ocrProperties.enabled()) {
            throw new InvalidOcrFileException("OCR feature is disabled by configuration");
        }
        if (file == null || file.isEmpty()) {
            throw new InvalidOcrFileException("file is empty");
        }
        if (file.getSize() > ocrProperties.maxFileSizeBytes()) {
            throw new InvalidOcrFileException("file exceeds max size of " + ocrProperties.maxFileSizeBytes() + " bytes");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new InvalidOcrFileException("supported types are image/png, image/jpeg, application/pdf");
        }
    }

    private String normalizeFileName(String originalFileName) {
        if (originalFileName == null || originalFileName.isBlank()) {
            return "ocr-upload";
        }
        return originalFileName.trim();
    }
}
