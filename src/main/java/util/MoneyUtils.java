package util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MoneyUtils {

    private static final int SCALE = 2; // 2 decimal places for currency

    public static BigDecimal scale(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        return amount.setScale(SCALE, RoundingMode.HALF_UP);
    }
}