package salary.calculator.service;

import org.springframework.stereotype.Service;

import repo.TaxYearEntity;
import repo.TaxYearRepo;
import tax.TaxYear;

@Service
public class TaxYearService {

    // Repository for accessing tax year data
    private final TaxYearRepo taxYearRepo;

    // Constructor injection of the repository
    public TaxYearService(TaxYearRepo taxYearRepo) {
        this.taxYearRepo = taxYearRepo;
    }

    // Method to retrieve a TaxYear by its label
    public TaxYear getByLabel(String label) {
        TaxYearEntity entity = taxYearRepo.findByLabel(label)
            .orElseThrow(() -> new IllegalArgumentException("Tax year not found: " + label));

        return mapToTaxYear(entity);
    }

    /**
     * Helper method to convert a TaxYearEntity to a TaxYear domain model
     * @param entity the TaxYearEntity to convert
     * @return the corresponding TaxYear domain model
     */
    private TaxYear mapToTaxYear(TaxYearEntity entity) {
        return new TaxYear(
            entity.getPersonalAllowance(),
            entity.getBasicRateThreshold(),
            entity.getHigherRateThreshold(),
            entity.getBasicRate(),
            entity.getHigherRate(),
            entity.getAdditionalRate(),
            entity.getAllowanceWithdrawalThreshold(),
            entity.getAllowanceWithdrawalRate()
        );
    }

    /**
     * Retrieves the most recent tax year (ordered by label descending)
     * @return the TaxYear with the highest label (most recent)
     */
    public TaxYear findMostRecent() {
        TaxYearEntity entity = taxYearRepo.findFirstByOrderByLabelDesc()
            .orElseThrow(() -> new IllegalArgumentException("No tax years found in database"));
        
        return mapToTaxYear(entity);
    }

    public TaxYearEntity getTaxYearEntityMostRecent() {
        return taxYearRepo.findFirstByOrderByLabelDesc()
            .orElseThrow(() -> new IllegalArgumentException("No tax years found in database"));
    }
}
