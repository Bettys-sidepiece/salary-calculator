package tax;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TaxYearTest {

    private TaxYear taxYear;

    @BeforeEach
    void setUp() {
        taxYear = TaxYear.uk2026();
    }

     @Test
    void personalAllowance_belowThreshold_returnsFullAllowance() {
        BigDecimal result = taxYear.calculatePersonalAllowance(BigDecimal.valueOf(99999));
        assertEquals(new BigDecimal("12570.00"), result);
    }

    @Test
    void personalAllowance_atThreshold_returnsFullAllowance() {
        BigDecimal result = taxYear.calculatePersonalAllowance(BigDecimal.valueOf(100000));
        assertEquals(new BigDecimal("12570.00"), result);
    }

    @Test
    void personalAllowance_aboveThreshold_isTapered() {
        // £110,000 = £10,000 over threshold → lose £5,000 → £7,570
        BigDecimal result = taxYear.calculatePersonalAllowance(BigDecimal.valueOf(110000));
        assertEquals(new BigDecimal("7570.00"), result);
    }

    @Test
    void personalAllowance_atWithdrawalPoint_returnsZero() {
        // £125,140+ → zero allowance
        BigDecimal result = taxYear.calculatePersonalAllowance(BigDecimal.valueOf(125140));
        assertEquals(BigDecimal.ZERO.setScale(2), result);
    }

    //Null and negative tests
    @Test
    void personalAllowance_nullIncome_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
            taxYear.calculatePersonalAllowance(null));
    }

    @Test
    void personalAllowance_negativeIncome_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
            taxYear.calculatePersonalAllowance(BigDecimal.valueOf(-1)));
    }

    @Test
    void personalAllowance_aboveWithdrawalPoint_returnsZero() {
        // well above £125,140 - should still be zero not negative
        BigDecimal result = taxYear.calculatePersonalAllowance(BigDecimal.valueOf(200000));
        assertEquals(BigDecimal.ZERO.setScale(2), result);
    }

}
