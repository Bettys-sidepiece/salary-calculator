package salary.calculator.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import model.IncomeProfile;
import model.PayPeriod;
import model.PreTaxDeduction;
import model.StudentLoanPlan;
import model.TaxBreakdown;
import repo.TaxYearEntity;
import salary.calculator.api.AppRequest;
import salary.calculator.api.AppResponse;
import salary.calculator.service.TaxYearService;
import tax.TaxYear;
import tax.UKTaxStrategy;

@RestController
@RequestMapping("/api/salary")
@CrossOrigin(origins = "http://localhost:3000") // TODO: Update this in production to match your frontend URL
/**
 * Controller class for handling salary calculation requests. 
 * This class will define endpoints for retrieving tax year information
 * and performing salary calculations based on the provided input.
 */
public class AppController {
    private final TaxYearService taxYearService;

    // Constructor injection of the TaxYearService
    public AppController(TaxYearService taxYearService) {
        this.taxYearService = taxYearService;
    }

    @GetMapping("/tax-year/current")
    @CrossOrigin(origins = "http://localhost:3000")
    public Map<String, String> getCurrentTaxYear() {
        TaxYearEntity entity = taxYearService.getTaxYearEntityMostRecent();
        return Map.of("label", entity.getLabel());
    }

    @PostMapping("/calculate")
    @CrossOrigin(origins = "http://localhost:3000")
    public AppResponse calculate(@RequestBody AppRequest request) {
        // Validate input and call the service to perform calculations
        TaxYear taxYear = taxYearService.getByLabel(request.getTaxYear());
        List<PreTaxDeduction> deductions = request.getDeductions() == null
            ? Collections.emptyList()
            : request.getDeductions().stream()
                .map(d -> new PreTaxDeduction(
                    d.getLabel(),
                    PreTaxDeduction.Type.valueOf(d.getType().toUpperCase()),
                    PreTaxDeduction.Mode.valueOf(d.getMode().toUpperCase()),
                    d.getValue()
                ))
                .toList();
        
        IncomeProfile profile = new IncomeProfile(
            request.getGrossSalary(), // Passes gross salary from the request to the profile
            deductions, //Passes pre-tax deductions to the profile from the request
            StudentLoanPlan.valueOf(request.getStudentLoanPlan().toUpperCase()), // Passes student loan plan from the request to the profile, converting it to uppercase for enum matching
            taxYear // Passes the retrieved TaxYear to the profile for tax calculations
        );

        // Perform salary calculation using the retrieved TaxYear
        UKTaxStrategy strategy = new UKTaxStrategy(taxYear);
        TaxBreakdown breakdown = strategy.calculateTax(profile);

        //Pay period calculations
        PayPeriod payPeriod = PayPeriod.valueOf(request.getPayPeriod().toUpperCase());
        BigDecimal grossPerPeriod;
        BigDecimal netPerPeriod;

        if (payPeriod == PayPeriod.HOURLY) {
            BigDecimal hoursPerYear = request.getHoursPerWeek().multiply(BigDecimal.valueOf(52));
            grossPerPeriod = breakdown.getGrossAnnual().divide(hoursPerYear, 2, RoundingMode.HALF_UP);
            netPerPeriod = breakdown.getNetAnnual().divide(hoursPerYear, 2, RoundingMode.HALF_UP);
        } else if (payPeriod == PayPeriod.DAILY) {
            BigDecimal daysPerYear = BigDecimal.valueOf(request.getWorkingDaysPerWeek()).multiply(BigDecimal.valueOf(52));
            grossPerPeriod = breakdown.getGrossAnnual().divide(daysPerYear, 2, RoundingMode.HALF_UP);
            netPerPeriod = breakdown.getNetAnnual().divide(daysPerYear, 2, RoundingMode.HALF_UP);
        } else {
            grossPerPeriod = breakdown.getGrossAnnual().divide(payPeriod.getPeriodsPerYear(), 2, RoundingMode.HALF_UP);
            netPerPeriod = breakdown.getNetAnnual().divide(payPeriod.getPeriodsPerYear(), 2, RoundingMode.HALF_UP);
        }

        AppResponse response = new AppResponse();
        response.setGrossAnnual(breakdown.getGrossAnnual());
        response.setGrossAnnual(breakdown.getGrossAnnual());
        response.setTaxableIncome(breakdown.getTaxableIncome());
        response.setTaxFreeAllowance(breakdown.getTaxFreeAllowance());
        response.setIncomeTax(breakdown.getBasicTax());
        response.setNationalInsurance(breakdown.getNationalInsurance());
        response.setStudentLoanRepayment(breakdown.getStudentLoanRepayment());
        response.setPensionContribution(breakdown.getPensionContribution());
        response.setSalarySacrifice(breakdown.getSalarySacrifice());
        response.setGiftAid(breakdown.getGiftAid());
        response.setProfessionalSubscription(breakdown.getProfessionalSubscriptions());
        response.setOther(breakdown.getOtherPreTaxDeductions());
        response.setTotalPreTaxDeductions(breakdown.getTotalPreTaxDeductions());
        response.setTotalDeductions(breakdown.getTotalDeductions());
        response.setNetAnnual(breakdown.getNetAnnual());
        response.setEffectiveTaxRate(breakdown.getEffectiveTaxRate());
        response.setGrossPerPeriod(grossPerPeriod);
        response.setNetPerPeriod(netPerPeriod);
        response.setPayPeriod(request.getPayPeriod());

        return response;
    }
}