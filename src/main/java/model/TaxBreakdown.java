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
    private final BigDecimal taxableIncome;

    private final BigDecimal basictax;
    private final BigDecimal nationalInsurance;
    private final BigDecimal studentLoanRepayment;

    // Pre-tax deductions broken down by type
    private final BigDecimal pensionContribution;
    private final BigDecimal salarySacrifice;
    private final BigDecimal giftAid;
    private final BigDecimal professionalSubscriptions;
    private final BigDecimal otherPreTaxDeductions;
    private final BigDecimal totalPreTaxDeductions;

    private final BigDecimal totalDeductions;
    private final BigDecimal netAnnual;

    private final BigDecimal taxFreeAllowance;
    private final BigDecimal taxFreeAllowanceUsed;
    private final BigDecimal taxFreeAllowanceRemaining;

    private final BigDecimal effectiveTaxRate;
    /**
     * Constructs a TaxBreakdown object with the specified values.
     *
     * @param grossAnnual the gross annual income
     * @param incomeTax the income tax amount
     * @param nationalInsurance the national insurance amount
     * @param studentLoanRepayment the student loan repayment amount
     * @param netAnnual the net annual income
     * @param totalDeductions the total deductions amount
     * @param pensionContribution the pension contribution amount
     * @param salarySacrifice the salary sacrifice amount
     * @param giftAid the gift aid amount
     * @param professionalSubscriptions the professional subscriptions amount
     * @param otherPreTaxDeductions the other pre-tax deductions amount
     * @param taxableIncome the taxable income amount
     * @param taxFreeAllowance the tax free allowance amount
     * @param taxFreeAllowanceRemaining the remaining tax free allowance amount
     * @param effectiveTaxRate the effective tax rate
     * @throws NullPointerException if any of the parameters are null
     * @throws IllegalArgumentException if any of the monetary values are negative, if total deductions
     * exceed gross annual income, if effective tax rate is not between 0 and 1, or if the sum of deductions and net annual income does not equal gross annual income
     * or if net annual income does not equal gross annual income minus all deductions
     */ 
    public TaxBreakdown(BigDecimal grossAnnual,
        BigDecimal incomeTax,
        BigDecimal nationalInsurance,
        BigDecimal studentLoanRepayment,
        BigDecimal netAnnual,
        BigDecimal totalDeductions,
        BigDecimal pensionContribution,
        BigDecimal salarySacrifice,
        BigDecimal giftAid,
        BigDecimal professionalSubscriptions,
        BigDecimal otherPreTaxDeductions,
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
            Objects.requireNonNull(salarySacrifice, "Salary sacrifice cannot be null");
            Objects.requireNonNull(giftAid, "Gift aid cannot be null");
            Objects.requireNonNull(professionalSubscriptions, "Professional subscriptions cannot be null");
            Objects.requireNonNull(otherPreTaxDeductions, "Other pre-tax deductions cannot be null");
            Objects.requireNonNull(taxableIncome, "Taxable income cannot be null");
            Objects.requireNonNull(taxFreeAllowance, "Tax free allowance cannot be null");
            Objects.requireNonNull(taxFreeAllowanceRemaining, "Tax free allowance remaining cannot be null");
            Objects.requireNonNull(effectiveTaxRate, "Effective tax rate cannot be null");

            if (grossAnnual.compareTo(BigDecimal.ZERO) <= 0)
                throw new IllegalArgumentException("Gross annual income must be positive");
            if (incomeTax.compareTo(BigDecimal.ZERO) < 0)
                throw new IllegalArgumentException("Income tax cannot be negative");
            if (nationalInsurance.compareTo(BigDecimal.ZERO) < 0)
                throw new IllegalArgumentException("National insurance cannot be negative");
            if (studentLoanRepayment.compareTo(BigDecimal.ZERO) < 0)
                throw new IllegalArgumentException("Student loan repayment cannot be negative");
            if (netAnnual.compareTo(BigDecimal.ZERO) < 0)
                throw new IllegalArgumentException("Net annual income cannot be negative");
            if (pensionContribution.compareTo(BigDecimal.ZERO) < 0)
                throw new IllegalArgumentException("Pension contribution cannot be negative");
            if (salarySacrifice.compareTo(BigDecimal.ZERO) < 0)
                throw new IllegalArgumentException("Salary sacrifice cannot be negative");
            if (giftAid.compareTo(BigDecimal.ZERO) < 0)
                throw new IllegalArgumentException("Gift aid cannot be negative");
            if (professionalSubscriptions.compareTo(BigDecimal.ZERO) < 0)
                throw new IllegalArgumentException("Professional subscriptions cannot be negative");
            if (otherPreTaxDeductions.compareTo(BigDecimal.ZERO) < 0)
                throw new IllegalArgumentException("Other pre-tax deductions cannot be negative");
            if (totalDeductions.compareTo(grossAnnual) > 0)
                throw new IllegalArgumentException("Total deductions cannot exceed gross annual income");
            if (effectiveTaxRate.compareTo(BigDecimal.ZERO) < 0 || effectiveTaxRate.compareTo(BigDecimal.ONE) > 0)
                throw new IllegalArgumentException("Effective tax rate must be between 0 and 1");
            if (taxFreeAllowance.compareTo(BigDecimal.ZERO) < 0)
                throw new IllegalArgumentException("Tax free allowance cannot be negative");
            if (taxFreeAllowanceRemaining.compareTo(BigDecimal.ZERO) < 0)
                throw new IllegalArgumentException("Tax free allowance remaining cannot be negative");

            BigDecimal computedPreTax = pensionContribution
                .add(salarySacrifice)
                .add(giftAid)
                .add(professionalSubscriptions)
                .add(otherPreTaxDeductions);

            if (incomeTax.add(nationalInsurance)
                    .add(studentLoanRepayment)
                    .add(computedPreTax)
                    .add(netAnnual)
                    .compareTo(grossAnnual) != 0)
                throw new IllegalArgumentException("The sum of deductions and net annual income must equal gross annual income");

            if (grossAnnual.subtract(incomeTax)
                    .subtract(nationalInsurance)
                    .subtract(studentLoanRepayment)
                    .subtract(computedPreTax)
                    .compareTo(netAnnual) != 0)
                throw new IllegalArgumentException("Net annual income must equal gross annual income minus all deductions");

            this.grossAnnual = MoneyUtils.scale(grossAnnual);
            this.basictax = MoneyUtils.scale(incomeTax);
            this.nationalInsurance = MoneyUtils.scale(nationalInsurance);
            this.studentLoanRepayment = MoneyUtils.scale(studentLoanRepayment);
            this.netAnnual = MoneyUtils.scale(netAnnual);
            this.pensionContribution = MoneyUtils.scale(pensionContribution);
            this.salarySacrifice = MoneyUtils.scale(salarySacrifice);
            this.giftAid = MoneyUtils.scale(giftAid);
            this.professionalSubscriptions = MoneyUtils.scale(professionalSubscriptions);
            this.otherPreTaxDeductions = MoneyUtils.scale(otherPreTaxDeductions);
            this.totalPreTaxDeductions = MoneyUtils.scale(computedPreTax);
            this.totalDeductions = MoneyUtils.scale(totalDeductions);
            this.taxableIncome = MoneyUtils.scale(taxableIncome);
            this.taxFreeAllowance = MoneyUtils.scale(taxFreeAllowance);
            this.taxFreeAllowanceUsed = MoneyUtils.scale(taxFreeAllowance.subtract(taxFreeAllowanceRemaining));
            this.taxFreeAllowanceRemaining = MoneyUtils.scale(taxFreeAllowanceRemaining);
            this.effectiveTaxRate = MoneyUtils.scale(effectiveTaxRate);
    }

    public BigDecimal getGrossAnnual() { return grossAnnual; }
    public BigDecimal getBasicTax() { return basictax; }
    public BigDecimal getNationalInsurance() { return nationalInsurance; }
    public BigDecimal getStudentLoanRepayment() { return studentLoanRepayment; }
    public BigDecimal getNetAnnual() { return netAnnual; }
    public BigDecimal getPensionContribution() { return pensionContribution; }
    public BigDecimal getSalarySacrifice() { return salarySacrifice; }
    public BigDecimal getGiftAid() { return giftAid; }
    public BigDecimal getProfessionalSubscriptions() { return professionalSubscriptions; }
    public BigDecimal getOtherPreTaxDeductions() { return otherPreTaxDeductions; }
    public BigDecimal getTotalPreTaxDeductions() { return totalPreTaxDeductions; }
    public BigDecimal getTaxableIncome() { return taxableIncome; }
    public BigDecimal getTaxFreeAllowance() { return taxFreeAllowance; }
    public BigDecimal getTaxFreeAllowanceUsed() { return taxFreeAllowanceUsed; }
    public BigDecimal getTaxFreeAllowanceRemaining() { return taxFreeAllowanceRemaining; }
    public BigDecimal getEffectiveTaxRate() { return effectiveTaxRate; }
    public BigDecimal getTotalDeductions() { return totalDeductions; }
}