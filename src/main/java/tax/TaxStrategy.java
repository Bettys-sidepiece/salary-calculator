/*
 * This interface defines the contract for tax calculation strategies.
 * It allows for different implementations of tax calculation logic, enabling flexibility and extensibility.
 * Each implementation will take an IncomeProfile and return a TaxBreakdown based on the specific tax rules it applies.
 */
package tax;

import model.IncomeProfile;
import model.TaxBreakdown;

public interface TaxStrategy {
    TaxBreakdown calculateTax(IncomeProfile incomeProfile);
}