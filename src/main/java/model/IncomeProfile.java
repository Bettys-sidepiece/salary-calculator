package model;

import java.math.BigDecimal;
import java.util.Objects;

public final class IncomeProfile {

    private final BigDecimal grossAnnual;
    private final BigDecimal pensionContribution;
    private final StudentLoanPlan studentLoanPlan;

    public IncomeProfile (BigDecimal grossAnnual, 
        BigDecimal pensionContribution, 
        StudentLoanPlan studentLoanPlan) {

            Objects.requireNonNull(grossAnnual, "Gross annual income cannot be null");
            Objects.requireNonNull(pensionContribution, "Pension contribution cannot be null");
            Objects.requireNonNull(studentLoanPlan, "Student loan repayment cannot be null");

            if (grossAnnual.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Gross annual income cannot be negative");
            }
            
            if (pensionContribution.compareTo(BigDecimal.ZERO) < 0){
                throw new IllegalArgumentException("Pension Contribition must be 0.0 or greater");
            }

            if (pensionContribution.compareTo(grossAnnual) > 0){
                throw new IllegalArgumentException("Pension Contribition can not be greater than GrossAnnual");
            }

            this.grossAnnual = grossAnnual;
            this.pensionContribution = pensionContribution;
            this.studentLoanPlan = studentLoanPlan;
    }

    public static IncomeProfile HoursWorked(BigDecimal hourlyRate, BigDecimal hoursPerWeek, StudentLoanPlan studentLoanPlan, BigDecimal pensionContribution){
        Objects.requireNonNull(hourlyRate, "Hourly rate cannot be null");
        Objects.requireNonNull(hoursPerWeek, "Hours per week cannot be null");
        Objects.requireNonNull(studentLoanPlan, "Student loan repayment cannot be null");
        Objects.requireNonNull(pensionContribution, "Pension contribution cannot be null");

        if (hourlyRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Hourly rate must be positive");
        }
        if (hoursPerWeek.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Hours per week must be positive");
        }

        BigDecimal grossAnnual = hourlyRate.multiply(hoursPerWeek).multiply(BigDecimal.valueOf(52));
        return new IncomeProfile(grossAnnual, pensionContribution, studentLoanPlan);
    }

    public BigDecimal getBigDecimal(){
        return grossAnnual;
    }

    public BigDecimal getPensionContribution(){
        return pensionContribution;
    }

    public StudentLoanPlan getStudentLoanPlan(){
        return this.studentLoanPlan;
    }
}
