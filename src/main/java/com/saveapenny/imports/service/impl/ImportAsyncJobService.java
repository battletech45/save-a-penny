package com.saveapenny.imports.service.impl;

import com.saveapenny.imports.entity.Import;
import com.saveapenny.imports.entity.ImportRow;
import com.saveapenny.imports.entity.ImportRowStatus;
import com.saveapenny.imports.entity.ImportStatus;
import com.saveapenny.imports.repository.ImportRepository;
import com.saveapenny.imports.repository.ImportRowRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImportAsyncJobService {

    private final ImportRepository importRepository;
    private final ImportRowRepository importRowRepository;

    public ImportAsyncJobService(ImportRepository importRepository, ImportRowRepository importRowRepository) {
        this.importRepository = importRepository;
        this.importRowRepository = importRowRepository;
    }

    @Async("importTaskExecutor")
    @Transactional
    public void processImportAsync(UUID importId) {
        Import importEntity = importRepository.findById(importId).orElse(null);
        if (importEntity == null) {
            return;
        }

        try {
            List<ImportRow> rows = importRowRepository.findAllByImportIdOrderByRowNumberAsc(importId);

            int importedRows = 0;
            int failedRows = 0;
            for (ImportRow row : rows) {
                if (row.getStatus() == ImportRowStatus.FAILED) {
                    failedRows++;
                    continue;
                }

                row.setStatus(ImportRowStatus.IMPORTED);
                row.setErrorMessage(null);
                importedRows++;
            }

            importRowRepository.saveAll(rows);

            importEntity.setImportedRows(importedRows);
            importEntity.setFailedRows(failedRows);
            importEntity.setStatus(ImportStatus.COMPLETED);
            importRepository.save(importEntity);
        } catch (RuntimeException ex) {
            importEntity.setStatus(ImportStatus.FAILED);
            importRepository.save(importEntity);
            throw ex;
        }
    }
}
