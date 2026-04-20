package salary.calculator.api;

import java.math.BigDecimal;

public class AppResponse {

    // ── Annual figures ───────────────────────────────
    private BigDecimal grossAnnual;
    private BigDecimal taxFreeAllowance;
    private BigDecimal taxableIncome;
    private BigDecimal incomeTax;
    private BigDecimal nationalInsurance;
    private BigDecimal studentLoanRepayment;
    private BigDecimal pensionContribution;
    private BigDecimal totalDeductions;
    private BigDecimal netAnnual;
    private BigDecimal effectiveTaxRate;

    // ── Pay period figures ─────────────────────────────
    private BigDecimal grossPerPeriod;
    private BigDecimal netPerPeriod;
    private String payPeriod;

    // ── Getters and Setters ─────────────────────────────
    public BigDecimal getGrossAnnual() { return grossAnnual; }
    public void setGrossAnnual(BigDecimal grossAnnual) { this.grossAnnual = grossAnnual; }

    public BigDecimal getTaxFreeAllowance() { return taxFreeAllowance; }
    public void setTaxFreeAllowance(BigDecimal taxFreeAllowance) { this.taxFreeAllowance = taxFreeAllowance; }

    public BigDecimal getTaxableIncome() { return taxableIncome; }
    public void setTaxableIncome(BigDecimal taxableIncome) { this.taxableIncome = taxableIncome; }

    public BigDecimal getIncomeTax() { return incomeTax; }
    public void setIncomeTax(BigDecimal incomeTax) { this.incomeTax = incomeTax; }

    public BigDecimal getNationalInsurance() { return nationalInsurance; }
    public void setNationalInsurance(BigDecimal nationalInsurance) { this.nationalInsurance = nationalInsurance; }

    public BigDecimal getStudentLoanRepayment() { return studentLoanRepayment; }
    public void setStudentLoanRepayment(BigDecimal studentLoanRepayment) { this.studentLoanRepayment = studentLoanRepayment; }

    public BigDecimal getPensionContribution() { return pensionContribution; }
    public void setPensionContribution(BigDecimal pensionContribution) { this.pensionContribution = pensionContribution; }

    public BigDecimal getTotalDeductions() { return totalDeductions; }
    public void setTotalDeductions(BigDecimal totalDeductions) { this.totalDeductions = totalDeductions; }

    public BigDecimal getNetAnnual() { return netAnnual; }
    public void setNetAnnual(BigDecimal netAnnual) { this.netAnnual = netAnnual; }

    public BigDecimal getEffectiveTaxRate() { return effectiveTaxRate; }
    public void setEffectiveTaxRate(BigDecimal effectiveTaxRate) { this.effectiveTaxRate = effectiveTaxRate; }
    
    public BigDecimal getGrossPerPeriod() { return grossPerPeriod; }
    public void setGrossPerPeriod(BigDecimal grossPerPeriod) { this.grossPerPeriod = grossPerPeriod; }

    public BigDecimal getNetPerPeriod() { return netPerPeriod; }
    public void setNetPerPeriod(BigDecimal netPerPeriod) { this.netPerPeriod = netPerPeriod; }

    public String getPayPeriod() { return payPeriod; }
    public void setPayPeriod(String payPeriod) { this.payPeriod = payPeriod; }
}
