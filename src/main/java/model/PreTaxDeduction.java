package model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class PreTaxDeduction {
    /* Type defines the category of the deduction, which can be used for reporting or applying specific rules based on deduction type.
       - PENSION: contributions to a pension scheme
       - SALARY_SACRIFICE: salary sacrifice arrangements
       - GIFT_AID: charitable donations eligible for Gift Aid
       - PROFESSIONAL_SUBSCRIPTION: subscriptions to professional bodies
       - OTHER: any other type of pre-tax deduction not covered by the above categories */
    public enum Type {
        PENSION,
        SALARY_SACRIFICE,
        GIFT_AID,
        PROFESSIONAL_SUBSCRIPTION,
        OTHER
    }

    /* Mode defines how the deduction value should be interpreted:
       - FIXED: the value is a fixed amount in £ to be deducted from gross salary
       - PERCENTAGE: the value is a percentage (as a decimal) of the gross salary to be deducted (e.g. 0.05 for 5%) */  
    public enum Mode {
        FIXED,       // flat £ amount
        PERCENTAGE   // % of gross salary
    }

    private final String label;
    private final Type type;
    private final Mode mode;
    private final BigDecimal value; // either £ amount or % as decimal e.g. 0.05 = 5%

    /**
     * Creates a new PreTaxDeduction instance with the specified parameters.
     * @param label - a descriptive label for the deduction (cannot be null)
     * @param type  - the type of deduction (cannot be null)
     * @param mode  - the mode of deduction, either FIXED or PERCENTAGE (cannot be null)
     * @param value - the value of the deduction, either a fixed amount in £ or a percentage as a decimal (cannot be null, must be non-negative, percentage cannot exceed 100%)
     * @throws NullPointerException if any of the parameters are null
     * @throws IllegalArgumentException if value is negative or if percentage value exceeds 100%
     */
    public PreTaxDeduction(String label, Type type, Mode mode, BigDecimal value) {
        Objects.requireNonNull(label, "Label cannot be null");
        Objects.requireNonNull(type, "Type cannot be null");
        Objects.requireNonNull(mode, "Mode cannot be null");
        Objects.requireNonNull(value, "Value cannot be null");

        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Deduction value cannot be negative");
        }
        if (mode == Mode.PERCENTAGE && value.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("Percentage deduction cannot exceed 100%");
        }

        this.label = label;
        this.type = type;
        this.mode = mode;
        this.value = value;
    }

    /**
     * Resolves the actual £ deduction amount against the gross salary.
     * @param grossSalary the gross salary to apply the deduction against (cannot be null)
     * @return the resolved deduction amount in £, scaled to 2 decimal places
     * @throws NullPointerException if grossSalary is null
     */
    public BigDecimal resolve(BigDecimal grossSalary) {
        Objects.requireNonNull(grossSalary, "Gross salary cannot be null");
        if (mode == Mode.PERCENTAGE) {
            return grossSalary.multiply(value).setScale(2, RoundingMode.HALF_UP);
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    public String getLabel() { return label; }
    public Type getType() { return type; }
    public Mode getMode() { return mode; }
    public BigDecimal getValue() { return value; }
}
