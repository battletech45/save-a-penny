package com.saveapenny.service.ocr;

import java.util.concurrent.CompletableFuture;
import org.springframework.web.multipart.MultipartFile;

public interface OcrService {

    String extractText(MultipartFile file);

    CompletableFuture<String> extractTextAsync(MultipartFile file);
}
