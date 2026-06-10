package com.saveapenny.report.scheduler;

import com.saveapenny.report.entity.NetWorthSnapshot;
import com.saveapenny.report.repository.NetWorthSnapshotRepository;
import com.saveapenny.report.repository.ReportAccountRepository;
import com.saveapenny.account.entity.AccountType;
import com.saveapenny.user.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class NetWorthSnapshotScheduler {

    private static final Logger log = LoggerFactory.getLogger(NetWorthSnapshotScheduler.class);

    private final UserRepository userRepository;
    private final ReportAccountRepository reportAccountRepository;
    private final NetWorthSnapshotRepository netWorthSnapshotRepository;

    public NetWorthSnapshotScheduler(
            UserRepository userRepository,
            ReportAccountRepository reportAccountRepository,
            NetWorthSnapshotRepository netWorthSnapshotRepository) {
        this.userRepository = userRepository;
        this.reportAccountRepository = reportAccountRepository;
        this.netWorthSnapshotRepository = netWorthSnapshotRepository;
    }

    @Scheduled(cron = "${report.net-worth.snapshot-cron:0 0 2 * * *}")
    @Transactional
    public void computeDailySnapshots() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        var userIds = userRepository.findAllUserIds();

        for (UUID userId : userIds) {
            if (netWorthSnapshotRepository.findByUserIdAndSnapshotDate(userId, yesterday).isPresent()) {
                continue;
            }

            try {
                BigDecimal totalAssets = reportAccountRepository.sumAssetsByUserId(userId, AccountType.CREDIT);
                BigDecimal totalLiabilities = reportAccountRepository.sumLiabilitiesByUserId(userId, AccountType.CREDIT);
                BigDecimal netWorth = totalAssets.subtract(totalLiabilities);

                NetWorthSnapshot snapshot = NetWorthSnapshot.builder()
                        .userId(userId)
                        .snapshotDate(yesterday)
                        .totalAssets(totalAssets)
                        .totalLiabilities(totalLiabilities)
                        .netWorth(netWorth)
                        .build();
                netWorthSnapshotRepository.save(snapshot);
            } catch (Exception ex) {
                log.warn("Failed to compute net worth snapshot for user {} on {}: {}", userId, yesterday, ex.getMessage());
            }
        }
    }
}
