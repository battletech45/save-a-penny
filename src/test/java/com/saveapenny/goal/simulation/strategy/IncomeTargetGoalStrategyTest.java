package com.saveapenny.goal.simulation.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.saveapenny.goal.entity.Feasibility;
import com.saveapenny.goal.entity.GoalType;
import com.saveapenny.goal.simulation.IncomeStrategy;
import com.saveapenny.goal.simulation.SimulationInput;
import com.saveapenny.goal.simulation.SimulationResult;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class IncomeTargetGoalStrategyTest {

    private final IncomeTargetGoalStrategy strategy = new IncomeTargetGoalStrategy();

    @Test
    void simulate_returnsOnTrack_forManageableCompoundIncomeGoal() {
        SimulationResult result = strategy.simulate(baseInput()
                .currentAverageMonthlyNetIncome(new BigDecimal("4000"))
                .targetMonthlyNetIncome(new BigDecimal("4450"))
                .expectedIncomeGrowthRate(new BigDecimal("1.0"))
                .incomeStrategy(IncomeStrategy.COMPOUND)
                .build());

        assertEquals(Feasibility.ON_TRACK, result.getFeasibility());
    }

    @Test
    void simulate_returnsTight_forModerateCompoundGrowthNeed() {
        SimulationResult result = strategy.simulate(baseInput()
                .currentAverageMonthlyNetIncome(new BigDecimal("4000"))
                .targetMonthlyNetIncome(new BigDecimal("5200"))
                .expectedIncomeGrowthRate(new BigDecimal("1.0"))
                .incomeStrategy(IncomeStrategy.COMPOUND)
                .build());

        assertEquals(Feasibility.TIGHT, result.getFeasibility());
    }

    @Test
    void simulate_returnsInfeasible_forAggressiveCompoundGrowthNeed() {
        SimulationResult result = strategy.simulate(baseInput()
                .currentAverageMonthlyNetIncome(new BigDecimal("3000"))
                .targetMonthlyNetIncome(new BigDecimal("15000"))
                .expectedIncomeGrowthRate(BigDecimal.ZERO)
                .incomeStrategy(IncomeStrategy.COMPOUND)
                .build());

        assertEquals(Feasibility.INFEASIBLE, result.getFeasibility());
    }

    @Test
    void simulate_returnsOnTrack_forManageableLinearIncomeGoal() {
        SimulationResult result = strategy.simulate(baseInput()
                .currentAverageMonthlyNetIncome(new BigDecimal("4000"))
                .targetMonthlyNetIncome(new BigDecimal("4400"))
                .expectedIncomeGrowthRate(new BigDecimal("1.0"))
                .incomeStrategy(IncomeStrategy.LINEAR)
                .build());

        assertEquals(Feasibility.ON_TRACK, result.getFeasibility());
        assertEquals(new BigDecimal("0.42"), result.getSummary().get("requiredMonthlyGrowthRate"));
        assertEquals(new BigDecimal("16.67"), result.getSummary().get("requiredMonthlyIncomeIncrease"));
    }

    @Test
    void simulate_returnsTight_forModerateLinearIncomeGoal() {
        SimulationResult result = strategy.simulate(baseInput()
                .currentAverageMonthlyNetIncome(new BigDecimal("4000"))
                .targetMonthlyNetIncome(new BigDecimal("4800"))
                .expectedIncomeGrowthRate(new BigDecimal("1.0"))
                .incomeStrategy(IncomeStrategy.LINEAR)
                .build());

        assertEquals(Feasibility.TIGHT, result.getFeasibility());
        assertEquals(new BigDecimal("0.83"), result.getSummary().get("requiredMonthlyGrowthRate"));
    }

    @Test
    void simulate_returnsAtRisk_forDemandingLinearIncomeGoal() {
        SimulationResult result = strategy.simulate(baseInput()
                .currentAverageMonthlyNetIncome(new BigDecimal("4000"))
                .targetMonthlyNetIncome(new BigDecimal("6400"))
                .expectedIncomeGrowthRate(BigDecimal.ZERO)
                .incomeStrategy(IncomeStrategy.LINEAR)
                .build());

        assertEquals(Feasibility.AT_RISK, result.getFeasibility());
        assertEquals(new BigDecimal("2.50"), result.getSummary().get("requiredMonthlyGrowthRate"));
    }

    @Test
    void simulate_returnsInfeasible_forAggressiveLinearIncomeGoal() {
        SimulationResult result = strategy.simulate(baseInput()
                .currentAverageMonthlyNetIncome(new BigDecimal("3000"))
                .targetMonthlyNetIncome(new BigDecimal("8000"))
                .expectedIncomeGrowthRate(BigDecimal.ZERO)
                .incomeStrategy(IncomeStrategy.LINEAR)
                .build());

        assertEquals(Feasibility.INFEASIBLE, result.getFeasibility());
        assertEquals(new BigDecimal("6.94"), result.getSummary().get("requiredMonthlyGrowthRate"));
    }

    private SimulationInput.SimulationInputBuilder baseInput() {
        return SimulationInput.builder()
                .type(GoalType.INCOME_TARGET)
                .asOfDate(LocalDate.of(2026, 6, 1))
                .targetDate(LocalDate.of(2028, 6, 1))
                .currency("USD");
    }
}
