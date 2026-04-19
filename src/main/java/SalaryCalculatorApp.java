import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.IncomeProfile;
import model.StudentLoanPlan;
import model.TaxBreakdown;
import tax.TaxYear;
import tax.UKTaxStrategy;

/**
 * Main entry point for the Salary Calculator application.
 * 
 * Usage: java SalaryCalculatorApp [--yearly] [--monthly] [--weekly] [--daily] [--hourly]
 * Default: shows yearly and monthly if no arguments provided
 */
public class SalaryCalculatorApp {
    private static final Logger logger = LoggerFactory.getLogger(SalaryCalculatorApp.class);
    private static final BigDecimal WEEKS_PER_YEAR = BigDecimal.valueOf(52);
    private static final BigDecimal DAYS_PER_YEAR = BigDecimal.valueOf(365);
    private static final BigDecimal HOURS_PER_YEAR = BigDecimal.valueOf(52 * 37.5); // 52 weeks × 40 hours

    public static void main(String[] args) {
        logger.info("Starting Salary Calculator Application");
        
        try {
            // Parse periods from arguments
            Set<String> periods = parsePeriods(args);
            if (periods.isEmpty()) {
                periods.add("yearly");
                periods.add("monthly");
                periods.add("weekly");
                periods.add("daily");
                periods.add("hourly");
                logger.debug("No periods specified, using defaults: yearly, monthly, weekly, daily, hourly");
            }
            
            // Create a tax year
            TaxYear taxYear = TaxYear.uk2026();
            logger.debug("Tax year loaded: 2026/27");
            
            // Create an income profile
            IncomeProfile profile = new IncomeProfile(
                BigDecimal.valueOf(37500),  // Gross annual
                BigDecimal.valueOf(125),     // Pre-tax deductions (pension)
                StudentLoanPlan.NONE,       // Student loan plan
                taxYear
            );
            logger.debug("Income profile created: £{}", profile.getGrossAnnual());
            
            // Calculate taxes using UK strategy
            UKTaxStrategy strategy = new UKTaxStrategy(taxYear);
            TaxBreakdown breakdown = strategy.calculateTax(profile);
            
            // Display results
            displayResults(breakdown, periods);
            
            logger.info("Calculation complete");
            
        } catch (Exception e) {
            logger.error("Error during calculation", e);
            System.exit(1);
        }
    }
    
    private static Set<String> parsePeriods(String[] args) {
        Set<String> periods = new HashSet<>();
        for (String arg : args) {
            switch (arg.toLowerCase()) {
                case "--yearly":
                    periods.add("yearly");
                    break;
                case "--monthly":
                    periods.add("monthly");
                    break;
                case "--weekly":
                    periods.add("weekly");
                    break;
                case "--daily":
                    periods.add("daily");
                    break;
                case "--hourly":
                    periods.add("hourly");
                    break;
            }
        }
        return periods;
    }
    
    private static void displayResults(TaxBreakdown breakdown, Set<String> periods) {
        System.out.println("\n========== SALARY BREAKDOWN ==========\n");
        
        if (periods.contains("yearly")) {
            displayPeriod(breakdown, "YEARLY", BigDecimal.ONE);
        }
        if (periods.contains("monthly")) {
            displayPeriod(breakdown, "MONTHLY", BigDecimal.valueOf(12));
        }
        if (periods.contains("weekly")) {
            displayPeriod(breakdown, "WEEKLY", WEEKS_PER_YEAR);
        }
        if (periods.contains("daily")) {
            displayPeriod(breakdown, "DAILY", DAYS_PER_YEAR);
        }
        if (periods.contains("hourly")) {
            displayPeriod(breakdown, "HOURLY", HOURS_PER_YEAR);
        }
        
        System.out.println("\n========== SUMMARY ==========");
        System.out.println(String.format("Effective Tax Rate:       %.2f%%", 
            breakdown.getEffectiveTaxRate().multiply(BigDecimal.valueOf(100))));
        System.out.println("=====================================\n");
    }
    
    private static void displayPeriod(TaxBreakdown breakdown, String periodName, BigDecimal divisor) {
        BigDecimal gross = divide(breakdown.getGrossAnnual(), divisor);
        BigDecimal incomeTax = divide(breakdown.getBasicTax(), divisor);
        BigDecimal ni = divide(breakdown.getNationalInsurance(), divisor);
        BigDecimal studentLoan = divide(breakdown.getStudentLoanRepayment(), divisor);
        BigDecimal pension = divide(breakdown.getPensionContribution(), divisor);
        BigDecimal totalDed = divide(breakdown.getTotalDeductions(), divisor);
        BigDecimal net = divide(breakdown.getNetAnnual(), divisor);
        
        System.out.println(String.format("--- %s ---", periodName));
        System.out.println(String.format("  Gross:                £%.2f", gross));
        System.out.println(String.format("  Income Tax:           £%.2f", incomeTax));
        System.out.println(String.format("  National Insurance:   £%.2f", ni));
        System.out.println(String.format("  Student Loan:         £%.2f", studentLoan));
        System.out.println(String.format("  Pension:              £%.2f", pension));
        System.out.println(String.format("  Total Deductions:     £%.2f", totalDed));
        System.out.println(String.format("  NET PAY:              £%.2f", net));
        System.out.println();
    }
    
    private static BigDecimal divide(BigDecimal value, BigDecimal divisor) {
        return value.divide(divisor, 2, RoundingMode.HALF_UP);
    }
}