package salary.calculator.api;

import java.math.BigDecimal;
import java.util.List;

public class AppRequest {
    private BigDecimal grossSalary;
    private String taxYear; //format "2023/24"
    private String studentLoanPlan;
    private List<DeductionRequest> deductions;
    private BigDecimal hoursPerWeek;
    private Integer workingDaysPerWeek;
    private String payPeriod;

    public static class DeductionRequest {
        private String label;
        private String type;   // PENSION, SALARY_SACRIFICE, GIFT_AID, etc.
        private String mode;   // FIXED or PERCENTAGE
        private BigDecimal value;

        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getMode() { return mode; }
        public void setMode(String mode) { this.mode = mode; }
        public BigDecimal getValue() { return value; }
        public void setValue(BigDecimal value) { this.value = value; }
    }

    public BigDecimal getGrossSalary() { return grossSalary; }
    public void setGrossSalary(BigDecimal grossSalary) { this.grossSalary = grossSalary; }
    public String getTaxYear() { return taxYear; } // Getter for taxYear
    public void setTaxYear(String taxYear) { this.taxYear = taxYear; }
    public String getStudentLoanPlan() { return studentLoanPlan; }
    public void setStudentLoanPlan(String studentLoanPlan) { this.studentLoanPlan = studentLoanPlan; }
    public List<DeductionRequest> getDeductions() { return deductions; }
    public void setDeductions(List<DeductionRequest> deductions) { this.deductions = deductions; }
    public BigDecimal getHoursPerWeek() { return hoursPerWeek; }
    public void setHoursPerWeek(BigDecimal hoursPerWeek) { this.hoursPerWeek = hoursPerWeek; }
    public Integer getWorkingDaysPerWeek() { return workingDaysPerWeek; }
    public void setWorkingDaysPerWeek(Integer workingDaysPerWeek) { this.workingDaysPerWeek = workingDaysPerWeek; }
    public String getPayPeriod() { return payPeriod; }
    public void setPayPeriod(String payPeriod) { this.payPeriod = payPeriod; }
}
