package tax;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.IncomeProfile;
import model.PreTaxDeduction;
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

    // --- Helper ---
    private IncomeProfile profile(double gross) {
        return new IncomeProfile(BigDecimal.valueOf(gross), StudentLoanPlan.NONE, taxYear);
    }

    private IncomeProfile profileWithPension(double gross, double pensionFixed) {
        List<PreTaxDeduction> deductions = List.of(new PreTaxDeduction(
            "Pension",
            PreTaxDeduction.Type.PENSION,
            PreTaxDeduction.Mode.FIXED,
            BigDecimal.valueOf(pensionFixed)
        ));
        return new IncomeProfile(BigDecimal.valueOf(gross), deductions, StudentLoanPlan.NONE, taxYear);
    }

    private IncomeProfile profileWithPlan(double gross, StudentLoanPlan plan) {
        return new IncomeProfile(BigDecimal.valueOf(gross), Collections.emptyList(), plan, taxYear);
    }

    private IncomeProfile profileWithPensionPercentage(double gross, double pensionPct) {
        List<PreTaxDeduction> deductions = List.of(new PreTaxDeduction(
            "Pension",
            PreTaxDeduction.Type.PENSION,
            PreTaxDeduction.Mode.PERCENTAGE,
            BigDecimal.valueOf(pensionPct).divide(BigDecimal.valueOf(100))
        ));
        return new IncomeProfile(BigDecimal.valueOf(gross), deductions, StudentLoanPlan.NONE, taxYear);
    }

    private IncomeProfile profileWithPreTaxDeduction(double gross, PreTaxDeduction deduction) {
        List<PreTaxDeduction> deductions = List.of(deduction);
        return new IncomeProfile(BigDecimal.valueOf(gross), deductions, StudentLoanPlan.NONE, taxYear);
    }

    private IncomeProfile profileWithSalarySacrifice(double gross, double sacrificeAmount) {
        List<PreTaxDeduction> deductions = List.of(new PreTaxDeduction(
            "Salary Sacrifice",
            PreTaxDeduction.Type.SALARY_SACRIFICE,
            PreTaxDeduction.Mode.FIXED,
            BigDecimal.valueOf(sacrificeAmount)
        ));
        return new IncomeProfile(BigDecimal.valueOf(gross), deductions, StudentLoanPlan.NONE, taxYear);
    }

    private IncomeProfile profileWithMultiplePreTaxDeductions(double gross, List<PreTaxDeduction> deductions) {
        return new IncomeProfile(BigDecimal.valueOf(gross), deductions, StudentLoanPlan.NONE, taxYear);
    }

    // --- Below personal allowance ---
    @Test
    void calculateTax_belowPersonalAllowance_zeroIncomeTax() {
        TaxBreakdown result = taxStrategy.calculateTax(profile(10000));
        assertEquals(new BigDecimal("0.00"), result.getBasicTax());
    }

    // --- Basic rate ---
    @Test
    void calculateTax_basicRateTaxpayer_correctIncomeTax() {
        TaxBreakdown result = taxStrategy.calculateTax(profile(37500));
        assertEquals(new BigDecimal("4986.00"), result.getBasicTax());
    }

    @Test
    void calculateTax_basicRateTaxpayer_correctNationalInsurance() {
        TaxBreakdown result = taxStrategy.calculateTax(profile(37500));
        assertEquals(new BigDecimal("1994.40"), result.getNationalInsurance());
    }

    @Test
    void calculateTax_basicRateTaxpayer_correctNetPay() {
        TaxBreakdown result = taxStrategy.calculateTax(profile(37500));
        assertEquals(new BigDecimal("30519.60"), result.getNetAnnual());
    }

    // --- Higher rate ---
    @Test
    void calculateTax_higherRateTaxpayer_correctIncomeTax() {
        TaxBreakdown result = taxStrategy.calculateTax(profile(60000));
        assertEquals(new BigDecimal("11432.00"), result.getBasicTax());
    }

    @Test
    void calculateTax_higherRateTaxpayer_correctNetPay() {
        TaxBreakdown result = taxStrategy.calculateTax(profile(60000));
        assertNotNull(result.getNetAnnual());
        assertTrue(result.getNetAnnual().compareTo(BigDecimal.ZERO) > 0);
    }

    // --- Personal allowance taper ---
    @Test
    void calculateTax_above100k_personalAllowanceTapered() {
        TaxBreakdown result = taxStrategy.calculateTax(profile(110000));
        assertEquals(new BigDecimal("7570.00"), result.getTaxFreeAllowanceUsed());
    }

    @Test
    void calculateTax_above125140_zeroPersonalAllowance() {
        TaxBreakdown result = taxStrategy.calculateTax(profile(130000));
        assertEquals(new BigDecimal("0.00"), result.getTaxFreeAllowanceUsed());
    }

    // --- Pension ---
    @Test
    void calculateTax_withPensionContribution_reducesIncomeTax() {
        TaxBreakdown with = taxStrategy.calculateTax(profileWithPension(37500, 200));
        TaxBreakdown without = taxStrategy.calculateTax(profile(37500));
        assertTrue(with.getBasicTax().compareTo(without.getBasicTax()) < 0);
    }

    // --- Student loan ---
    @Test
    void calculateTax_plan2StudentLoan_deductionApplied() {
        TaxBreakdown result = taxStrategy.calculateTax(profileWithPlan(37500, StudentLoanPlan.PLAN2));
        assertTrue(result.getStudentLoanRepayment().compareTo(BigDecimal.ZERO) > 0);
    }

    // --- Rounding ---
    @Test
    void calculateTax_allValues_scaledToTwoDecimalPlaces() {
        TaxBreakdown result = taxStrategy.calculateTax(profile(37500));
        assertEquals(2, result.getBasicTax().scale());
        assertEquals(2, result.getNationalInsurance().scale());
        assertEquals(2, result.getNetAnnual().scale());
        assertEquals(2, result.getTotalDeductions().scale());
    }

    //--- pre-tax deductions ---
    @Test
    void calculateTax_withPreTaxDeductions_as_Percentage() {
        // 5% pension on £37,500 = £1,875 deduction → taxable income = £35,625
        TaxBreakdown with = taxStrategy.calculateTax(profileWithPensionPercentage(37500, 5));
        TaxBreakdown without = taxStrategy.calculateTax(profile(37500));
        assertTrue(with.getBasicTax().compareTo(without.getBasicTax()) < 0);
        assertEquals(new BigDecimal("1875.00"), with.getPensionContribution());
    }

    @Test
    void calculateTax_withPreTaxDeductions_as_FixedAmount() {
        // £500 fixed pension → taxable income reduced by £500
        TaxBreakdown with = taxStrategy.calculateTax(profileWithPension(37500, 500));
        TaxBreakdown without = taxStrategy.calculateTax(profile(37500));
        assertTrue(with.getBasicTax().compareTo(without.getBasicTax()) < 0);
        assertEquals(new BigDecimal("500.00"), with.getPensionContribution());
    }

    @Test
    void calculateTax_withPreTaxDeductions_exceedingGross() {
        // deductions exceeding gross should throw
        assertThrows(IllegalArgumentException.class, () ->
            profileWithPension(37500, 40000)
        );
    }

    @Test
    void calculateTax_withPreTaxDeductions_negativeValue() {
        // negative deduction value should throw
        assertThrows(IllegalArgumentException.class, () ->
            new PreTaxDeduction("Pension", PreTaxDeduction.Type.PENSION,
                PreTaxDeduction.Mode.FIXED, BigDecimal.valueOf(-100))
        );
    }

    @Test
    void calculateTax_withPreTaxDeductions_as_SalarySacrifice() {
        TaxBreakdown with = taxStrategy.calculateTax(profileWithSalarySacrifice(37500, 1200));
        TaxBreakdown without = taxStrategy.calculateTax(profile(37500));
        assertTrue(with.getBasicTax().compareTo(without.getBasicTax()) < 0);
    }

    @Test
    void calculateTax_withPreTaxDeductions_as_GiftAid() {
        TaxBreakdown result = taxStrategy.calculateTax(profileWithPreTaxDeduction(37500,
            new PreTaxDeduction("Gift Aid", PreTaxDeduction.Type.GIFT_AID,
                PreTaxDeduction.Mode.FIXED, BigDecimal.valueOf(300))
        ));
        TaxBreakdown without = taxStrategy.calculateTax(profile(37500));
        assertTrue(result.getBasicTax().compareTo(without.getBasicTax()) < 0);
    }

    @Test
    void calculateTax_withPreTaxDeductions_as_ProfessionalSubscription() {
        TaxBreakdown result = taxStrategy.calculateTax(profileWithPreTaxDeduction(37500,
            new PreTaxDeduction("CIPD", PreTaxDeduction.Type.PROFESSIONAL_SUBSCRIPTION,
                PreTaxDeduction.Mode.FIXED, BigDecimal.valueOf(150))
        ));
        TaxBreakdown without = taxStrategy.calculateTax(profile(37500));
        assertTrue(result.getBasicTax().compareTo(without.getBasicTax()) < 0);
    }

    @Test
    void calculateTax_withPreTaxDeductions_as_Other() {
        TaxBreakdown result = taxStrategy.calculateTax(profileWithPreTaxDeduction(37500,
            new PreTaxDeduction("Cycle to Work", PreTaxDeduction.Type.OTHER,
                PreTaxDeduction.Mode.FIXED, BigDecimal.valueOf(500))
        ));
        TaxBreakdown without = taxStrategy.calculateTax(profile(37500));
        assertTrue(result.getBasicTax().compareTo(without.getBasicTax()) < 0);
    }

    @Test
    void calculateTax_withMultiplePreTaxDeductions() {
        // Pension 5% = £1,875 + Salary Sacrifice £1,200 + Gift Aid £300 = £3,375 total
        List<PreTaxDeduction> deductions = List.of(
            new PreTaxDeduction("Pension", PreTaxDeduction.Type.PENSION,
                PreTaxDeduction.Mode.PERCENTAGE, new BigDecimal("0.05")),
            new PreTaxDeduction("Salary Sacrifice", PreTaxDeduction.Type.SALARY_SACRIFICE,
                PreTaxDeduction.Mode.FIXED, BigDecimal.valueOf(1200)),
            new PreTaxDeduction("Gift Aid", PreTaxDeduction.Type.GIFT_AID,
                PreTaxDeduction.Mode.FIXED, BigDecimal.valueOf(300))
        );
        TaxBreakdown with = taxStrategy.calculateTax(profileWithMultiplePreTaxDeductions(37500, deductions));
        TaxBreakdown without = taxStrategy.calculateTax(profile(37500));
        assertTrue(with.getBasicTax().compareTo(without.getBasicTax()) < 0);
        assertEquals(new BigDecimal("3375.00"), with.getTotalPreTaxDeductions());
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
