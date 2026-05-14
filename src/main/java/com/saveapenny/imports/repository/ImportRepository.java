package com.saveapenny.imports.repository;

import com.saveapenny.imports.entity.Import;
import com.saveapenny.imports.entity.ImportStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImportRepository extends JpaRepository<Import, UUID> {

    Optional<Import> findByIdAndUserId(UUID id, UUID userId);

    Page<Import> findAllByUserId(UUID userId, Pageable pageable);

    Page<Import> findAllByUserIdAndStatus(UUID userId, ImportStatus status, Pageable pageable);
}
