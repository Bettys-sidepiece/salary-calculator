package model;

import java.math.BigDecimal;

public enum StudentLoanPlan{
    NONE(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
    PLAN1(BigDecimal.valueOf(22015), BigDecimal.valueOf(0.09), BigDecimal.ZERO, BigDecimal.ZERO),
    PLAN2(BigDecimal.valueOf(27715), BigDecimal.valueOf(0.09), BigDecimal.ZERO, BigDecimal.ZERO),
    PLAN_4(BigDecimal.valueOf(27665), BigDecimal.valueOf(0.09), BigDecimal.ZERO, BigDecimal.ZERO),
    POSTGRAD(BigDecimal.valueOf(21000), BigDecimal.valueOf(0.06), BigDecimal.ZERO, BigDecimal.ZERO),
    CUSTOM(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);

    private final BigDecimal threshold;
    private final BigDecimal rate;
    private final BigDecimal fixedRepayment;
    private final BigDecimal interestRate;

    StudentLoanPlan(BigDecimal threshold, 
    BigDecimal rate, BigDecimal fixedRepayment, BigDecimal interestRate) {

        if (threshold.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Threshold cannot be negative");
        }
        if (rate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Rate cannot be negative");
        }
        if (fixedRepayment.compareTo(BigDecimal.ZERO) < 0) {       
            throw new IllegalArgumentException("Fixed repayment cannot be negative");
        }
        if (interestRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Interest rate cannot be negative");
        }  

        this.threshold = threshold;
        this.rate = rate;
        this.fixedRepayment = fixedRepayment;
        this.interestRate = interestRate;
    }


    public BigDecimal getThreshold() {
        return threshold;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public BigDecimal getFixedRepayment() {
        return fixedRepayment;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

}
