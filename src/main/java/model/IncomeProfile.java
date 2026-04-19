package model;

import java.math.BigDecimal;
import java.util.Objects;

import tax.TaxYear;
import util.MoneyUtils;

public final class IncomeProfile {

    private final BigDecimal grossAnnual;
    private final BigDecimal preTaxDeductions;
    private final StudentLoanPlan studentLoanPlan;
    private final TaxYear taxYear;

    public IncomeProfile (BigDecimal grossAnnual, 
        BigDecimal preTaxDeductions, 
        StudentLoanPlan studentLoanPlan, 
        TaxYear taxYear) {

            Objects.requireNonNull(grossAnnual, "Gross annual income cannot be null");
            Objects.requireNonNull(preTaxDeductions, "Pre-tax deductions cannot be null");
            Objects.requireNonNull(studentLoanPlan, "Student loan repayment cannot be null");
            Objects.requireNonNull(taxYear, "Tax year cannot be null");

            if (grossAnnual.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Gross annual income must be positive");
            }
            
            if (preTaxDeductions.compareTo(BigDecimal.ZERO) < 0){
                throw new IllegalArgumentException("Pre-tax deductions must be 0.0 or greater");
            }

            if (preTaxDeductions.compareTo(grossAnnual) > 0){
                throw new IllegalArgumentException("Pre-tax deductions cannot be greater than Gross Annual");
            }

            this.grossAnnual = MoneyUtils.scale(grossAnnual);
            this.preTaxDeductions = MoneyUtils.scale(preTaxDeductions);
            this.studentLoanPlan = studentLoanPlan;
            this.taxYear = taxYear;
    }

    public static IncomeProfile HoursWorked(BigDecimal hourlyRate,
                BigDecimal hoursPerWeek,
                StudentLoanPlan studentLoanPlan,
                BigDecimal preTaxDeductions){
        Objects.requireNonNull(hourlyRate, "Hourly rate cannot be null");
        Objects.requireNonNull(hoursPerWeek, "Hours per week cannot be null");
        Objects.requireNonNull(studentLoanPlan, "Student loan repayment cannot be null");
        Objects.requireNonNull(preTaxDeductions, "Pre-tax deductions cannot be null");

        if (hourlyRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Hourly rate must be positive");
        }
        if (hoursPerWeek.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Hours per week must be positive");
        }

        BigDecimal grossAnnual = MoneyUtils.scale(hourlyRate.multiply(hoursPerWeek).multiply(BigDecimal.valueOf(52)));
        return new IncomeProfile(grossAnnual, preTaxDeductions, studentLoanPlan, TaxYear.getTaxYear());
    }

    public BigDecimal getGrossAnnual(){
        return grossAnnual;
    }

    public BigDecimal getPreTaxDeductions(){
        return preTaxDeductions;
    }

    public StudentLoanPlan getStudentLoanPlan(){
        return this.studentLoanPlan;
    }

    public TaxYear getTaxYear(){
        return this.taxYear;
    }
}
