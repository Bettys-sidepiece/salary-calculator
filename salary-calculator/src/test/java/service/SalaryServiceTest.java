import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SalaryServiceTest {

    @Test
    void testCalculateHourlySalary() {
        SalaryService salaryService = new SalaryService();
        double hourlyRate = 20.0;
        int hoursWorked = 40;
        double expectedSalary = 800.0;
        assertEquals(expectedSalary, salaryService.calculateHourlySalary(hourlyRate, hoursWorked));
    }

    @Test
    void testCalculateSalariedSalary() {
        SalaryService salaryService = new SalaryService();
        double annualSalary = 52000.0;
        double expectedMonthlySalary = 4333.33;
        assertEquals(expectedMonthlySalary, salaryService.calculateSalariedSalary(annualSalary), 0.01);
    }

    @Test
    void testCalculateSalaryWithNegativeHours() {
        SalaryService salaryService = new SalaryService();
        double hourlyRate = 20.0;
        int hoursWorked = -5;
        assertThrows(IllegalArgumentException.class, () -> {
            salaryService.calculateHourlySalary(hourlyRate, hoursWorked);
        });
    }

    @Test
    void testCalculateSalaryWithZeroHourlyRate() {
        SalaryService salaryService = new SalaryService();
        double hourlyRate = 0.0;
        int hoursWorked = 40;
        assertEquals(0.0, salaryService.calculateHourlySalary(hourlyRate, hoursWorked));
    }
}