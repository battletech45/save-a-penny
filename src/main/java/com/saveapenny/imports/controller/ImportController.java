package com.saveapenny.imports.controller;

import com.saveapenny.config.security.CurrentUserPrincipal;
import com.saveapenny.imports.dto.ConfirmImportRequest;
import com.saveapenny.imports.dto.ImportPreviewResponse;
import com.saveapenny.imports.dto.ImportStatusResponse;
import com.saveapenny.imports.service.ImportService;
import com.saveapenny.shared.api.ApiResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/imports/transactions")
@PreAuthorize("isAuthenticated()")
public class ImportController {

    private final ImportService importService;

    public ImportController(ImportService importService) {
        this.importService = importService;
    }

    @PostMapping(value = "/preview", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ImportPreviewResponse>> preview(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @RequestPart("file") MultipartFile file) {
        ImportPreviewResponse response = importService.preview(getCurrentUserId(principal), file);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<ImportStatusResponse>> confirm(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @Valid @RequestBody ConfirmImportRequest request) {
        ImportStatusResponse response = importService.confirm(getCurrentUserId(principal), request.getImportId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{importId}/status")
    public ResponseEntity<ApiResponse<ImportStatusResponse>> getStatus(
            @AuthenticationPrincipal CurrentUserPrincipal principal,
            @PathVariable UUID importId) {
        ImportStatusResponse response = importService.getStatus(getCurrentUserId(principal), importId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    private UUID getCurrentUserId(CurrentUserPrincipal principal) {
        if (principal == null || principal.userId() == null) {
            throw new AccessDeniedException("Missing authenticated user context.");
        }
        return principal.userId();
    }
}
