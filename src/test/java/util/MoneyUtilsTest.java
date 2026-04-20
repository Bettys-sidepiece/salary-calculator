package util;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class MoneyUtilsTest {

    @Test
    void scale_roundsHalfUp() {
        assertEquals(new BigDecimal("1.01"), MoneyUtils.scale(new BigDecimal("1.005")));
    }

    @Test
    void scale_roundsDown() {
        assertEquals(new BigDecimal("1.00"), MoneyUtils.scale(new BigDecimal("1.004")));
    }

    @Test
    void scale_alreadyTwoDecimalPlaces_unchanged() {
        assertEquals(new BigDecimal("1.00"), MoneyUtils.scale(new BigDecimal("1.000")));
    }

    @Test
    void scale_roundsUpToNextWhole() {
        assertEquals(new BigDecimal("2.00"), MoneyUtils.scale(new BigDecimal("1.999")));
    }

    @Test
    void scale_zero_returnsZero() {
        assertEquals(new BigDecimal("0.00"), MoneyUtils.scale(BigDecimal.ZERO));
    }

    @Test
    void scale_negative_roundsCorrectly() {
        assertEquals(new BigDecimal("-1.01"), MoneyUtils.scale(new BigDecimal("-1.005")));
    }

    @Test
    void scale_null_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> MoneyUtils.scale(null));
    }
}
