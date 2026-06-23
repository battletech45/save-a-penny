package com.saveapenny.stock.integration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.saveapenny.config.TimeService;
import com.saveapenny.stock.exception.StockRateLimitExceededException;
import com.saveapenny.stock.infrastructure.RateLimitTracker;
import java.time.Clock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RateLimitTrackerTest {

    private RateLimitTracker tracker;

    @BeforeEach
    void setUp() {
        tracker = new RateLimitTracker(5, 25, new TimeService(Clock.systemUTC()));
    }

    @Test
    void allowsRequestsWithinLimit() {
        for (int i = 0; i < 5; i++) {
            assertDoesNotThrow(() -> tracker.checkQuota());
        }
    }

    @Test
    void throwsWhenMinuteLimitExceeded() {
        for (int i = 0; i < 5; i++) {
            tracker.checkQuota();
        }
        assertThrows(StockRateLimitExceededException.class, () -> tracker.checkQuota());
    }

    @Test
    void throwsWhenDayLimitExceeded() {
        RateLimitTracker dayLimited = new RateLimitTracker(1000, 1, new TimeService(Clock.systemUTC()));
        dayLimited.checkQuota();
        assertThrows(StockRateLimitExceededException.class, () -> dayLimited.checkQuota());
    }

    @Test
    void tracksMinuteWindowSize() {
        tracker.checkQuota();
        tracker.checkQuota();
        assert tracker.minuteWindowSize() == 2;
    }

    @Test
    void resetClearsWindows() {
        tracker.checkQuota();
        tracker.reset();
        assert tracker.minuteWindowSize() == 0;
        assert tracker.dayWindowSize() == 0;
        assertDoesNotThrow(() -> tracker.checkQuota());
    }
}
