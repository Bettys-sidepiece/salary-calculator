package model;

import java.math.BigDecimal;
import java.util.Objects;

import util.MoneyUtils;

public final class IncomeContext {

    private final BigDecimal gross;
    private final BigDecimal adjustedIncome;
    private final BigDecimal taxFreeAllowance;

    public IncomeContext(BigDecimal gross,
                BigDecimal adjustedIncome,
                BigDecimal taxFreeAllowance) {
                
                Objects.requireNonNull(gross, "Gross income cannot be null");
                Objects.requireNonNull(adjustedIncome, "Adjusted income cannot be null");
                Objects.requireNonNull(taxFreeAllowance, "Tax-free allowance cannot be null");

                /* Tax Context invariants:
                    - gross >= 0
                    - adjustedIncome >= 0
                    - taxFreeAllowance >= 0

                    - allowanceUsed = min(adjustedIncome, taxFreeAllowance)
                    - taxableIncome = adjustedIncome - allowanceUsed

                    - taxableIncome >= 0
                    - allowanceUsed >= 0

                    - adjustedIncome = taxableIncome + allowanceUsed
                */
               if (gross.compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("Gross income cannot be negative");
                }
                if (adjustedIncome.compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("Adjusted income cannot be negative");
                }

                if (taxFreeAllowance.compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("Tax-free allowance cannot be negative");
                }

                BigDecimal allowanceUsed = adjustedIncome.min(taxFreeAllowance);
                if (allowanceUsed.compareTo(allowanceUsed) != 0) {
                    throw new IllegalArgumentException("Allowance used must be the minimum of adjusted income and tax-free allowance");
                }
                BigDecimal taxableIncome = adjustedIncome.subtract(allowanceUsed);
                if (taxableIncome.compareTo(taxableIncome) != 0) {
                    throw new IllegalArgumentException("Taxable income must be equal to adjusted income minus allowance used");
                }
                if (taxableIncome.compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("Taxable income cannot be negative");
                }
                if (allowanceUsed.compareTo(BigDecimal.ZERO) < 0) { 
                    throw new IllegalArgumentException("Allowance used cannot be negative");
                }

                this.gross = MoneyUtils.scale(gross);
                this.adjustedIncome = MoneyUtils.scale(adjustedIncome);
                this.taxFreeAllowance = MoneyUtils.scale(taxFreeAllowance);
    }

    public BigDecimal getGross() {
        return gross;
    }

    public BigDecimal getAdjustedIncome() {
        return adjustedIncome;
    }

    public BigDecimal getTaxableIncome() {
        BigDecimal allowanceUsed = adjustedIncome.min(taxFreeAllowance);
        return MoneyUtils.scale(adjustedIncome.subtract(allowanceUsed));
    }

    public BigDecimal getAllowanceUsed() {
        BigDecimal allowanceUsed = adjustedIncome.min(taxFreeAllowance);
        return MoneyUtils.scale(allowanceUsed);
    }

    public BigDecimal getTaxFreeAllowance() {
        return taxFreeAllowance;
    }

}