import java.math.BigDecimal;
import java.util.Scanner;

import model.IncomeProfile;
import model.StudentLoanPlan;
import model.TaxBreakdown;
import tax.TaxYear;
import tax.UKTaxStrategy;

public class SalaryCalculatorCLI {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== UK Salary Calculator (2026/27) ===");

        System.out.print("Enter gross annual salary (£): ");
        BigDecimal gross = new BigDecimal(scanner.nextLine().trim());

        System.out.print("Enter pre-tax deductions e.g. pension (£, or 0): ");
        BigDecimal preTax = new BigDecimal(scanner.nextLine().trim());

        System.out.println("Select student loan plan:");
        for (StudentLoanPlan plan : StudentLoanPlan.values()) {
            System.out.println("  " + plan.name());
        }
        System.out.print("Enter plan: ");
        StudentLoanPlan plan = StudentLoanPlan.valueOf(scanner.nextLine().trim().toUpperCase());

        IncomeProfile profile = new IncomeProfile(gross, preTax, plan, TaxYear.uk2026());
        UKTaxStrategy strategy = new UKTaxStrategy(TaxYear.uk2026());
        TaxBreakdown breakdown = strategy.calculateTax(profile);

        System.out.println("\n=== Tax Breakdown ===");
        System.out.printf("Gross Annual:            £%,.2f%n", breakdown.getGrossAnnual());
        System.out.printf("Tax-Free Allowance:      £%,.2f%n", breakdown.getTaxFreeAllowance());
        System.out.printf("Taxable Income:          £%,.2f%n", breakdown.getTaxableIncome());
        System.out.printf("Income Tax:              £%,.2f%n", breakdown.getBasicTax());
        System.out.printf("National Insurance:      £%,.2f%n", breakdown.getNationalInsurance());
        System.out.printf("Student Loan:            £%,.2f%n", breakdown.getStudentLoanRepayment());
        System.out.printf("Pension/Pre-Tax Deduct:  £%,.2f%n", breakdown.getPensionContribution());
        System.out.printf("Total Deductions:        £%,.2f%n", breakdown.getTotalDeductions());
        System.out.printf("Net Annual:              £%,.2f%n", breakdown.getNetAnnual());
        System.out.printf("Net Monthly:             £%,.2f%n", breakdown.getNetAnnual().divide(BigDecimal.valueOf(12), 2, java.math.RoundingMode.HALF_UP));
        System.out.printf("Effective Tax Rate:      %.2f%%%n", breakdown.getEffectiveTaxRate().multiply(BigDecimal.valueOf(100)));

        scanner.close();
    }
}