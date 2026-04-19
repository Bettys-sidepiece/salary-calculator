package tax;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Represents a UK Tax Year with full tax policy:
 * - Personal allowance (with taper)
 * - Income tax bands
 *
 * This class is immutable and acts as a rules provider.
 */
public final class TaxYear {
    private static final Logger logger = LoggerFactory.getLogger(TaxYear.class);

    private final BigDecimal personalAllowance;
    private final BigDecimal basicRateLimit;
    private final BigDecimal higherRateLimit;

    private final BigDecimal basicRate;
    private final BigDecimal higherRate;
    private final BigDecimal additionalRate;

    private final BigDecimal allowanceWithdrawalThreshold;
    private final BigDecimal allowanceWithdrawalRate;

    /**
     * Static factory for current UK tax year (2026/27)
     */
    public static TaxYear uk2026() {
        return new TaxYear(
            bd(12570),
            bd(50270),
            bd(125140),
            bd("0.20"),
            bd("0.40"),
            bd("0.45"),
            bd(100000),
            bd("0.50")
        );
    }

    public TaxYear(
            BigDecimal personalAllowance,
            BigDecimal basicRateLimit,
            BigDecimal higherRateLimit,
            BigDecimal basicRate,
            BigDecimal higherRate,
            BigDecimal additionalRate,
            BigDecimal allowanceWithdrawalThreshold,
            BigDecimal allowanceWithdrawalRate) {

        this.personalAllowance = requireNonNegative(personalAllowance, "Personal allowance");
        this.basicRateLimit = requireNonNegative(basicRateLimit, "Basic rate limit");
        this.higherRateLimit = requireNonNegative(higherRateLimit, "Higher rate limit");

        this.basicRate = requireRate(basicRate, "Basic rate");
        this.higherRate = requireRate(higherRate, "Higher rate");
        this.additionalRate = requireRate(additionalRate, "Additional rate");

        this.allowanceWithdrawalThreshold =
                requireNonNegative(allowanceWithdrawalThreshold, "Allowance withdrawal threshold");

        this.allowanceWithdrawalRate =
                requireRate(allowanceWithdrawalRate, "Allowance withdrawal rate");

        if (basicRateLimit.compareTo(higherRateLimit) >= 0) {
            throw new IllegalArgumentException("Basic rate limit must be less than higher rate limit");
        }
    }

    /**
     * Calculates personal allowance after tapering.
     *
     * Rules:
     * - <= 100k → full allowance
     * - 100k–125,140 → reduced £1 per £2
     * - >= 125,140 → zero
     */
    public BigDecimal calculatePersonalAllowance(BigDecimal adjustedIncome) {
        Objects.requireNonNull(adjustedIncome, "Adjusted income cannot be null");

        if (adjustedIncome.compareTo(allowanceWithdrawalThreshold) <= 0) {
            logger.debug("Income {} below withdrawal threshold, using full allowance: {}", 
                adjustedIncome, personalAllowance);
            return scale(personalAllowance);
        }

        BigDecimal excess = adjustedIncome.subtract(allowanceWithdrawalThreshold);
        BigDecimal reduction = excess.multiply(allowanceWithdrawalRate);
        BigDecimal tapered = personalAllowance.subtract(reduction);
        BigDecimal result = scale(tapered.max(BigDecimal.ZERO));

        logger.debug("Income {} exceeds withdrawal threshold. Reduction: {}, Final allowance: {}", 
            adjustedIncome, reduction, result);
        
        return result;
    }

    /**
     * Returns ordered income tax bands.
     */
    public List<TaxBand> getIncomeTaxBands() {
        logger.debug("Generating tax bands - Basic: {}%, Higher: {}%, Additional: {}%",
            basicRate.multiply(BigDecimal.valueOf(100)).intValue(),
            higherRate.multiply(BigDecimal.valueOf(100)).intValue(),
            additionalRate.multiply(BigDecimal.valueOf(100)).intValue());
        
        return List.of(
            new TaxBand(BigDecimal.ZERO, basicRateLimit, basicRate),
            new TaxBand(basicRateLimit, higherRateLimit, higherRate),
            new TaxBand(higherRateLimit, null, additionalRate)
        );
    }

    public BigDecimal getPersonalAllowance() {
        return personalAllowance;
    }

    public BigDecimal getBasicRateLimit() {
        return basicRateLimit;
    }

    public BigDecimal getHigherRateLimit() {
        return higherRateLimit;
    }

    public BigDecimal getBasicRate() {
        return basicRate;
    }

    public BigDecimal getHigherRate() {
        return higherRate;
    }

    public BigDecimal getAdditionalRate() {
        return additionalRate;
    }

    public BigDecimal getAllowanceWithdrawalThreshold() {
        return allowanceWithdrawalThreshold;
    }

    public BigDecimal getAllowanceWithdrawalRate() {
        return allowanceWithdrawalRate;
    }

    // ------------------------
    // Helpers
    // ------------------------

    private static BigDecimal requireNonNegative(BigDecimal value, String name) {
        Objects.requireNonNull(value, name + " cannot be null");
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(name + " cannot be negative");
        }
        return value;
    }

    private static BigDecimal requireRate(BigDecimal value, String name) {
        Objects.requireNonNull(value, name + " cannot be null");
        if (value.compareTo(BigDecimal.ZERO) < 0 || value.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException(name + " must be between 0 and 1");
        }
        return value;
    }

    private static BigDecimal bd(double value) {
        return BigDecimal.valueOf(value);
    }

    private static BigDecimal bd(String value) {
        return new BigDecimal(value);
    }

    private static BigDecimal scale(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}