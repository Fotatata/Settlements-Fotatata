package dev.breezes.settlements.application.enchanting.engine;

import dev.breezes.settlements.shared.util.RandomUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class PowerBudgetCalculator {

    private static final double INTELLIGENCE_BASE_MULTIPLIER = 0.5;
    private static final int POWER_PER_BOOKSHELF = 10;
    private static final double VARIATION_MIN = 0.8;
    private static final double VARIATION_MAX = 1.2;

    public static int calculate(int basePower, double intelligence, int bookshelfCount) {
        double budgetMultiplier = INTELLIGENCE_BASE_MULTIPLIER + intelligence;
        int bookshelfBonus = bookshelfCount * POWER_PER_BOOKSHELF;

        double rawPower = (basePower * budgetMultiplier) + bookshelfBonus;
        double variation = RandomUtil.randomDouble(VARIATION_MIN, VARIATION_MAX);

        return Math.max(0, (int) (rawPower * variation));
    }

}
