package model;

import java.math.BigDecimal;

public enum PayPeriod {
    // HOURLY: Use IncomeProfile.HoursWorked() instead - hours per week varies per person
    WEEKLY(BigDecimal.valueOf(52)),
    FORTNIGHTLY(BigDecimal.valueOf(26)),
    MONTHLY(BigDecimal.valueOf(12)),
    ANNUALLY(BigDecimal.ONE);

    private final BigDecimal periodsPerYear;

    PayPeriod(BigDecimal periodsPerYear) {
        this.periodsPerYear = periodsPerYear;
    }

    public BigDecimal getPeriodsPerYear() {
        return periodsPerYear;
    }
}
