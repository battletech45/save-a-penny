package com.saveapenny.report.repository;

import com.saveapenny.report.entity.NetWorthSnapshot;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NetWorthSnapshotRepository extends JpaRepository<NetWorthSnapshot, UUID> {

    Optional<NetWorthSnapshot> findByUserIdAndSnapshotDate(UUID userId, LocalDate snapshotDate);
}
