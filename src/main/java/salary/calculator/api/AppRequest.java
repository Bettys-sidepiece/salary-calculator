package salary.calculator.api;

import java.math.BigDecimal;

public class AppRequest {
    private BigDecimal grossSalary;
    private String taxYear;
    private String studentLoanPlan;
    private BigDecimal preTaxDeductions;

    private BigDecimal hoursPerWeek;
    private Integer workingDaysPerWeek;
    private String payPeriod;

    public BigDecimal getGrossSalary() {
        return grossSalary;
    }

    public void setGrossSalary(BigDecimal grossSalary) {
        this.grossSalary = grossSalary;
    }

    public String getTaxYear() {
        return taxYear;
    }

    public void setTaxYear(String taxYear) {
        this.taxYear = taxYear;
    }

    public String getStudentLoanPlan() {
        return studentLoanPlan;
    }

    public void setStudentLoanPlan(String studentLoanPlan) {
        this.studentLoanPlan = studentLoanPlan;
    }

    public BigDecimal getPreTaxDeductions() {
        return preTaxDeductions;
    }

    public void setPreTaxDeductions(BigDecimal preTaxDeductions) {
        this.preTaxDeductions = preTaxDeductions;
    }

    public BigDecimal getHoursPerWeek() { return hoursPerWeek; }
    public void setHoursPerWeek(BigDecimal hoursPerWeek) { this.hoursPerWeek = hoursPerWeek; }

    public Integer getWorkingDaysPerWeek() { return workingDaysPerWeek; }
    public void setWorkingDaysPerWeek(Integer workingDaysPerWeek) { this.workingDaysPerWeek = workingDaysPerWeek; }

    public String getPayPeriod() { return payPeriod; }
    public void setPayPeriod(String payPeriod) { this.payPeriod = payPeriod; }
}
