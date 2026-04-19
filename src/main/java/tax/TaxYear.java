
/**
 * TaxYear class defines the tax rules and thresholds for a specific tax year.
 * It provides constants for personal allowance, tax rate limits, and tax rates.
 */
package tax;

import java.math.BigDecimal;

public class TaxYear {
    private final BigDecimal PERSONAL_ALLOWANCE = BigDecimal.valueOf(12570); // TODO: Use actual tax year data
    private final BigDecimal BASIC_RATE_LIMIT = BigDecimal.valueOf(50270); // TODO: Use actual tax year data
    private final BigDecimal HIGHER_RATE_LIMIT = BigDecimal.valueOf(150000); // TODO: Use actual tax year data
    private final BigDecimal BASIC_RATE = BigDecimal.valueOf(0.20);
    private final BigDecimal HIGHER_RATE = BigDecimal.valueOf(0.40);
    private final BigDecimal ADDITIONAL_RATE = BigDecimal.valueOf(0.45);

    private static final TaxYear taxYear = new TaxYear(); 

    public TaxYear() {
    }

    public static TaxYear getTaxYear() {
        return taxYear;
    }

    public BigDecimal getPersonalAllowance(){
        return PERSONAL_ALLOWANCE;
    }

    public BigDecimal getBasicRateLimit(){
        return BASIC_RATE_LIMIT;
    }

    public BigDecimal getHigherRateLimit(){
        return HIGHER_RATE_LIMIT;
    }

    public BigDecimal getBasicRate(){
        return BASIC_RATE;
    }

    public BigDecimal getHigherRate(){
        return HIGHER_RATE;
    }

    public BigDecimal getAdditionalRate(){
        return ADDITIONAL_RATE;
    }

}