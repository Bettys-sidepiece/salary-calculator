package model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents a breakdown of tax and deductions for a given gross annual income.
 * This class is immutable and thread-safe.
 */
public final class TaxBreakdown {

    private final BigDecimal grossAnnual;
    private final BigDecimal pensionContribution;
    private final BigDecimal taxableIncome;

    private final BigDecimal basictax;
    private final BigDecimal nationalInsurance;
    private final BigDecimal studentLoanRepayment;

    private final BigDecimal totalDeductions;
    private final BigDecimal netAnnual;

    private final BigDecimal taxFreeAllowance;
    private final BigDecimal taxFreeAllowanceUsed;
    private final BigDecimal taxFreeAllowanceRemaining;

    private final BigDecimal effectiveTaxRate;
    /**
     * Constructs a TaxBreakdown with the specified values.
     *
     * @param grossAnnual the gross annual income (must be positive)
     * @param incomeTax the total income tax deducted (must be non-negative)
     * @param nationalInsurance the total national insurance deducted (must be non-negative)
     * @param studentLoanRepayment the total student loan repayment deducted (must be non-negative)
     * @param netAnnual the net annual income after deductions (must be non-negative and consistent with grossAnnual and deductions)
     * @throws NullPointerException if any of the parameters are null
     * @throws IllegalArgumentException if any of the parameters are invalid (e.g., negative values, inconsistent totals)
     */
    public TaxBreakdown(BigDecimal grossAnnual,
        BigDecimal incomeTax,
        BigDecimal nationalInsurance,
        BigDecimal studentLoanRepayment,
        BigDecimal netAnnual,
        BigDecimal totalDeductions,
        BigDecimal pensionContribution,
        BigDecimal taxableIncome,
        BigDecimal taxFreeAllowance,
        BigDecimal taxFreeAllowanceRemaining,
        BigDecimal effectiveTaxRate) {

            Objects.requireNonNull(grossAnnual, "Gross annual income cannot be null");
            Objects.requireNonNull(incomeTax, "Income tax cannot be null");
            Objects.requireNonNull(nationalInsurance, "National insurance cannot be null");
            Objects.requireNonNull(studentLoanRepayment, "Student loan repayment cannot be null");
            Objects.requireNonNull(totalDeductions, "Total deductions cannot be null");
            Objects.requireNonNull(netAnnual, "Net annual income cannot be null");
            Objects.requireNonNull(pensionContribution, "Pension contribution cannot be null");
            Objects.requireNonNull(taxableIncome, "Taxable income cannot be null");
            Objects.requireNonNull(taxFreeAllowance, "Tax free allowance cannot be null");
            Objects.requireNonNull(taxFreeAllowanceRemaining, "Tax free allowance remaining cannot be null");
            Objects.requireNonNull(effectiveTaxRate, "Effective tax rate cannot be null");

            if (grossAnnual.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Gross annual income must be positive");
            }
            if (incomeTax.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Income tax cannot be negative");
            }
            if (nationalInsurance.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("National insurance cannot be negative");
            }
            if (studentLoanRepayment.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Student loan repayment cannot be negative");
            }
            if (netAnnual.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Net annual income cannot be negative");
            }
            if (pensionContribution.compareTo(BigDecimal.ZERO) < 0){
                throw new IllegalArgumentException("Pension Contribition must be 0.0 or greater");
            }
            if (pensionContribution.compareTo(grossAnnual) > 0){
                throw new IllegalArgumentException("Pension Contribition can not be greater than GrossAnnual");
            }

            if (totalDeductions.add(incomeTax).add(nationalInsurance).add(studentLoanRepayment).add(pensionContribution).compareTo(grossAnnual) > 0) {
                throw new IllegalArgumentException("Total deductions cannot exceed gross annual income");
            }

            if (effectiveTaxRate.compareTo(BigDecimal.ZERO) < 0 || effectiveTaxRate.compareTo(BigDecimal.ONE) > 0) {
                throw new IllegalArgumentException("Effective tax rate must be between 0 and 1");
            }

            if (taxFreeAllowance.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Tax free allowance cannot be negative");
            }
            if (taxFreeAllowanceRemaining.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Tax free allowance remaining cannot be negative");
            }
            if (effectiveTaxRate.compareTo(BigDecimal.ZERO) < 0 || effectiveTaxRate.compareTo(BigDecimal.ONE) > 0) {
                throw new IllegalArgumentException("Effective tax rate must be between 0 and 1");
            }

            if(incomeTax.add(nationalInsurance)
                    .add(studentLoanRepayment)
                    .add(pensionContribution)
                    .add(netAnnual)
                    .compareTo(grossAnnual) != 0){
                throw new IllegalArgumentException("The sum of deductions and net annual income must equal gross annual income");
            }

            if(taxableIncome.add(taxFreeAllowance).compareTo(grossAnnual) < 0){
                throw new IllegalArgumentException("The sum of taxable income and tax free allowance must be at least equal to gross annual income");
            }

            if(grossAnnual.subtract(incomeTax)
                        .subtract(nationalInsurance)
                        .subtract(studentLoanRepayment)
                        .subtract(pensionContribution)
                        .compareTo(netAnnual) != 0){
                throw new IllegalArgumentException("Net annual income must be equal to gross annual income minus deductions");
            }
        
            this.grossAnnual = grossAnnual;
            this.basictax = incomeTax;
            this.nationalInsurance = nationalInsurance;
            this.studentLoanRepayment = studentLoanRepayment;
            this.netAnnual = netAnnual;
            this.pensionContribution = pensionContribution;
            this.totalDeductions = totalDeductions;
            this.taxableIncome = taxableIncome;
            this.taxFreeAllowance = taxFreeAllowance;
            this.taxFreeAllowanceUsed = taxFreeAllowance.subtract(taxFreeAllowanceRemaining);
            this.taxFreeAllowanceRemaining = taxFreeAllowanceRemaining;
            this.effectiveTaxRate = effectiveTaxRate;
    }

    public BigDecimal getGrossAnnual(){
        return grossAnnual;
    }

    public BigDecimal getBasicTax(){
        return basictax;
    }

    public BigDecimal getNationalInsurance(){
        return nationalInsurance;
    }

    public BigDecimal getStudentLoanRepayment(){
        return studentLoanRepayment;
    }

    public BigDecimal getNetAnnual(){
        return netAnnual;
    }

    public BigDecimal getPensionContribution(){
        return pensionContribution;
    }

    public BigDecimal getTaxableIncome(){
        return taxableIncome;
    }

    public BigDecimal getTaxFreeAllowance(){
        return taxFreeAllowance;
    }

    public BigDecimal getTaxFreeAllowanceUsed(){
        return taxFreeAllowanceUsed;
    }

    public BigDecimal getTaxFreeAllowanceRemaining(){
        return taxFreeAllowanceRemaining;
    }

    public BigDecimal getEffectiveTaxRate(){
        return effectiveTaxRate;
    }

    public BigDecimal getTotalDeductions(){
        return totalDeductions;
    }

}