package infrastructure.repository;

import domain.model.CarConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface JpaCarConfigurationRepository extends JpaRepository<CarConfiguration, UUID>, JpaSpecificationExecutor<CarConfiguration> {
}
