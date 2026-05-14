package com.saveapenny.imports.repository;

import com.saveapenny.imports.entity.ImportRow;
import com.saveapenny.imports.entity.ImportRowStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImportRowRepository extends JpaRepository<ImportRow, UUID> {

    List<ImportRow> findAllByImportIdOrderByRowNumberAsc(UUID importId);

    long countByImportIdAndStatus(UUID importId, ImportRowStatus status);

    boolean existsByImportIdAndRowNumber(UUID importId, Integer rowNumber);
}
