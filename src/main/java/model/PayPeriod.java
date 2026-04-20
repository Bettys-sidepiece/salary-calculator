package model;

import java.math.BigDecimal;

public enum PayPeriod {
    HOURLY(null), // Hourly pay doesn't have a fixed number of periods per year
    DAILY(null), // Daily pay doesn't have a fixed number of periods per year
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
