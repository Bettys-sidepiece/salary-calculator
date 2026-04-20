package repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repository interface for accessing tax year data from the database
@Repository
public interface TaxYearRepo extends JpaRepository<TaxYearEntity, Long> {
    Optional<TaxYearEntity> findByLabel(String label);
}
