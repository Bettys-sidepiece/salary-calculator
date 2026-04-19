package tax;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class NationalInsuranceContributions  {
    private static final Logger logger = LoggerFactory.getLogger(NationalInsuranceContributions.class);

    private final BigDecimal primaryThreshold;
    private final BigDecimal upperEarningsLimit;

    private final BigDecimal mainRate;
    private final BigDecimal additionalRate;

    public static NationalInsuranceContributions uk2026() {
        return new NationalInsuranceContributions(
            bd(12570),
            bd(50270),
            bd("0.12"),
            bd("0.02")
        );
    }

    public NationalInsuranceContributions(
            BigDecimal primaryThreshold,
            BigDecimal upperEarningsLimit,
            BigDecimal mainRate,
            BigDecimal additionalRate) {

        this.primaryThreshold = requireNonNegative(primaryThreshold, "Primary threshold");
        this.upperEarningsLimit = requireNonNegative(upperEarningsLimit, "Upper earnings limit");

        this.mainRate = requireRate(mainRate, "Main rate");
        this.additionalRate = requireRate(additionalRate, "Additional rate");

        if (primaryThreshold.compareTo(upperEarningsLimit) >= 0) {
            throw new IllegalArgumentException("Primary threshold must be less than upper earnings limit");
        }
    }

    /**
     * Calculates Class 1 employee NICs (annualised).
     */
    public BigDecimal calculate(BigDecimal grossAnnual) {
        Objects.requireNonNull(grossAnnual, "Income cannot be null");

        logger.debug("Calculating NI for gross: {}", grossAnnual);

        if (grossAnnual.compareTo(primaryThreshold) <= 0) {
            logger.debug("Income {} below primary threshold {}, NI = £0", grossAnnual, primaryThreshold);
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal ni = BigDecimal.ZERO;

        // Main band (PT → UEL)
        BigDecimal mainBandIncome =
                grossAnnual.min(upperEarningsLimit).subtract(primaryThreshold);

        if (mainBandIncome.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal mainBandNI = mainBandIncome.multiply(mainRate);
            logger.debug("Main band NI: {} × {} = {}", mainBandIncome, mainRate, mainBandNI);
            ni = ni.add(mainBandNI);
        }

        // Additional band (above UEL)
        if (grossAnnual.compareTo(upperEarningsLimit) > 0) {
            BigDecimal additionalIncome =
                    grossAnnual.subtract(upperEarningsLimit);

            BigDecimal additionalNI = additionalIncome.multiply(additionalRate);
            logger.debug("Additional band NI: {} × {} = {}", additionalIncome, additionalRate, additionalNI);
            ni = ni.add(additionalNI);
        }

        BigDecimal result = ni.setScale(2, RoundingMode.HALF_UP);
        logger.debug("Total NI: {}", result);
        return result;
    }

    // ------------------------
    // Helpers
    // ------------------------

    private static BigDecimal bd(double value) {
        return BigDecimal.valueOf(value);
    }

    private static BigDecimal bd(String value) {
        return new BigDecimal(value);
    }

    private static BigDecimal requireNonNegative(BigDecimal value, String name) {
        Objects.requireNonNull(value);
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(name + " cannot be negative");
        }
        return value;
    }

    private static BigDecimal requireRate(BigDecimal value, String name) {
        Objects.requireNonNull(value);
        if (value.compareTo(BigDecimal.ZERO) < 0 || value.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException(name + " must be between 0 and 1");
        }
        return value;
    }
}
