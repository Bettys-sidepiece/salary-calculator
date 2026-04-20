package model;

import java.math.BigDecimal;
import java.util.Objects;

import util.MoneyUtils;

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

            // Validate inputs
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

            // Validate values
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
                throw new IllegalArgumentException("Pension contribition must be 0.0 or greater");
            }
            if (pensionContribution.compareTo(grossAnnual) > 0){
                throw new IllegalArgumentException("Pension contribition can not be greater than GrossAnnual");
            }

            if(totalDeductions.compareTo(grossAnnual) > 0){
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
            
            // Assign values with scaling
            this.grossAnnual = MoneyUtils.scale(grossAnnual);
            this.basictax = MoneyUtils.scale(incomeTax);
            this.nationalInsurance = MoneyUtils.scale(nationalInsurance);
            this.studentLoanRepayment = MoneyUtils.scale(studentLoanRepayment);
            this.netAnnual = MoneyUtils.scale(netAnnual);
            this.pensionContribution = MoneyUtils.scale(pensionContribution);
            this.totalDeductions = MoneyUtils.scale(totalDeductions);
            this.taxableIncome = MoneyUtils.scale(taxableIncome);
            this.taxFreeAllowance = MoneyUtils.scale(taxFreeAllowance);
            this.taxFreeAllowanceUsed = MoneyUtils.scale(taxFreeAllowance.subtract(taxFreeAllowanceRemaining));
            this.taxFreeAllowanceRemaining = MoneyUtils.scale(taxFreeAllowanceRemaining);
            this.effectiveTaxRate = MoneyUtils.scale(effectiveTaxRate);
    }

    /* Getters */

    /**
     * Returns the gross annual income.
     *
     * @return the gross annual income
     */
    public BigDecimal getGrossAnnual(){
        return grossAnnual;
    }
    /**
    * Returns the basic income tax amount.
    *
    * @return the basic income tax amount
    */    
    public BigDecimal getBasicTax(){
        return basictax;
    }
    /**
     * Returns the national insurance amount.
     *
     * @return the national insurance amount
     */
    public BigDecimal getNationalInsurance(){
        return nationalInsurance;
    }

    /**
     * Returns the student loan repayment amount.
     *
     * @return the student loan repayment amount
     */
    public BigDecimal getStudentLoanRepayment(){
        return studentLoanRepayment;
    }

    /**
     * Returns the net annual income.
     *
     * @return the net annual income
     */
    public BigDecimal getNetAnnual(){
        return netAnnual;
    }
    /**
     * Returns the pension contribution amount.
     *
     * @return the pension contribution amount
     */
    public BigDecimal getPensionContribution(){
        return pensionContribution;
    }
    /**
     * Returns the taxable income amount.
     *
     * @return the taxable income amount
     */
    public BigDecimal getTaxableIncome(){
        return taxableIncome;
    }
    /**
     * Returns the tax free allowance amount.
     *
     * @return the tax free allowance amount
     */
    public BigDecimal getTaxFreeAllowance(){
        return taxFreeAllowance;
    }
    /**
     * Returns the amount of tax free allowance used.
     *
     * @return the amount of tax free allowance used
     */
    public BigDecimal getTaxFreeAllowanceUsed(){
        return taxFreeAllowanceUsed;
    }
    /**
     * Returns the amount of tax free allowance remaining.
     *
     * @return the amount of tax free allowance remaining
     */
    public BigDecimal getTaxFreeAllowanceRemaining(){
        return taxFreeAllowanceRemaining;
    }
    /**
     * Returns the effective tax rate as a decimal (e.g., 0.25 for 25%).
     *
     * @return the effective tax rate
     */
    public BigDecimal getEffectiveTaxRate(){
        return effectiveTaxRate;
    }
    /**
     * Returns the total deductions amount.
     *
     * @return the total deductions amount
     */
    public BigDecimal getTotalDeductions(){
        return totalDeductions;
    }

}