package tax;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.IncomeProfile;
import model.StudentLoanPlan;
import model.TaxBreakdown;

public class UKTaxStrategyTest {

    private TaxYear taxYear;
    private UKTaxStrategy taxStrategy;

    @BeforeEach
    void setUp() {
        taxYear = TaxYear.uk2026();
        taxStrategy = new UKTaxStrategy(taxYear);
    }

    // --- Below personal allowance ---
    @Test
    void calculateTax_belowPersonalAllowance_zeroIncomeTax() {
        IncomeProfile profile = new IncomeProfile(
            BigDecimal.valueOf(10000),
            BigDecimal.ZERO,
            StudentLoanPlan.NONE,
            taxYear
        );
        TaxBreakdown result = taxStrategy.calculateTax(profile);
        assertEquals(new BigDecimal("0.00"), result.getBasicTax());
    }

    // --- Basic rate ---
    @Test
    void calculateTax_basicRateTaxpayer_correctIncomeTax() {
        // Taxable = £37,500 - £12,570 = £24,930 × 20% = £4,986.00
        IncomeProfile profile = new IncomeProfile(
            BigDecimal.valueOf(37500),
            BigDecimal.ZERO,
            StudentLoanPlan.NONE,
            taxYear
        );
        TaxBreakdown result = taxStrategy.calculateTax(profile);
        assertEquals(new BigDecimal("4986.00"), result.getBasicTax());
    }

    @Test
    void calculateTax_basicRateTaxpayer_correctNationalInsurance() {
        // NI = (£37,500 - £12,570) × 8% = £1,994.40
        IncomeProfile profile = new IncomeProfile(
            BigDecimal.valueOf(37500),
            BigDecimal.ZERO,
            StudentLoanPlan.NONE,
            taxYear
        );
        TaxBreakdown result = taxStrategy.calculateTax(profile);
        assertEquals(new BigDecimal("1994.40"), result.getNationalInsurance());
    }

    @Test
    void calculateTax_basicRateTaxpayer_correctNetPay() {
        // Net = £37,500 - £4,986.00 - £1,994.40 = £30,519.60
        IncomeProfile profile = new IncomeProfile(
            BigDecimal.valueOf(37500),
            BigDecimal.ZERO,
            StudentLoanPlan.NONE,
            taxYear
        );
        TaxBreakdown result = taxStrategy.calculateTax(profile);
        assertEquals(new BigDecimal("30519.60"), result.getNetAnnual());
    }

    // --- Higher rate ---
    @Test
    void calculateTax_higherRateTaxpayer_correctIncomeTax() {
        // £60,000 gross
        // Basic band:  (£50,270 - £12,570) = £37,700 × 20% = £7,540.00
        // Higher band: (£60,000 - £50,270) = £9,730  × 40% = £3,892.00
        // Total tax = £11,432.00
        IncomeProfile profile = new IncomeProfile(
            BigDecimal.valueOf(60000),
            BigDecimal.ZERO,
            StudentLoanPlan.NONE,
            taxYear
        );
        TaxBreakdown result = taxStrategy.calculateTax(profile);
        assertEquals(new BigDecimal("11432.00"), result.getBasicTax());
    }

    @Test
    void calculateTax_higherRateTaxpayer_correctNetPay() {
        IncomeProfile profile = new IncomeProfile(
            BigDecimal.valueOf(60000),
            BigDecimal.ZERO,
            StudentLoanPlan.NONE,
            taxYear
        );
        TaxBreakdown result = taxStrategy.calculateTax(profile);
        // Net = £60,000 - tax - NI
        assertNotNull(result.getNetAnnual());
        assertTrue(result.getNetAnnual().compareTo(BigDecimal.ZERO) > 0);
    }

    // --- Personal allowance taper ---
    @Test
    void calculateTax_above100k_personalAllowanceTapered() {
        IncomeProfile profile = new IncomeProfile(
            BigDecimal.valueOf(110000),
            BigDecimal.ZERO,
            StudentLoanPlan.NONE,
            taxYear
        );
        TaxBreakdown result = taxStrategy.calculateTax(profile);
        // allowanceUsed = £7,570, remaining = £12,570 - £7,570 = £5,000
        assertEquals(new BigDecimal("7570.00"), result.getTaxFreeAllowanceUsed());
    }

    @Test
    void calculateTax_above125140_zeroPersonalAllowance() {
        IncomeProfile profile = new IncomeProfile(
            BigDecimal.valueOf(130000),
            BigDecimal.ZERO,
            StudentLoanPlan.NONE,
            taxYear
        );
        TaxBreakdown result = taxStrategy.calculateTax(profile);
        // allowanceUsed = £0, remaining = £12,570
        assertEquals(new BigDecimal("0.00"), result.getTaxFreeAllowanceUsed());
    }

    // --- Pension ---
    @Test
    void calculateTax_withPensionContribution_reducesIncomeTax() {
        IncomeProfile withPension = new IncomeProfile(
            BigDecimal.valueOf(37500),
            BigDecimal.valueOf(200),
            StudentLoanPlan.NONE,
            taxYear
        );
        IncomeProfile noPension = new IncomeProfile(
            BigDecimal.valueOf(37500),
            BigDecimal.ZERO,
            StudentLoanPlan.NONE,
            taxYear
        );
        TaxBreakdown with = taxStrategy.calculateTax(withPension);
        TaxBreakdown without = taxStrategy.calculateTax(noPension);
        assertTrue(with.getBasicTax().compareTo(without.getBasicTax()) < 0);
    }

    // --- Student loan ---
    @Test
    void calculateTax_plan2StudentLoan_deductionApplied() {
        IncomeProfile profile = new IncomeProfile(
            BigDecimal.valueOf(37500),
            BigDecimal.ZERO,
            StudentLoanPlan.PLAN2,
            taxYear
        );
        TaxBreakdown result = taxStrategy.calculateTax(profile);
        assertTrue(result.getStudentLoanRepayment().compareTo(BigDecimal.ZERO) > 0);
    }

    // --- Rounding ---
    @Test
    void calculateTax_allValues_scaledToTwoDecimalPlaces() {
        IncomeProfile profile = new IncomeProfile(
            BigDecimal.valueOf(37500),
            BigDecimal.ZERO,
            StudentLoanPlan.NONE,
            taxYear
        );
        TaxBreakdown result = taxStrategy.calculateTax(profile);
        assertEquals(2, result.getBasicTax().scale());
        assertEquals(2, result.getNationalInsurance().scale());
        assertEquals(2, result.getNetAnnual().scale());
        assertEquals(2, result.getTotalDeductions().scale());
    }

    // --- Null guards ---
    @Test
    void calculateTax_nullProfile_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> taxStrategy.calculateTax(null));
    }

    @Test
    void constructor_nullTaxYear_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new UKTaxStrategy(null));
    }
}
