package repo;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * label
    personal_allowance
    basic_rate_threshold
    higher_rate_threshold
    basic_rate
    higher_rate,
    additional_rate,
    allowance_withdrawal_threshold,
    allowance_withdrawal_rate
 */
@Entity // Marks this class as a JPA entity
@Table(name = "tax_year")// Specifies the table name in the database
public class TaxYearEntity {
    // Fields
    @Id // Marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String label;

    @Column(nullable = false)
    private BigDecimal personalAllowance;

    @Column(nullable = false)
    private BigDecimal basicRateThreshold;

    @Column(nullable = false)
    private BigDecimal higherRateThreshold;

    @Column(nullable = false)
    private BigDecimal basicRate;

    @Column(nullable = false)
    private BigDecimal higherRate;

    @Column(nullable = false)
    private BigDecimal additionalRate;

    @Column(nullable = false)
    private BigDecimal allowanceWithdrawalThreshold;

    @Column(nullable = false)
    private BigDecimal allowanceWithdrawalRate;

    
    //Getters
    public Long getId(){return id;}
    public String getLabel() { return label; }
    public BigDecimal getPersonalAllowance() { return personalAllowance; }
    public BigDecimal getBasicRateThreshold() { return basicRateThreshold; }
    public BigDecimal getHigherRateThreshold() { return higherRateThreshold; }
    public BigDecimal getBasicRate() { return basicRate; }
    public BigDecimal getHigherRate() { return higherRate; }
    public BigDecimal getAdditionalRate() { return additionalRate; }
    public BigDecimal getAllowanceWithdrawalThreshold() { return allowanceWithdrawalThreshold; }
    public BigDecimal getAllowanceWithdrawalRate() { return allowanceWithdrawalRate; }

}
