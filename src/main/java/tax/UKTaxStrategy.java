/**
 * UKTaxStrategy implements the TaxStrategy interface to calculate tax breakdown for UK income profiles.
 * Handles the £100k allowance withdrawal threshold and progressive tax rates.
 */
package tax;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.IncomeProfile;
import model.TaxBreakdown;
import util.MoneyUtils;

public class UKTaxStrategy implements TaxStrategy {
    private static final Logger logger = LoggerFactory.getLogger(UKTaxStrategy.class);
    private final TaxYear taxYear;

    public UKTaxStrategy(TaxYear taxYear) {
        this.taxYear = Objects.requireNonNull(taxYear, "Tax year cannot be null");
    }

    @Override
    /**
     * Calculates the tax breakdown for a given income profile based on the UK tax rules.
     * This includes calculating the personal allowance (with withdrawal), income tax, national insurance, and
     * student loan deductions.
     * @param profile The income profile containing salary and deduction information.
     * @return The tax breakdown for the given income profile.
     */
    public TaxBreakdown calculateTax(IncomeProfile profile) {
        Objects.requireNonNull(profile, "Income profile cannot be null");
        
        logger.debug("Starting tax calculation for gross: {}", profile.getGrossAnnual());
        
        BigDecimal gross = profile.getGrossAnnual();
        BigDecimal preTax = profile.getPreTaxDeductions();
        BigDecimal taxableIncome = MoneyUtils.scale(gross.subtract(preTax));
        
        // Calculate allowance after withdrawal threshold
        BigDecimal allowanceUsed = calculateAllowanceAfterWithdrawal(gross);
        BigDecimal allowanceRemaining = taxYear.getPersonalAllowance().subtract(allowanceUsed);
        BigDecimal effectiveTaxableIncome = MoneyUtils.scale(
            gross.subtract(preTax).subtract(allowanceUsed));
        
        BigDecimal incomeTax = calculateIncomeTax(effectiveTaxableIncome);
        BigDecimal ni = calculateNationalInsurance(gross);
        BigDecimal studentLoan = calculateStudentLoan(gross, profile.getStudentLoanPlan());
        BigDecimal totalDeductions = MoneyUtils.scale(preTax.add(incomeTax).add(ni).add(studentLoan));
        BigDecimal netAnnual = MoneyUtils.scale(gross.subtract(totalDeductions));
        
        BigDecimal effectiveTaxRate = MoneyUtils.scale(
            incomeTax.add(ni).divide(gross, 2,RoundingMode.HALF_UP)
        );
        
        logger.info("Tax calculated - Allowance used: {}, Income tax: {}, NI: {}, Student loan: {}", 
            allowanceUsed, incomeTax, ni, studentLoan);
        
        return new TaxBreakdown(
            gross,
            incomeTax, 
            ni, 
            studentLoan, 
            netAnnual, 
            totalDeductions, 
            preTax, 
            taxableIncome, 
            taxYear.getPersonalAllowance(), 
            allowanceRemaining, 
            effectiveTaxRate);
    }
    /**
     * Calculates the personal allowance after considering the withdrawal threshold.
     * @param gross The gross annual income.
     * @return The remaining personal allowance after withdrawal.
     */
    private BigDecimal calculateAllowanceAfterWithdrawal(BigDecimal gross) {
        BigDecimal threshold = taxYear.getAllowanceWithdrawalThreshold();
        
        if (gross.compareTo(threshold) <= 0) {
            return taxYear.getPersonalAllowance();
        }
        
        BigDecimal excessIncome = gross.subtract(threshold);
        BigDecimal allowanceReduction = MoneyUtils.scale(
            excessIncome.multiply(taxYear.getAllowanceWithdrawalRate())
        );
        
        BigDecimal allowanceRemaining = taxYear.getPersonalAllowance().subtract(allowanceReduction);
        
        if (allowanceRemaining.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        
        return allowanceRemaining;
    }

    /**
     * Calculates the income tax based on the taxable income and the tax bands for the year.
     * @param taxableIncome The taxable income after deductions and allowances.
     * @return The calculated income tax.
     */
    private BigDecimal calculateIncomeTax(BigDecimal taxableIncome) {
        if (taxableIncome.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal tax = BigDecimal.ZERO;
        BigDecimal basicRateLimit = taxYear.getBasicRateLimit()
            .subtract(taxYear.getPersonalAllowance());
        BigDecimal higherRateLimit = taxYear.getHigherRateLimit()
            .subtract(taxYear.getPersonalAllowance());
        
        // Basic rate (20%)
        if (taxableIncome.compareTo(basicRateLimit) <= 0) {
            tax = MoneyUtils.scale(taxableIncome.multiply(taxYear.getBasicRate()));
        } else {
            tax = MoneyUtils.scale(basicRateLimit.multiply(taxYear.getBasicRate()));
            BigDecimal remaining = taxableIncome.subtract(basicRateLimit);
            
            // Higher rate (40%)
            if (remaining.compareTo(higherRateLimit.subtract(basicRateLimit)) <= 0) {
                tax = MoneyUtils.scale(tax.add(remaining.multiply(taxYear.getHigherRate())));
            } else {
                BigDecimal higherRateAmount = higherRateLimit.subtract(basicRateLimit);
                tax = MoneyUtils.scale(tax.add(higherRateAmount.multiply(taxYear.getHigherRate())));
                
                // Additional rate (45%)
                BigDecimal additionalAmount = taxableIncome.subtract(higherRateLimit);
                tax = MoneyUtils.scale(tax.add(additionalAmount.multiply(taxYear.getAdditionalRate())));
            }
        }
        
        return tax;
    }

    /**
     * Calculates the national insurance contribution based on the gross income.
     * @param gross The gross annual income.
     * @return The calculated national insurance contribution.
     */
    private BigDecimal calculateNationalInsurance(BigDecimal gross) {
        BigDecimal threshold = BigDecimal.valueOf(12570); // 2026/27 NI threshold
        BigDecimal afterThreshold = gross.subtract(threshold);
        
        if (afterThreshold.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        return MoneyUtils.scale(afterThreshold.multiply(BigDecimal.valueOf(0.08)));
    }
    /**
     * Calculates the student loan repayment based on the gross income and the specified student loan plan.
     * @param gross The gross annual income.
     * @param plan The student loan plan to use for calculation.
     * @return The calculated student loan repayment.
     */
    private BigDecimal calculateStudentLoan(BigDecimal gross, model.StudentLoanPlan plan) {
        BigDecimal threshold = plan.getThreshold();
        BigDecimal afterThreshold = gross.subtract(threshold);
        
        if (afterThreshold.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        return MoneyUtils.scale(afterThreshold.multiply(plan.getRate()));
    }
}
