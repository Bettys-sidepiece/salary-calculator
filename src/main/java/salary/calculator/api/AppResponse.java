package salary.calculator.api;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

public class AppResponse {

    // ── Annual figures ───────────────────────────────
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal grossAnnual;
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal taxFreeAllowance;
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal taxableIncome;
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal incomeTax;
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal nationalInsurance;
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal studentLoanRepayment;
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal pensionContribution;
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal salarySacrifice;
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal giftAid;
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal professionalSubscription;
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal other;
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal totalPreTaxDeductions;
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal totalDeductions;
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal netAnnual;
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal effectiveTaxRate;

    // ── Pay period figures ─────────────────────────────
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal grossPerPeriod;
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal netPerPeriod;
    @JsonSerialize(using = ToStringSerializer.class)
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

    public BigDecimal getSalarySacrifice() { return salarySacrifice; }
    public void setSalarySacrifice(BigDecimal salarySacrifice) { this.salarySacrifice = salarySacrifice; }

    public BigDecimal getGiftAid() { return giftAid; }
    public void setGiftAid(BigDecimal giftAid) { this.giftAid = giftAid; }

    public BigDecimal getProfessionalSubscription() { return professionalSubscription; }
    public void setProfessionalSubscription(BigDecimal professionalSubscription) { this.professionalSubscription = professionalSubscription; }

    public BigDecimal getOther() { return other; }
    public void setOther(BigDecimal other) { this.other = other; }

    public BigDecimal getTotalPreTaxDeductions() { return totalPreTaxDeductions; }
    public void setTotalPreTaxDeductions(BigDecimal totalPreTaxDeductions) { this.totalPreTaxDeductions = totalPreTaxDeductions; }

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
