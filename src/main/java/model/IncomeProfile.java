package model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import tax.TaxYear;
import util.MoneyUtils;

public final class IncomeProfile {

    private final BigDecimal grossAnnual;
    private final List<PreTaxDeduction> deductions;
    private final StudentLoanPlan studentLoanPlan;
    private final TaxYear taxYear;

    /**
     * Creates an immutable IncomeProfile representing an individual's income situation for tax calculations.
     * This includes the gross annual income, any pre-tax deductions, the applicable student loan plan, and the tax year for which the calculations will be performed.
     * The constructor validates that the gross annual income is positive, that the total pre-tax deductions do not exceed the gross income, and
     * that none of the parameters are null.
     * @param grossAnnual - the gross annual income (must be positive)
     * @param deductions - a list of pre-tax deductions (cannot be null, total must not exceed gross income)
     * @param studentLoanPlan - the applicable student loan plan (cannot be null)
     * @param taxYear - the tax year for which the calculations will be performed (cannot be null)
     * @throws NullPointerException if any of the parameters are null
     * @throws IllegalArgumentException if grossAnnual is not positive or if total deductions exceed gross
     */
    public IncomeProfile(BigDecimal grossAnnual,
        List<PreTaxDeduction> deductions,
        StudentLoanPlan studentLoanPlan,
        TaxYear taxYear) {

            Objects.requireNonNull(grossAnnual, "Gross annual income cannot be null");
            Objects.requireNonNull(deductions, "Deductions cannot be null");
            Objects.requireNonNull(studentLoanPlan, "Student loan plan cannot be null");
            Objects.requireNonNull(taxYear, "Tax year cannot be null");

            if (grossAnnual.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Gross annual income must be positive");
            }

            BigDecimal totalDeductions = deductions.stream()
                .map(d -> d.resolve(grossAnnual))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (totalDeductions.compareTo(grossAnnual) > 0) {
                throw new IllegalArgumentException("Total pre-tax deductions cannot exceed gross annual income");
            }

            this.grossAnnual = MoneyUtils.scale(grossAnnual);
            this.deductions = Collections.unmodifiableList(new ArrayList<>(deductions));
            this.studentLoanPlan = studentLoanPlan;
            this.taxYear = taxYear;
    }

    /** Convenience constructor for no deductions 
     * @param grossAnnual - the gross annual income (must be positive)
     * @param studentLoanPlan - the applicable student loan plan (cannot be null)
     * @param taxYear - the tax year for which the calculations will be performed (cannot be null)
     * @throws NullPointerException if any of the parameters are null
     * @throws IllegalArgumentException if grossAnnual is not positive
     * @see #IncomeProfile(BigDecimal, List, StudentLoanPlan, TaxYear)
    */
    public IncomeProfile(BigDecimal grossAnnual,
        StudentLoanPlan studentLoanPlan,
        TaxYear taxYear) {
        this(grossAnnual, Collections.emptyList(), studentLoanPlan, taxYear);
    }

    /** 
     * Resolves total pre-tax deduction amount 
     * @return the total pre-tax deductions
     */
    public BigDecimal getPreTaxDeductions() {
        return deductions.stream()
            .map(d -> d.resolve(grossAnnual))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getGrossAnnual() { return grossAnnual; }
    public List<PreTaxDeduction> getDeductions() { return deductions; }
    public StudentLoanPlan getStudentLoanPlan() { return studentLoanPlan; }
    public TaxYear getTaxYear() { return taxYear; }
}
