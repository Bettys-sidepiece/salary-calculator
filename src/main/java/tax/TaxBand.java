package tax;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents a single progressive tax band.
 * upper = null means no upper limit.
 */
public record TaxBand(
        BigDecimal lower,
        BigDecimal upper,
        BigDecimal rate) {

    public TaxBand {
        Objects.requireNonNull(lower, "Lower bound cannot be null");
        Objects.requireNonNull(rate, "Rate cannot be null");

        if (lower.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Lower bound cannot be negative");
        }

        if (upper != null && upper.compareTo(lower) <= 0) {
            throw new IllegalArgumentException("Upper bound must be greater than lower bound");
        }

        if (rate.compareTo(BigDecimal.ZERO) < 0 || rate.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("Rate must be between 0 and 1");
        }
    }
}